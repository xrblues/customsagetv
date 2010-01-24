package org.jdna.media.metadata.impl.xbmc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.HasFindByIMDBID;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MediaArt;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Field;
import org.jdna.media.metadata.impl.imdb.IMDBMovieMetaDataParser;
import org.jdna.media.metadata.impl.imdb.IMDBUtils;
import org.jdna.util.DOMUtils;
import org.jdna.util.ParserUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaArtifactType;
import sagex.phoenix.fanart.MediaType;

public class XbmcMetadataProvider implements IMediaMetadataProvider, HasFindByIMDBID {
    private static final Logger                 log                  = Logger.getLogger(XbmcMetadataProvider.class);
    private static final DocumentBuilderFactory factory              = DocumentBuilderFactory.newInstance();

    private IProviderInfo                       info;
    private XbmcScraper                         scraper;
    private MediaType[]                         supportedSearchTypes = null;

    public XbmcMetadataProvider(String providerXml) {
        XbmcScraperParser parser = new XbmcScraperParser();
        XbmcScraper scr;
        try {
            scr = parser.parseScraper(new File(providerXml));
        } catch (Exception e) {
            log.error("Failed to Load XBMC Scraper: " + providerXml);
            throw new RuntimeException("Failed to Load XBMC Scraper: " + providerXml, e);
        }

        init(scr);
    }

    public XbmcMetadataProvider(XbmcScraper scraper) {
        init(scraper);
    }

    private void init(XbmcScraper scraper) {
        this.scraper = scraper;

        ProviderInfo in = new ProviderInfo(scraper.getId(), scraper.getName(), scraper.getDescription(), scraper.getThumb());
        info = in;

        String content = scraper.getContent();
        if (!StringUtils.isEmpty(content)) {
            List<MediaType> types = new ArrayList<MediaType>();
            Pattern p = Pattern.compile("([^,]+)");
            Matcher m = p.matcher(content);
            while (m.find()) {
                String type = m.group(1).trim();
                log.debug("Provider:  " + scraper.getId() + "; content type: " + type);
                if ("movies".equalsIgnoreCase(type)) {
                    log.debug("Using Movies for Provider:  " + scraper.getId() + "; content type: " + type);
                    types.add(MediaType.MOVIE);
                } else if ("tvshows".equalsIgnoreCase(type)) {
                    log.debug("Using TV for Provider:  " + scraper.getId() + "; content type: " + type);
                    types.add(MediaType.TV);
                } else if ("music".equalsIgnoreCase(type)) {
                    types.add(MediaType.MUSIC);
                } else {
                    log.debug("Unknown XBMC Scraper type: " + type);
                }
            }

            supportedSearchTypes = types.toArray(new MediaType[types.size()]);
        } else {
            log.warn("No Content Type for provider: " + scraper.getId());
            supportedSearchTypes = new MediaType[0];
        }
    }

    public IProviderInfo getInfo() {
        return info;
    }

