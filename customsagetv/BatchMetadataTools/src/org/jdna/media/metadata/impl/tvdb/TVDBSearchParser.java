package org.jdna.media.metadata.impl.tvdb;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.url.IUrl;
import org.jdna.url.UrlFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TVDBSearchParser {
    private static final Logger                 log        = Logger.getLogger(TVDBSearchParser.class);
    private static final String                 SEARCH_URL = "http://www.thetvdb.com/api/GetSeries.php?seriesname=%s";
    private static final DocumentBuilderFactory factory    = DocumentBuilderFactory.newInstance();
    private static final Pattern yearPattern = Pattern.compile("([0-9]{4})");

    private SearchQuery query = null;
    private IUrl                                url;
    private List<IMediaSearchResult>            results    = new LinkedList<IMediaSearchResult>();
    private String								searchTitle;

    private Comparator<IMediaSearchResult> sorter              = new Comparator<IMediaSearchResult>() {
        public int compare(IMediaSearchResult o1, IMediaSearchResult o2) {
     	   if(o1.getScore() > o2.getScore()) return -1;
     	   if(o1.getScore() < o2.getScore()) return 1;
            return 0;
        }
    };
    
    public TVDBSearchParser(SearchQuery query) {
        this.query=query;
        searchTitle = query.get(SearchQuery.Field.TITLE);
        this.url = UrlFactory.newUrl(String.format(SEARCH_URL, URLEncoder.encode(searchTitle)));

        log.debug("TVDB SearchQuery Url: " + url);
    }

    public List<IMediaSearchResult> getResults() {
        // already parsed
        if (results.size() > 0) return results;

        // parse
        try {
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document doc = parser.parse(url.getInputStream(null, true));

            NodeList nl = doc.getElementsByTagName("Series");
            int len = nl.getLength();
            if (len==0) {
                log.warn("Could not find any results for: " + url);
            }
            for (int i = 0; i < len; i++) {
                addMovie((Element) nl.item(i));
            }
            Collections.sort(results, sorter);
        } catch (Exception e) {
            log.error("Failed to parse/search using url: " + url, e);
        }
        return results;
    }

    private void addMovie(Element item) {
        MediaSearchResult sr = new MediaSearchResult();
        for (SearchQuery.Field f : SearchQuery.Field.values()) {
            if (f==SearchQuery.Field.TITLE) continue;
            String s = query.get(f);
            if (!StringUtils.isEmpty(s)) {
                sr.addExtraArg(f.name(), s);
            }
        }
        sr.setProviderId(TVDBMetadataProvider.PROVIDER_ID);
        String title = getElementValue(item, "SeriesName");
        if (StringUtils.isEmpty(title)) {
            log.warn("TVDB Item didn't contain a title: " + item.getTextContent());
            return;
        }
        
        sr.setTitle(title);
        sr.setScore(getScore(title));
        
        sr.setYear(parseYear(getElementValue(item, "FirstAired")));
        
        String id = getElementValue(item, "seriesid");
        sr.setMetadataId(new MetadataID("tvdb", id));
        
        sr.setUrl(id);
        
        // use the url with extra args
        sr.setUrl(sr.getUrlWithExtraArgs());
        
        // TODO: once MetadataID contains a map of ids, add the imdb to it
        //sr.setMetadataId(new MetadataID(IMDBMetaDataProvider.PROVIDER_ID, getElementValue(item, "imdb")));

        results.add(sr);
        log.debug("Adding TV Title: " + sr.getTitle());
    }

    private String parseYear(String year) {
        if (year==null) return null;
        Matcher m = yearPattern.matcher(year);
        if (m.find()) {
            year = m.group(1);
        }
        return year;
    }

    private float getScore(String title) {
        if (title==null) return 0.0f;
        try {
            float score = (float)org.jdna.util.Similarity.getInstance().compareStrings(searchTitle,title);
            log.debug(String.format("Comparing:[%s][%s]: %s", searchTitle, title, score));
            return score;
        } catch (Exception e) {
            return 0.0f;
        }
    }
    
    public static String getElementValue(Element el, String tag) {
        NodeList nl = el.getElementsByTagName(tag);
        if (nl.getLength() > 0) {
            Node n = nl.item(0);
            return n.getTextContent().trim();
        }
        return null;
    }
}
