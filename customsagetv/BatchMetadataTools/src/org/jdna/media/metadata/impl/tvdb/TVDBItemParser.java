package org.jdna.media.metadata.impl.tvdb;

import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.MediaArt;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.url.IUrl;
import org.jdna.url.UrlFactory;
import org.jdna.util.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sagex.phoenix.fanart.FanartUtil.MediaArtifactType;

public class TVDBItemParser {
    private static final Logger    log                 = Logger.getLogger(TVDBItemParser.class);
    private static final Pattern   yearPattern         = Pattern.compile("([0-9]{4})");

    public static final String     BANNERS_URL         = "http://www.thetvdb.com/api/{0}/series/{1}/banners.xml";
    public static final String     ACTORS_URL          = "http://www.thetvdb.com/api/{0}/series/{1}/actors.xml";
    public static final String     SERIES_URL          = "http://www.thetvdb.com/api/{0}/series/{1}";
    public static final String     SEASON_EPISODE_URL  = "http://www.thetvdb.com/api/{0}/series/{1}/default/{2}/{3}";

    // Date format is YYYY-MM-DD
    public static final String     EPISODE_BY_DATE_URL = "http://thetvdb.com/api/GetEpisodeByAirDate.php?apikey={0}&seriesid={1}&airdate={2}";
    public static final String     EPISODE_BY_TITLE    = "http://www.thetvdb.com/api/{0}/series/{1}/all/en.xml";
    public static final String     FANART_URL          = "http://www.thetvdb.com/banners/{0}";

    private String                 origUrl;
    private String                 seriesId            = null;
    private Map<String, String>    queryArgs           = null;

    private MediaMetadata          md                  = null;
    private DocumentBuilderFactory factory             = DocumentBuilderFactory.newInstance();
    private Document               banners             = null;


    public TVDBItemParser(String metadataResultUrl) {
        this.origUrl = metadataResultUrl;
        MediaSearchResult sr = new MediaSearchResult();
        sr.setUrlWithExtraArgs(metadataResultUrl);
        this.seriesId = sr.getUrl();
        this.queryArgs = sr.getExtraArgs();
    }

    public IMediaMetadata getMetadata() {
        if (md == null) {
            try {
                // parse and fill
                md = new MediaMetadata();

                // TODO: Cache the series info in memory, since it may be likely
                // that we are going to
                // need it again very soon.
                addSeriesInfo(md);
                addActors(md);
                addBanners(md, null);

                if (queryArgs != null) {
                    String season = queryArgs.get(SearchQuery.Field.SEASON.name());
                    // add in season fanart
                    addBanners(md, season);

                    // now add in episode specific fanart
                    if (!StringUtils.isEmpty(queryArgs.get(SearchQuery.Field.SEASON.name())) && !StringUtils.isEmpty(queryArgs.get(SearchQuery.Field.EPISODE.name()))) {
                        addSeasonEpisodeInfo(md, queryArgs.get(SearchQuery.Field.SEASON.name()), queryArgs.get(SearchQuery.Field.EPISODE.name()));
                    } else if (!StringUtils.isEmpty(queryArgs.get(SearchQuery.Field.EPISODE_DATE.name()))) {
                        addSeasonEpisodeInfoByDate(md, queryArgs.get(SearchQuery.Field.EPISODE_DATE.name()));
                    } else if (!StringUtils.isEmpty(queryArgs.get(SearchQuery.Field.EPISODE_TITLE.name()))) {
                        addSeasonEpisodeInfoByTitle(md, queryArgs.get(SearchQuery.Field.EPISODE_TITLE.name()));
                    } else {
                        log.warn("No Specific Episode Lookup for query: " + origUrl);
                    }
                }

                md.setProviderDataUrl(origUrl);
                md.setProviderId(TVDBMetadataProvider.PROVIDER_ID);
                md.setProviderDataId(new MetadataID("tvdb", seriesId));
            } catch (Exception e) {
                log.error("Failed while parsing series: " + origUrl, e);
                md = null;
            }
        }
        
        if (md!=null) {
            log.debug("Metadata Contains: " + md.getCastMembers(ICastMember.ACTOR).length + " actors");
        }
        
        return md;
    }

