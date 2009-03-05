package org.jdna.media.metadata.impl.xbmc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MediaArt;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Type;
import org.jdna.media.metadata.impl.imdb.IMDBMovieMetaDataParser;
//import org.jdna.media.util.Scoring;
import org.jdna.util.DOMUtils;
import org.jdna.util.ParserUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XbmcMetadataProvider implements IMediaMetadataProvider {
    private static final Logger                 log                  = Logger.getLogger(XbmcMetadataProvider.class);
    private static final String                 EXTRA_ARGS_SEP       = ";;;;";
    private static final DocumentBuilderFactory factory              = DocumentBuilderFactory.newInstance();

    private IProviderInfo                       info;
    private XbmcScraper                         scraper;
    private Type[]                              supportedSearchTypes = null;

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

        ProviderInfo in = new ProviderInfo(scraper.getId(), scraper.getName() + " (XBMC Scraper)", scraper.getDescription(), scraper.getThumb());
        info = in;

        String content = scraper.getContent();
        if (!StringUtils.isEmpty(content)) {
            List<SearchQuery.Type> types = new ArrayList<Type>();
            Pattern p = Pattern.compile("([^,]+)");
            Matcher m = p.matcher(content);
            while (m.find()) {
                String type = m.group(1).trim();
                log.debug("Provider:  " + scraper.getId() + "; content type: " + type);
                if ("movies".equalsIgnoreCase(type)) {
                    log.debug("Using Movies for Provider:  " + scraper.getId() + "; content type: " + type);
                    types.add(SearchQuery.Type.MOVIE);
                } else if ("tvshows".equalsIgnoreCase(type)) {
                    log.debug("Using TV for Provider:  " + scraper.getId() + "; content type: " + type);
                    types.add(SearchQuery.Type.TV);
                } else if("music".equalsIgnoreCase(type)) {
                    types.add(SearchQuery.Type.MUSIC);
                } else {
                    log.debug("Unknown XBMC Scraper type: " + type);
                }
            }
            
            supportedSearchTypes = types.toArray(new SearchQuery.Type[types.size()]);
        } else {
            log.warn("No Content Type for provider: " + scraper.getId());
            supportedSearchTypes = new SearchQuery.Type[0];
        }
    }

    public IProviderInfo getInfo() {
        return info;
    }

    public IMediaMetadata getMetaData(IMediaSearchResult result) throws Exception {
        return getMetaData(result.getUrl());
    }

    private IMediaMetadata getMetaData(String providerDataUrl, XbmcUrl url, Map<String, String> extraArgs) throws Exception {
        MediaMetadata md = new MediaMetadata();
        updateMDValue(md, MetadataKey.PROVIDER_DATA_URL, providerDataUrl);
        updateMDValue(md, MetadataKey.PROVIDER_ID, getInfo().getId());

        XbmcMovieProcessor processor = new XbmcMovieProcessor(scraper);
        String xmlDetails = processor.getDetails(url, (extraArgs != null) ? extraArgs.get("id") : null);

        if (extraArgs == null || extraArgs.get("season") == null) {
            processXmlContent(xmlDetails, md);
        } else {
            processXmlContentForTV(xmlDetails, md, extraArgs);
        }

        return md;
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

    private void processXmlContentForTV(String xmlDetails, MediaMetadata md, Map<String, String> args) throws Exception {
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
            int findEpisode = NumberUtils.toInt(args.get("episode"));
            int findSeason = NumberUtils.toInt(args.get("season"));
            int findDisc = NumberUtils.toInt(args.get("disc"));
            
            XbmcUrl url = new XbmcUrl(episodeUrl);
            // Call get Episode List
            XbmcMovieProcessor processor = new XbmcMovieProcessor(scraper);
	            
            if(findEpisode > 0){ 
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
		            
	            md.set(MetadataKey.TV_EPISODE, String.valueOf(findEpisode));
	            md.set(MetadataKey.RELEASE_DATE, DOMUtils.getElementValue(el, "aired"));
	            md.set(MetadataKey.TV_SHOW_TITLE, DOMUtils.getElementValue(el, "title"));
            }else if(findDisc > 0){
            	md.set(MetadataKey.DVD_DISC, String.format("%1$02d", findDisc));
            }
            
            md.set(MetadataKey.TV_SEASON, String.valueOf(findSeason));
        }

    }

    private void addMetadata(MediaMetadata md, Element details) {
        log.debug("Processing <details> node....");
        NodeList nl = details.getElementsByTagName("fanart");
        for (int i = 0; i < nl.getLength(); i++) {
            Element fanart = (Element) nl.item(i);
            String url = fanart.getAttribute("url");
            processMediaArt(md, IMediaArt.BACKGROUND, "Backdrop Fanart", url);
        }

        nl = details.getElementsByTagName("thumbs");
        for (int i = 0; i < nl.getLength(); i++) {
            Element fanart = (Element) nl.item(i);
            processMediaArt(md, IMediaArt.POSTER, "Poster", fanart.getElementsByTagName("thumb"), null);
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
        updateMDValue(md, MetadataKey.RUNNING_TIME, DOMUtils.getElementValue(details, "runtime"));
        updateMDValue(md, MetadataKey.TITLE, DOMUtils.getElementValue(details, "title"));
        updateMDValue(md, MetadataKey.USER_RATING, DOMUtils.getElementValue(details, "rating"));
        updateMDValue(md, MetadataKey.YEAR, DOMUtils.getElementValue(details, "year"));
    }

    private void processMediaArt(MediaMetadata md, int type, String label, NodeList els, String baseUrl) {
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
    
    private void processMediaArt(MediaMetadata md, int type, String label, String image) {        
            MediaArt ma = new MediaArt();
            ma.setDownloadUrl(image);
            ma.setLabel(label);
            ma.setProviderId(getInfo().getId());
            ma.setType(type);
            md.addMediaArt(ma);
    }

    private void processUrlElement(MediaMetadata md, Element el) {
        // hack, but not sure how to deal with this...
        // we need to see if the returned result is a <url> and if so, then
        // process that url for functions...
        try {
            if (!StringUtils.isEmpty(el.getAttribute("function"))) {
                log.info("** BEGIN Processing Function: " + el.getAttribute("function") + " with url: " + el.getTextContent());
                processXmlContent(new XbmcUrl(el, scraper).getTextContent(), md);
                log.info("** END Processing Function: " + el.getAttribute("function") + " with url: " + el.getTextContent());
            }
        } catch (Exception e) {
            log.error("Failed to process sub url: " + el, e);
        }
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

    public IMediaMetadata getMetaData(String providerDataUrl) throws Exception {
        log.debug("Xbmc: ProviderDataUrl: " + providerDataUrl);
        Map<String, String> args = parseUrlArgs(providerDataUrl);
        return getMetaData(providerDataUrl, new XbmcUrl(args.get("_providerDataUrl")), args);
    }

    /**
     * given a provider data url, parse out the url and the extra provider args.
     * provider args are separated by ;;;;
     * 
     * @param providerDataUrl
     * @return
     */
    private Map<String, String> parseUrlArgs(String providerDataUrl) {
        log.debug("Looking for Extra Args in url: " + providerDataUrl);
        if (providerDataUrl == null) return null;

        Map<String, String> map = new HashMap<String, String>();
        Pattern p = Pattern.compile("(.*)" + EXTRA_ARGS_SEP + "(.*)");
        Matcher m = p.matcher(providerDataUrl);
        if (m.find()) {
            map.put("_providerDataUrl", m.group(1));

            Pattern argPattern = Pattern.compile("&?([^=]+)=([^&$]+)");
            Matcher argMatcher = argPattern.matcher(m.group(2));
            while (argMatcher.find()) {
                map.put(argMatcher.group(1), argMatcher.group(2));
                log.debug("Adding Arg: " + argMatcher.group(1) + "; " + argMatcher.group(2));
            }
            return map;
        } else {
            log.debug("No Extra Args in url: " + providerDataUrl);
            return null;
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

    public List<IMediaSearchResult> search(SearchQuery query) throws Exception {
        List<IMediaSearchResult> l = new ArrayList<IMediaSearchResult>();
        String arg = query.get(SearchQuery.Field.TITLE);
        // xbmc wants title and year separated, so let's do that
        String args[] = ParserUtils.parseTitle(arg);
        String title = args[0];
        String year = args[1];

        XbmcMovieProcessor processor = new XbmcMovieProcessor(scraper);
        XbmcUrl url = processor.getSearchUrl(title, year);
        String xmlString = processor.getSearchReulsts(url);

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
                sr.setUrl(createDetailUrl(u.toExternalForm(), query, DOMUtils.getElementValue(el, "id")));
                sr.setProviderId(getInfo().getId());
                String v[] = ParserUtils.parseTitle(t);
                sr.setTitle(v[0]);
                sr.setYear(v[1]);
                //sr.setResultType(Scoring.getInstance().getType(arg, v[0]));
                sr.setScore((float)org.jdna.util.Similarity.getInstance().compareStrings(arg,v[0]));
                l.add(sr);
            } catch (Exception e) {
                log.error("Error process an xml node!  Ignoring it from the search results.");
            }
        }

        return l;
    }

    /**
     * builds custom url storing the episode and season for later retrieval
     * 
     * @param url
     * @param query
     * @param id
     * @return
     */
    private String createDetailUrl(String url, SearchQuery query, String id) {
        Map<String, String> args = new HashMap<String, String>();
        args.put("id", id);

        if (query.getType() == SearchQuery.Type.MOVIE) {
        } else if (query.getType() == SearchQuery.Type.TV) {
            args.put("season", query.get(SearchQuery.Field.SEASON));
            args.put("episode", query.get(SearchQuery.Field.EPISODE));
            args.put("disc", query.get(SearchQuery.Field.DISC));
        } else {
        }

        String extraArgs = EXTRA_ARGS_SEP;
        boolean sep = false;
        for (String key : args.keySet()) {
            if (sep) extraArgs += "&";
            extraArgs += (key + "=" + args.get(key));
            sep = true;
        }
        return url + extraArgs;
    }

    public Type[] getSupportedSearchTypes() {
        return supportedSearchTypes;
    }
}
