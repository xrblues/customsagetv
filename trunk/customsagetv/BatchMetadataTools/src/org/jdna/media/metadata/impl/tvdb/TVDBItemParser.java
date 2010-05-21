package org.jdna.media.metadata.impl.tvdb;

import java.text.MessageFormat;
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
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.url.IUrl;
import org.jdna.url.UrlFactory;
import org.jdna.util.DOMUtils;
import org.jdna.util.Pair;
import org.jdna.util.ParserUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaArtifactType;

public class TVDBItemParser {
    private static final Logger    log                 = Logger.getLogger(TVDBItemParser.class);
    private static final Pattern   yearPattern         = Pattern.compile("([0-9]{4})");

    public static final String     BANNERS_URL         = "http://www.thetvdb.com/api/{0}/series/{1}/banners.xml";
    public static final String     ACTORS_URL          = "http://www.thetvdb.com/api/{0}/series/{1}/actors.xml";
    public static final String     SERIES_URL          = "http://www.thetvdb.com/api/{0}/series/{1}/{2}.xml";
    public static final String     SEASON_EPISODE_URL  = "http://www.thetvdb.com/api/{0}/series/{1}/default/{2}/{3}/{4}.xml";

    // Date format is YYYY-MM-DD
    public static final String     EPISODE_BY_DATE_URL = "http://thetvdb.com/api/GetEpisodeByAirDate.php?apikey={0}&seriesid={1}&airdate={2}";
    public static final String     EPISODE_BY_TITLE    = "http://www.thetvdb.com/api/{0}/series/{1}/all/{2}.xml";
    public static final String     FANART_URL          = "http://www.thetvdb.com/banners/{0}";

    private MediaMetadata          md                  = null;
    private DocumentBuilderFactory factory             = DocumentBuilderFactory.newInstance();
    private Document               banners             = null;
    private TVDBConfiguration config = null;

    private IMetadataSearchResult result = null;

    public TVDBItemParser(IMetadataSearchResult result) {
        this.result=result;
        config = GroupProxy.get(TVDBConfiguration.class);
    }

    public IMediaMetadata getMetadata() {
        if (md == null) {
            try {
                log.debug("Getting Metadata for Result: " + result);
                
                // parse and fill
                md = new MediaMetadata();

                // update with the query args, and then overwrite if needed
                MetadataAPI.setSeason(md, result.getExtra().get(SearchQuery.Field.SEASON.name()));
                MetadataAPI.setEpisode(md, result.getExtra().get(SearchQuery.Field.EPISODE.name()));
                MetadataAPI.setDisc(md, result.getExtra().get(SearchQuery.Field.DISC.name()));
                MetadataAPI.setReleaseDate(md, result.getExtra().get(SearchQuery.Field.EPISODE_DATE.name()));
                
                // TODO: Cache the series info in memory, since it may be likely
                // that we are going to
                // need it again very soon.
                addSeriesInfo(md);

                String season = result.getExtra().get(SearchQuery.Field.SEASON.name());
                String episode = result.getExtra().get(SearchQuery.Field.EPISODE.name());
                String date = result.getExtra().get(SearchQuery.Field.EPISODE_DATE.name());
                String title = result.getExtra().get(SearchQuery.Field.EPISODE_TITLE.name());
                
                if (!StringUtils.isEmpty(season) && !StringUtils.isEmpty(episode)) {
                    addSeasonEpisodeInfo(md, season, episode);
                }
                
                if (StringUtils.isEmpty(MetadataAPI.getEpisode(md)) && !StringUtils.isEmpty(date)) {
                    addSeasonEpisodeInfoByDate(md, date);
                }
                
                if (StringUtils.isEmpty(MetadataAPI.getEpisode(md)) && !StringUtils.isEmpty(title)) {
                    addSeasonEpisodeInfoByTitle(md, title);
                }

                if (StringUtils.isEmpty(MetadataAPI.getSeason(md))) {
                    throw new Exception("Cannot process TV without a valid season; Result: " + result);
                }
                
                if (StringUtils.isEmpty(MetadataAPI.getDisc(md))) {
                    // if it's not disc based, then check that we have a valid series info
                    if (StringUtils.isEmpty(MetadataAPI.getEpisodeTitle(md)) || StringUtils.isEmpty(MetadataAPI.getEpisode(md))) {
                        throw new Exception("Did not find a valid season and episode; Episode: " + episode + "; Search Date: " + date + "; Search Title: " + title);
                    }
                } else {
                    // TODO: gather all the episode titles and add them to the description
                    // TODO: set the episode title to be "23 episodes"
                }
                
                md.setProviderId(TVDBMetadataProvider.PROVIDER_ID);
                md.setProviderDataId(result.getId());

                // now add in banners and actors, no point in doing it early
                addActors(md);
                addBanners(md, season);
                addBanners(md, null);
            } catch (Exception e) {
                log.warn("Failed while parsing series: " + result, e);
                md = null;
            }
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
        md.set(MetadataKey.EPISODE_TITLE, org.jdna.util.StringUtils.unquote(DOMUtils.getElementValue(el, "EpisodeName")));
        // actually this is redundant because the tvdb is already YYYY-MM-DD, but this will
        // ensure that we are safe if out internal mask changes
        MetadataUtil.setReleaseDateFromFormattedDate(md, DOMUtils.getElementValue(el, "FirstAired"), "yyyy-MM-dd");
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
            updateMetadataFromUrl(md, MessageFormat.format(EPISODE_BY_DATE_URL, TVDBMetadataProvider.getApiKey(), result.getId(), date));
        } catch (Exception e) {
            log.warn("Failed to get season/episode specific information for " + result.getId() + "; Date: " + date, e);
        }

    }