    private void updateMetadataFromUrl(MediaMetadata md, String episodeUrl) throws Exception {
        DocumentBuilder parser = factory.newDocumentBuilder();
        log.debug("Parsing TVDB Episode url: " + episodeUrl);
        IUrl url = UrlFactory.newUrl(episodeUrl);
        Document doc = parser.parse(url.getInputStream(null, true));

        Element el = DOMUtils.getElementByTagName(doc.getDocumentElement(), "Episode");
        updateMetadataFromElement(md, el);
    }
    
    private void updateMetadataFromElement(MediaMetadata md, Element el) {
        md.set(MetadataKey.SEASON, DOMUtils.getElementValue(el, "SeasonNumber"));
        md.set(MetadataKey.EPISODE, DOMUtils.getElementValue(el, "EpisodeNumber"));
        md.set(MetadataKey.EPISODE_TITLE, DOMUtils.getElementValue(el, "EpisodeName"));
        md.set(MetadataKey.RELEASE_DATE, DOMUtils.getElementValue(el, "FirstAired"));
        md.set(MetadataKey.YEAR, parseYear(DOMUtils.getElementValue(el, "FirstAired")));
        md.set(MetadataKey.DESCRIPTION, DOMUtils.getElementValue(el, "Overview"));
        md.set(MetadataKey.USER_RATING, DOMUtils.getElementValue(el, "Rating"));

        addCastMember(md, DOMUtils.getElementValue(el, "GuestStars"), "Guest", ICastMember.ACTOR);
        addCastMember(md, DOMUtils.getElementValue(el, "Writer"), "Writer", ICastMember.WRITER);
        addCastMember(md, DOMUtils.getElementValue(el, "Director"), "Director", ICastMember.DIRECTOR);
    }
    
    private void addCastMember(MediaMetadata md, String strSplit, String part, int type) {
        if (!StringUtils.isEmpty(strSplit)) {
            String directorsArr[] = strSplit.split("[,\\|]");
            for (String d : directorsArr) {
                if (!StringUtils.isEmpty(d)) {
                    CastMember cm = new CastMember(type);
                    cm.setName(d.trim());
                    cm.setPart(part);
                    md.addCastMember(cm);
                    log.debug("Adding Cast Member: " + cm.getName());
                }
            }
        }
    }

    private void addSeasonEpisodeInfoByDate(MediaMetadata md, String date) {
        try {
            updateMetadataFromUrl(md, MessageFormat.format(EPISODE_BY_DATE_URL, TVDBMetadataProvider.getApiKey(), seriesId, date));
        } catch (Exception e) {
            log.error("Failed to get season/episode specific information for " + seriesId + "; Date: " + date, e);
        }

    }

    private void addSeasonEpisodeInfoByTitle(MediaMetadata md, String title) {
        try {
            DocumentBuilder parser = factory.newDocumentBuilder();
            String allurl = MessageFormat.format(EPISODE_BY_TITLE, TVDBMetadataProvider.getApiKey(), seriesId); 
            log.debug("Parsing TVDB Complete Episode Info: " + allurl);
            IUrl url = UrlFactory.newUrl(allurl);
            Document doc = parser.parse(url.getInputStream(null, true));
            
            NodeList nl = doc.getElementsByTagName("Episode");
            boolean updated = updateIfScored(nl, title, 1.0f);
            if (!updated) {
                float matchScore = 0.8f;
                log.debug("Couldn't find an exact title match, so using a fuzzy match score of " + matchScore);

                // do another search, this time use a less sensitive matching criteria
                updated = updateIfScored(nl, title, matchScore);
            }
            
            if (!updated) {
                log.info("Unable to match a direct title for: " + title);
            }
        } catch (Exception e) {
            log.error("Failed to find a match based on title: " + title);
        }
    }
    