    public IMediaMetadata getMetaData(IMetadataSearchResult result) throws Exception {
        log.debug("Xbmc: getMetadata(): " + result);
        
        MediaMetadata md = new MediaMetadata();
        updateMDValue(md, MetadataKey.METADATA_PROVIDER_ID, getInfo().getId());

        md.setString(MetadataKey.MEDIA_PROVIDER_DATA_ID, result.getId());

        XbmcMovieProcessor processor = new XbmcMovieProcessor(scraper);
        String xmlDetails = processor.getDetails(new XbmcUrl(result.getUrl()), result.getId());

        if (result.getMediaType() == MediaType.TV) {
            md.set(MetadataKey.MEDIA_TYPE, MetadataUtil.TV_MEDIA_TYPE);
            processXmlContentForTV(xmlDetails, md, result);
        } else {
            processXmlContent(xmlDetails, md);
        }

        // try to parse an imdb id from the url
        if (!StringUtils.isEmpty(result.getUrl()) && StringUtils.isEmpty(MetadataAPI.getIMDBID(md))) {
            MetadataAPI.setIMDBID(md, IMDBUtils.parseIMDBID(result.getUrl()));
        }
        
        return md;
    }
    public List<IMetadataSearchResult> search(SearchQuery query) throws Exception {
        List<IMetadataSearchResult> l = new ArrayList<IMetadataSearchResult>();
        String arg = query.get(SearchQuery.Field.QUERY);
        
        // xbmc wants title and year separated, so let's do that
        String args[] = ParserUtils.parseTitle(arg);
        String title = args[0];
        String year = query.get(Field.YEAR);

        XbmcMovieProcessor processor = new XbmcMovieProcessor(scraper);
        XbmcUrl url = processor.getSearchUrl(title, year);
        String xmlString = processor.getSearchReulsts(url);

        log.debug("========= BEGIN XBMC Scraper Search Xml Results: Url: " + url);
        log.debug(xmlString);
        log.debug("========= End XBMC Scraper Search Xml Results: Url: " + url);

        Document xml = parseXmlString(xmlString);

        NodeList nl = xml.getElementsByTagName("entity");
        for (int i = 0; i < nl.getLength(); i++) {
            try {
                Element el = (Element) nl.item(i);
                NodeList titleList = el.getElementsByTagName("title");
                String t = titleList.item(0).getTextContent();
                NodeList urlList = el.getElementsByTagName("url");
                XbmcUrl u = new XbmcUrl((Element) urlList.item(0));

                MediaSearchResult sr = new MediaSearchResult();
                String id = DOMUtils.getElementValue(el, "id");
                sr.setId(id);
                sr.setUrl(u.toExternalForm());
                sr.setProviderId(getInfo().getId());
                sr.getExtra().put("mediatype", query.getMediaType().name());
                
                // populate extra args
                MetadataUtil.copySearchQueryToSearchResult(query, sr);

                if (u.toExternalForm().indexOf("imdb") != -1) {
                    sr.addExtraArg("xbmcprovider", "imdb");
                    sr.addExtraArg("imdbid", id);
                } else if (u.toExternalForm().indexOf("thetvdb.com") != -1) {
                    sr.addExtraArg("xbmcprovider", "tvdb");
                    sr.addExtraArg("tvdbid", id);
                }

                String v[] = ParserUtils.parseTitle(t);
                sr.setTitle(v[0]);
                sr.setYear(v[1]);
                sr.setScore(MetadataUtil.calculateScore(arg, v[0]));
                l.add(sr);
            } catch (Exception e) {
                log.error("Error process an xml node!  Ignoring it from the search results.");
            }
        }

        return l;
    }


    public IMediaMetadata getMetadataForIMDBId(String imdbid) {
        if (getInfo().getId().contains("imdb")) {
            MediaSearchResult sr = new MediaSearchResult();
            sr.setIMDBId(imdbid);
            sr.setId(imdbid);
            sr.setUrl(IMDBUtils.createDetailUrl(imdbid));
            try {
                return getMetaData(sr);
            } catch (Exception e) {
                log.warn("Failed to search by IMDB URL: " + sr.getUrl(), e);
            }
        }
        return null;
    }

    private void processXmlContent(String xmlDetails, MediaMetadata md) throws Exception {
        if (xmlDetails == null || StringUtils.isEmpty(xmlDetails)) {
            log.warn("Cannot process empty Xml Contents.");
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("******* BEGIN XML ***********");
            log.debug(xmlDetails);
            log.debug("******* END XML ***********");
        }

        Document xml = parseXmlString(xmlDetails);
        addMetadata(md, xml.getDocumentElement());
    }

