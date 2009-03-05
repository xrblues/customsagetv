package org.jdna.media.metadata.impl.themoviedb;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.SearchQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TheMovieDBSearchParser {
    private static final Logger                 log        = Logger.getLogger(TheMovieDBMetadataProvider.class);
    private static final String                 SEARCH_URL = "http://api.themoviedb.org/2.0/Movie.search?title=%s&api_key=%s";
    private static final DocumentBuilderFactory factory    = DocumentBuilderFactory.newInstance();

    private String                              url;
    private List<IMediaSearchResult>            results    = new ArrayList<IMediaSearchResult>();
    //private SearchQuery 						query;
    private List<String> 						words 	   = new ArrayList<String>();
    private String								searchTitle;
    private Comparator<IMediaSearchResult> sorter              = new Comparator<IMediaSearchResult>() {

        public int compare(IMediaSearchResult o1, IMediaSearchResult o2) {
     	   if(o1.getScore() > o2.getScore()) return -1;
     	   if(o1.getScore() < o2.getScore()) return 1;
            return 0;
        }

    };
    
    public TheMovieDBSearchParser(SearchQuery query) {
        searchTitle = query.get(SearchQuery.Field.TITLE);
        this.url = String.format(SEARCH_URL, URLEncoder.encode(searchTitle), TheMovieDBMetadataProvider.getApiKey());

        Pattern p = Pattern.compile("([^\\s]+)\\s?");
        Matcher m;
        
        m = p.matcher(searchTitle);
	    if(m.find()){
		   for(int i=0; i<m.groupCount(); i++)
		   {
				 words.add(m.group(i));    			
		   }
	    }
    }

    public List<IMediaSearchResult> getResults() {
        // already parsed
        if (results.size() > 0) return results;

        // parse
        try {
            DocumentBuilder parser = factory.newDocumentBuilder();
            Document doc = parser.parse(url);

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
        
        sr.setScore(getScore(item));
        sr.setTitle(getElementValue(item, "title"));
        sr.setYear(getElementValue(item, "release"));
        sr.setUrl(getElementValue(item, "id"));
        sr.setImdbId(getElementValue(item, "imdb"));

        results.add(sr);
    }

    private float getScore(Element item) {
        try {
        	String matchTitle = getElementValue(item, "title");
        	
        	return (float)org.jdna.util.Similarity.getInstance().compareStrings(searchTitle,matchTitle);
        } catch (Exception e) {
            return (float)0.0f;
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
