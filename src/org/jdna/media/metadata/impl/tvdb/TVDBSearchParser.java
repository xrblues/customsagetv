package org.jdna.media.metadata.impl.tvdb;

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
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.url.IUrl;
import org.jdna.url.UrlFactory;
import org.jdna.url.UrlUtil;
import org.jdna.util.Pair;
import org.jdna.util.ParserUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.fanart.IMetadataSearchResult;

public class TVDBSearchParser {
    private static final Logger                 log        = Logger.getLogger(TVDBSearchParser.class);
    private static final String                 SEARCH_URL = "http://www.thetvdb.com/api/GetSeries.php?seriesname=%s&language=%s";
    private static final DocumentBuilderFactory factory    = DocumentBuilderFactory.newInstance();
    private static final Pattern yearPattern = Pattern.compile("([0-9]{4})");

    private SearchQuery query = null;
    private IUrl                                url;
    private List<IMetadataSearchResult>            results    = new LinkedList<IMetadataSearchResult>();
    private String								searchTitle;
    private TVDBConfiguration config = null;
    private Comparator<IMetadataSearchResult> sorter              = new Comparator<IMetadataSearchResult>() {
        public int compare(IMetadataSearchResult o1, IMetadataSearchResult o2) {
     	   if(o1.getScore() > o2.getScore()) return -1;
     	   if(o1.getScore() < o2.getScore()) return 1;
            return 0;
        }
    };
    
    public TVDBSearchParser(SearchQuery query) {
        config = GroupProxy.get(TVDBConfiguration.class);
        this.query=query;
        searchTitle = query.get(SearchQuery.Field.QUERY);
        this.url = UrlFactory.newUrl(String.format(SEARCH_URL, UrlUtil.encode(searchTitle), config.getLanguage()));

        log.debug("TVDB SearchQuery Url: " + url);
    }

    public List<IMetadataSearchResult> getResults() {
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
        String title = getElementValue(item, "SeriesName");
        if (StringUtils.isEmpty(title)) {
            log.warn("TVDB Item didn't contain a title: " + item.getTextContent());
            return;
        }
        
        MediaSearchResult sr = new MediaSearchResult();
        MetadataUtil.copySearchQueryToSearchResult(query, sr);
        sr.setProviderId(TVDBMetadataProvider.PROVIDER_ID);
        Pair<String, String> pair = ParserUtils.parseTitleAndDateInBrackets(org.jdna.util.StringUtils.unquote(title));
        sr.setTitle(pair.first());
        sr.setScore(getScore(pair.first()));
        sr.setYear(parseYear(getElementValue(item, "FirstAired")));
        sr.setId(getElementValue(item, "seriesid"));
        sr.setIMDBId(getElementValue(item, "imdb"));
        // note, sr.setUrl() is never used, since IMDB only looks up using ids

        results.add(sr);
        log.debug("Added TVDB Title: " + sr.getTitle());
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
            float score = MetadataUtil.calculateCompressedScore(searchTitle,title);
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