    private void processXmlContentForTV(String xmlDetails, MediaMetadata md, IMetadataSearchResult result) throws Exception {
        log.debug("*** PROCESSING TV ***");
        if (xmlDetails == null || StringUtils.isEmpty(xmlDetails)) {
            log.warn("Cannot process empty Xml Contents.");
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("******* BEGIN XML ***********");
            log.debug(xmlDetails);
            log.debug("******* END XML ***********");
        }

        Document xml = parseXmlString(xmlDetails);

        addMetadata(md, xml.getDocumentElement());

        log.debug("Fetching Episode Guide url");

        // now check for episode and guide url
        String episodeUrl = DOMUtils.getElementValue(xml.getDocumentElement(), "episodeguide");
        if (StringUtils.isEmpty(episodeUrl)) {
            log.error("No Episode Data!");
        } else {
            if (!StringUtils.isEmpty(result.getExtra().get(SearchQuery.Field.SEASON.name()))) {
                int findEpisode = NumberUtils.toInt(result.getExtra().get(SearchQuery.Field.EPISODE.name()));
                int findSeason = NumberUtils.toInt(result.getExtra().get(SearchQuery.Field.SEASON.name()));
                int findDisc = NumberUtils.toInt(result.getExtra().get(SearchQuery.Field.DISC.name()));

                XbmcUrl url = new XbmcUrl(episodeUrl);
                // Call get Episode List
                XbmcMovieProcessor processor = new XbmcMovieProcessor(scraper);

                if (findEpisode > 0) {
                    String epListXml = processor.getEpisodeList(url);

                    log.debug("******** BEGIN EPISODE LIST XML ***********");
                    log.debug(epListXml);
                    log.debug("******** END EPISODE LIST XML ***********");

                    Document epListDoc = parseXmlString(epListXml);

                    NodeList nl = epListDoc.getElementsByTagName("episode");
                    int s = nl.getLength();
                    int season, ep;
                    String id = null;
                    String epUrl = null;
                    for (int i = 0; i < s; i++) {
                        Element el = (Element) nl.item(i);
                        season = DOMUtils.getElementIntValue(el, "season");
                        ep = DOMUtils.getElementIntValue(el, "epnum");
                        if (season == findSeason && ep == findEpisode) {
                            id = DOMUtils.getElementValue(el, "id");
                            epUrl = DOMUtils.getElementValue(el, "url");
                            break;
                        }
                    }

                    if (id == null) {
                        throw new Exception("Could Not Find Seaons and Episode for: " + findSeason + "x" + findEpisode);
                    }

                    log.debug("We have an episdoe id for season and episode... fetching details...");

                    processor = new XbmcMovieProcessor(scraper);
                    xmlDetails = processor.getEpisodeDetails(new XbmcUrl(epUrl), id);

                    log.debug("******** BEGIN EPISODE DETAILS XML ***********");
                    log.debug(xmlDetails);
                    log.debug("******** END EPISODE DETAILS XML ***********");

                    // update again, using the episode specific data
                    xml = parseXmlString(xmlDetails);
                    Element el = xml.getDocumentElement();
                    addMetadata(md, el);

                    // add/update tv specific stuff
                    String plot = DOMUtils.getElementValue(el, "plot");
                    if (!StringUtils.isEmpty(plot)) {
                        md.setDescription(plot);
                    }

                    md.set(MetadataKey.EPISODE, String.valueOf(findEpisode));
                    md.set(MetadataKey.RELEASE_DATE, DOMUtils.getElementValue(el, "aired"));
                    md.set(MetadataKey.EPISODE_TITLE, DOMUtils.getElementValue(el, "title"));
                } else if (findDisc > 0) {
                    md.set(MetadataKey.DVD_DISC, String.format("%1$02d", findDisc));
                }

                if (findSeason>0) {
                    md.set(MetadataKey.SEASON, String.valueOf(findSeason));
                }
            }
        }

    }

