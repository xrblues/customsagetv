package org.jdna.media.metadata.impl.themoviedb;

import java.net.URLEncoder;
import java.util.ArrayList;
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
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.url.IUrl;
import org.jdna.url.UrlFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sagex.phoenix.configuration.proxy.GroupProxy;

public class TheMovieDBSearchParser {
    private static final Logger                 log        = Logger.getLogger(TheMovieDBMetadataProvider.class);
    private static final String                 SEARCH_URL = "http://api.themoviedb.org/2.0/Movie.search?title=%s&api_key=%s";
    private static final DocumentBuilderFactory factory    = DocumentBuilderFactory.newInstance();
    private static final Pattern yearPattern = Pattern.compile("([0-9]{4})");

    private IUrl                                url;
    private List<IMediaSearchResult>            results    = new ArrayList<IMediaSearchResult>();
    private String								searchTitle;
    
    public MetadataConfiguration cfg = GroupProxy.get(MetadataConfiguration.class);
    
    private Comparator<IMediaSearchResult> sorter              = new Comparator<IMediaSearchResult>() {

        public int compare(IMediaSearchResult o1, IMediaSearchResult o2) {
     	   if(o1.getScore() > o2.getScore()) return -1;
     	   if(o1.getScore() < o2.getScore()) return 1;
            return 0;
        }

    };
    
    private static class ScoredTitle {
        String title;
        float score;
        
        public ScoredTitle(String title, float score) {
            this.title=title;
            this.score=score;
        }
    }
    
    public TheMovieDBSearchParser(SearchQuery query) {
        searchTitle = query.get(SearchQuery.Field.QUERY);
        this.url = UrlFactory.newUrl(String.format(SEARCH_URL, URLEncoder.encode(searchTitle), TheMovieDBMetadataProvider.getApiKey()));

        log.debug("TheMovieDB SearchQuery Url: " + url);
    }

    public List<IMediaSearchResult> getResults() {
        // already parsed
        if (results.size() > 0) return results;

        // parse
        try {
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document doc = parser.parse(url.getInputStream(null, true));

            NodeList nl = doc.getElementsByTagName("movie");
            int len = nl.getLength();
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
        sr.setProviderId(TheMovieDBMetadataProvider.PROVIDER_ID);
        if (StringUtils.isEmpty(getElementValue(item, "title"))) {
            log.warn("TheMovieDB Item didn't contain a title: " + item.getTextContent());
            return;
        }
        
        String title = getElementValue(item, "title");
        sr.setTitle(getElementValue(item, "title"));
        sr.setScore(getScore(title));

        // add alternate title scoring...
        if (cfg.isScoreAlternateTitles()) {
            log.debug("Looking for alternate Titles");
            List<ScoredTitle> scoredTitles = new LinkedList<ScoredTitle>();
            NodeList nl = item.getElementsByTagName("alternative_title");
            if (nl!=null && nl.getLength()>0) {
                for (int i=0;i<nl.getLength();i++) {
                    Element el = (Element) nl.item(i);
                    String altTitle = el.getTextContent();
                    if (!StringUtils.isEmpty(altTitle)) {
                        altTitle = altTitle.trim();
                        ScoredTitle st = new ScoredTitle(altTitle, getScore(altTitle));
                        scoredTitles.add(st);
                        log.debug("Adding Alternate Title: " + st.title + "; score: " + st.score);
                    }
                }
            }
            
            if (scoredTitles.size()>0) {
                ScoredTitle curTitle = new ScoredTitle(sr.getTitle(), sr.getScore());
                for (ScoredTitle st : scoredTitles) {
                    if (st.score > curTitle.score) {
                        curTitle = st;
                    }
                }
                
                if (curTitle.score>sr.getScore()) {
                    log.debug("Using Alternate Title Score: " + curTitle.score + "; Title: " + curTitle.title);
                    sr.setScore(curTitle.score);
                    sr.setTitle(sr.getTitle() + " (aka " + curTitle.title + ") ");
                }
            }
        }
        
        sr.setYear(parseYear(getElementValue(item, "release")));
        String id = getElementValue(item, "id");
        sr.setUrl(String.format(TheMovieDBItemParser.ITEM_URL, id));
        sr.setMetadataId(new MetadataID("themoviedb", id));
        
        // TODO: once MetadataID contains a map of ids, add the imdb to it
        //sr.setMetadataId(new MetadataID(IMDBMetaDataProvider.PROVIDER_ID, getElementValue(item, "imdb")));

        results.add(sr);
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
            float score = MetadataUtil.calculateScore(searchTitle,title);
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