    private void addSeasonEpisodeInfoByTitle(MediaMetadata md, String title) {
        try {
            DocumentBuilder parser = factory.newDocumentBuilder();
            String allurl = MessageFormat.format(EPISODE_BY_TITLE, TVDBMetadataProvider.getApiKey(), result.getId(), config.getLanguage()); 
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
            log.warn("Failed to find a match based on title: " + title);
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
            updateMetadataFromUrl(md, MessageFormat.format(SEASON_EPISODE_URL, TVDBMetadataProvider.getApiKey(), result.getId(), inSeason, inEpisode, config.getLanguage()));
        } catch (Exception e) {
            log.warn("Failed to get season/episode specific information for " + result.getId() + "; Season: " + season + "; episode: " + episode);
        }

    }

    private void addBanners(MediaMetadata md, String season) {
        int inSeason = NumberUtils.toInt(season, -9);
        try {
            if (banners == null) {
                DocumentBuilder parser = factory.newDocumentBuilder();
                String seriesUrl = MessageFormat.format(BANNERS_URL, TVDBMetadataProvider.getApiKey(), result.getId());
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
            log.warn("Unable to process banners for series: " + result, e);
        }
    }

    private void addActors(MediaMetadata md2) {
        try {
            DocumentBuilder parser = factory.newDocumentBuilder();
            String seriesUrl = MessageFormat.format(ACTORS_URL, TVDBMetadataProvider.getApiKey(), result.getId());
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
            log.warn("Failed to process the Actors for series: " + result, e);
        }
    }

    private void addSeriesInfo(MediaMetadata md) throws Exception {
        DocumentBuilder parser = factory.newDocumentBuilder();
        String seriesUrl = MessageFormat.format(SERIES_URL, TVDBMetadataProvider.getApiKey(), result.getId(), config.getLanguage());
        log.debug("Parsing TVDB Series url: " + seriesUrl);
        IUrl url = UrlFactory.newUrl(seriesUrl);
        Document doc = parser.parse(url.getInputStream(null, true));

        Element series = DOMUtils.getElementByTagName(doc.getDocumentElement(), "Series");
        md.set(MetadataKey.MPAA_RATING, DOMUtils.getElementValue(series, "ContentRating"));
        MetadataUtil.setReleaseDateFromFormattedDate(md, DOMUtils.getElementValue(series, "FirstAired"), "yyyy-MM-dd");
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
        md.set(MetadataKey.RUNNING_TIME, MetadataUtil.convertTimeToMillissecondsForSage(DOMUtils.getElementValue(series, "Runtime")));
        // fix title, unquote, and then parse the title if it's Title (year)
        Pair<String, String> pair = ParserUtils.parseTitleAndDateInBrackets(org.jdna.util.StringUtils.unquote(DOMUtils.getElementValue(series, "SeriesName")));
        md.set(MetadataKey.MEDIA_TITLE, pair.first());
        md.set(MetadataKey.DISPLAY_TITLE, pair.first());
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

    public String getTheMovieDBID() {
        return result.getId();
    }
}