    private void addMetadata(MediaMetadata md, Element details) {
        log.debug("Processing <details> node....");
        NodeList nl = details.getElementsByTagName("fanart");
        for (int i = 0; i < nl.getLength(); i++) {
            Element fanart = (Element) nl.item(i);
            String url = fanart.getAttribute("url");
            NodeList thumbs = fanart.getElementsByTagName("thumb");
            if (thumbs!=null && thumbs.getLength()>0) {
                processMediaArt(md, MediaArtifactType.BACKGROUND, "Backgrounds", thumbs, url);
            } else {
                if (!StringUtils.isEmpty(url)) {
                    processMediaArt(md, MediaArtifactType.BACKGROUND, "Background", url);
                }
            }
        }

        nl = details.getElementsByTagName("thumbs");
        for (int i = 0; i < nl.getLength(); i++) {
            Element fanart = (Element) nl.item(i);
            processMediaArt(md, MediaArtifactType.POSTER, "Poster", fanart.getElementsByTagName("thumb"), null);
        }

        nl = details.getElementsByTagName("actor");
        for (int i = 0; i < nl.getLength(); i++) {
            Element actor = (Element) nl.item(i);
            CastMember cm = new CastMember();
            cm.setType(ICastMember.ACTOR);
            cm.setName(DOMUtils.getElementValue(actor, "name"));
            cm.setPart(DOMUtils.getElementValue(actor, "role"));
            md.addCastMember(cm);
        }

        nl = details.getElementsByTagName("director");
        for (int i = 0; i < nl.getLength(); i++) {
            Element el = (Element) nl.item(i);
            CastMember cm = new CastMember();
            cm.setType(ICastMember.DIRECTOR);
            cm.setName(StringUtils.trim(el.getTextContent()));
            log.debug("Adding Director: " + cm.getName());
            cm.setPart("Director");
            md.addCastMember(cm);
        }

        nl = details.getElementsByTagName("credits");
        for (int i = 0; i < nl.getLength(); i++) {
            Element el = (Element) nl.item(i);
            CastMember cm = new CastMember();
            cm.setType(ICastMember.WRITER);
            cm.setName(StringUtils.trim(el.getTextContent()));
            cm.setPart("Writer");
            md.addCastMember(cm);
        }

        nl = details.getElementsByTagName("genre");
        for (int i = 0; i < nl.getLength(); i++) {
            Element el = (Element) nl.item(i);
            md.addGenre(StringUtils.trim(el.getTextContent()));
        }

        updateMDValue(md, MetadataKey.COMPANY, DOMUtils.getElementValue(details, "studio"));
        updateMDValue(md, MetadataKey.DESCRIPTION, DOMUtils.getMaxElementValue(details, "plot"));
        updateMDValue(md, MetadataKey.MPAA_RATING, IMDBMovieMetaDataParser.parseMPAARating(DOMUtils.getElementValue(details, "mpaa")));
        updateMDValue(md, MetadataKey.MPAA_RATING_DESCRIPTION, DOMUtils.getElementValue(details, "mpaa"));

        updateMDValue(md, MetadataKey.RELEASE_DATE, DOMUtils.getElementValue(details, "year"));
        updateMDValue(md, MetadataKey.RUNNING_TIME, MetadataUtil.parseRunningTime(DOMUtils.getElementValue(details, "runtime"), IMDBMovieMetaDataParser.IMDB_RUNNING_TIME_REGEX));
        updateMDValue(md, MetadataKey.MEDIA_TITLE, DOMUtils.getElementValue(details, "title"));
        updateMDValue(md, MetadataKey.USER_RATING, DOMUtils.getElementValue(details, "rating"));
        updateMDValue(md, MetadataKey.YEAR, DOMUtils.getElementValue(details, "year"));
    }

    private void processMediaArt(MediaMetadata md, MediaArtifactType type, String label, NodeList els, String baseUrl) {
        for (int i = 0; i < els.getLength(); i++) {
            Element e = (Element) els.item(i);
            String image = e.getTextContent();
            if (image != null) image = image.trim();
            if (baseUrl != null) {
                baseUrl = baseUrl.trim();
                image = baseUrl + image;
            }
            processMediaArt(md, type, label, image);
        }
    }

    private void processMediaArt(MediaMetadata md, MediaArtifactType type, String label, String image) {
        MediaArt ma = new MediaArt();
        ma.setDownloadUrl(image);
        ma.setLabel(label);
        ma.setProviderId(getInfo().getId());
        ma.setType(type);
        md.addMediaArt(ma);
    }

    /**
     * only update if the existing value is null or empty
     * 
     * @param md
     * @param key
     * @param value
     */
    private void updateMDValue(MediaMetadata md, MetadataKey key, String value) {
        if (md.get(key) == null && !StringUtils.isEmpty(value)) {
            md.set(key, value);
        }
    }

    /**
     * added because some xml strings are not parsable using utf-8
     * 
     * @param xml
     * @return
     * @throws Exception
     */
    private Document parseXmlString(String xml) throws Exception {
        DocumentBuilder parser = factory.newDocumentBuilder();
        Document doc = null;
        for (String charset : new String[] { "UTF-8", "ISO-8859-1", "US-ASCII" }) {
            try {
                doc = parser.parse(new ByteArrayInputStream(xml.getBytes(charset)));
                break;
            } catch (Throwable t) {
                log.error("Failed to parse xml using charset: " + charset, t);
            }
        }

        if (doc == null) {
            log.error("Unabled to parse xml string");
            log.error(xml);
            throw new Exception("Unable to parse xml!");
        }

        return doc;
    }

    public MediaType[] getSupportedSearchTypes() {
        return supportedSearchTypes;
    }

}