    private boolean updateIfScored(NodeList nl, String title, float scoreToMatch) {
        boolean updated=false;
        int s = nl.getLength();
        for (int i=0;i<s;i++) {
            Element el = (Element) nl.item(i);
            String epTitle = DOMUtils.getElementValue(el, "EpisodeName");
            float score = MetadataUtil.calculateCompressedScore(title, epTitle);
            
            if (score>=scoreToMatch) {
                log.debug("Found a title match: " + epTitle + "; Updating Metadata.");
                updateMetadataFromElement(md, el);
                updated=true;
                break;
            }
        }
        return updated;
    }

    
    private void addSeasonEpisodeInfo(MediaMetadata md, String season, String episode) {
        int inSeason = NumberUtils.toInt(season, -1);
        int inEpisode = NumberUtils.toInt(episode, -1);

        try {
            updateMetadataFromUrl(md, MessageFormat.format(SEASON_EPISODE_URL, TVDBMetadataProvider.getApiKey(), seriesId, inSeason, inEpisode));
        } catch (Exception e) {
            log.error("Failed to get season/episode specific information for " + seriesId + "; Season: " + season + "; episode: " + episode, e);
        }

    }

    private void addBanners(MediaMetadata md, String season) {
        int inSeason = NumberUtils.toInt(season, -9);
        try {
            if (banners == null) {
                DocumentBuilder parser = factory.newDocumentBuilder();
                String seriesUrl = MessageFormat.format(BANNERS_URL, TVDBMetadataProvider.getApiKey(), seriesId);
                log.debug("Parsing TVDB Banners url: " + seriesUrl);
                IUrl url = UrlFactory.newUrl(seriesUrl);
                banners = parser.parse(url.getInputStream(null, true));
            }

            NodeList nl = banners.getElementsByTagName("Banner");
            for (int i = 0; i < nl.getLength(); i++) {
                Element el = (Element) nl.item(i);

                // TODO: Use a setting for the preferred language
                String lang = DOMUtils.getElementValue(el, "Language");
                if (StringUtils.isEmpty(lang) || lang.equals("en")) {
                    String type = DOMUtils.getElementValue(el, "BannerType");
                    MediaArt ma = null;
                    if (season == null) {
                        if ("fanart".equals(type)) {
                            ma = new MediaArt();
                            ma.setType(MediaArtifactType.BACKGROUND);
                        } else if ("poster".equals(type)) {
                            ma = new MediaArt();
                            ma.setType(MediaArtifactType.POSTER);
                        } else if ("series".equals(type)) {
                            ma = new MediaArt();
                            ma.setType(MediaArtifactType.BANNER);
                        } else if ("season".equals(type)) {
                        } else {
                            log.debug("Unhandled Banner Type: " + type);
                        }

                    } else {
                        if ("fanart".equals(type)) {
                        } else if ("poster".equals(type)) {
                        } else if ("series".equals(type)) {
                        } else if ("season".equals(type)) {
                            int seasonNum = DOMUtils.getElementIntValue(el, "Season");
                            if (seasonNum == inSeason) {
                                String type2 = DOMUtils.getElementValue(el, "BannerType2");
                                if ("season".equals(type2)) {
                                    ma = new MediaArt();
                                    ma.setType(MediaArtifactType.POSTER);
                                } else if ("seasonwide".equals(type2)) {
                                    ma = new MediaArt();
                                    ma.setType(MediaArtifactType.BANNER);
                                } else {
                                    log.debug("Unhandled Season Banner Type2: " + type2);
                                }
                                if (ma != null) {
                                    ma.setSeason(seasonNum);
                                }
                            }
                        } else {
                            log.debug("Unhandled Season Banner Type: " + type);
                        }
                    }

                    if (ma != null) {
                        addFanartUrl(md, ma, DOMUtils.getElementValue(el, "BannerPath"));
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Unable to process banners for series: " + seriesId, e);
        }
    }

    private void addActors(MediaMetadata md2) {
        try {
            DocumentBuilder parser = factory.newDocumentBuilder();
            String seriesUrl = MessageFormat.format(ACTORS_URL, TVDBMetadataProvider.getApiKey(), seriesId);
            log.debug("Parsing TVDB Actors url: " + seriesUrl);
            IUrl url = UrlFactory.newUrl(seriesUrl);
            Document doc = parser.parse(url.getInputStream(null, true));

            NodeList nl = doc.getElementsByTagName("Actor");
            for (int i = 0; i < nl.getLength(); i++) {
                CastMember cm = new CastMember(ICastMember.ACTOR);
                Element e = (Element) nl.item(i);
                cm.setName(DOMUtils.getElementValue(e, "Name"));
                cm.setPart(DOMUtils.getElementValue(e, "Role"));
                log.debug("Adding Actor: " + cm.getName());
                md2.addCastMember(cm);

                NodeList imgs = e.getElementsByTagName("Image");
                for (int j = 0; j < imgs.getLength(); j++) {
                    log.debug("Adding actor fanart: " + imgs.item(j).getTextContent());
                    cm.addFanart(imgs.item(j).getTextContent());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to process the Actors for series: " + seriesId, e);
        }
    }

    private void addSeriesInfo(MediaMetadata md) throws Exception {
        DocumentBuilder parser = factory.newDocumentBuilder();
        String seriesUrl = MessageFormat.format(SERIES_URL, TVDBMetadataProvider.getApiKey(), seriesId);
        log.debug("Parsing TVDB Series url: " + seriesUrl);
        IUrl url = UrlFactory.newUrl(seriesUrl);
        Document doc = parser.parse(url.getInputStream(null, true));

        Element series = DOMUtils.getElementByTagName(doc.getDocumentElement(), "Series");
        md.set(MetadataKey.MPAA_RATING, DOMUtils.getElementValue(series, "ContentRating"));
        md.set(MetadataKey.RELEASE_DATE, DOMUtils.getElementValue(series, "FirstAired"));
        md.set(MetadataKey.YEAR, parseYear(DOMUtils.getElementValue(series, "FirstAired")));

        String genres = DOMUtils.getElementValue(series, "Genre");
        if (!StringUtils.isEmpty(genres)) {
            for (String g : genres.split("[,\\|]")) {
                if (!StringUtils.isEmpty(g)) {
                    md.addGenre(g.trim());
                }
            }
        }

        md.set(MetadataKey.DESCRIPTION, DOMUtils.getElementValue(series, "Overview"));
        md.set(MetadataKey.USER_RATING, DOMUtils.getElementValue(series, "Rating"));
        md.set(MetadataKey.RUNNING_TIME, convertTimeToMillissecondsForSage(DOMUtils.getElementValue(series, "Runtime")));
        md.set(MetadataKey.MEDIA_TITLE, DOMUtils.getElementValue(series, "SeriesName"));
        md.set(MetadataKey.DISPLAY_TITLE, DOMUtils.getElementValue(series, "SeriesName"));
        md.set(MetadataKey.MEDIA_TYPE, MetadataUtil.TV_MEDIA_TYPE);
    }

    private void addFanartUrl(MediaMetadata md, MediaArt ma, String path) {
        if (StringUtils.isEmpty(path)) return;
        ma.setDownloadUrl(MessageFormat.format(FANART_URL, path));
        md.addMediaArt(ma);
    }

    private String parseYear(String releaseDate) {
        if (releaseDate == null) return null;
        Matcher m = yearPattern.matcher(releaseDate);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private String convertTimeToMillissecondsForSage(String time) {
        return String.valueOf(NumberUtils.toLong(time) * 60 * 1000);
    }

    public String getTheMovieDBID() {
        return seriesId;
    }
}
