package org.jdna.media.metadata.impl.themoviedb;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaSearchResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TheMovieDBSearchParser {
	private static final Logger log = Logger.getLogger(TheMovieDBMetadataProvider.class);
	private static final String SEARCH_URL = "http://api.themoviedb.org/2.0/Movie.search?title=%s&api_key=%s";
	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	
	private String url;
	private List<IMediaSearchResult> results = new ArrayList<IMediaSearchResult>();
	
	public TheMovieDBSearchParser(int searchType, String arg) {
		this.url = String.format(SEARCH_URL, URLEncoder.encode(arg), TheMovieDBMetadataProvider.getApiKey());
	}
	
	public List<IMediaSearchResult> getResults() {
		// already parsed
		if (results.size()>0) return results;
		
		// parse
		try {
			DocumentBuilder parser = factory.newDocumentBuilder();
			Document doc = parser.parse(url);
			
			NodeList nl = doc.getElementsByTagName("movie");
			int len = nl.getLength();
			for (int i=0;i<len;i++) {
				addMovie((Element)nl.item(i));
			}
			
		} catch (Exception e) {
			log.error("Failed to parse/search using url: " + url, e);
		}
		return results;
	}

	private void addMovie(Element item) {
		MediaSearchResult sr = new MediaSearchResult();
		sr.setProviderId(TheMovieDBMetadataProvider.PROVIDER_ID);
		
		float score = getScore(item);
		if (score>=1.0) {
			sr.setResultType(IMediaSearchResult.RESULT_TYPE_EXACT_MATCH);
		} else {
			sr.setResultType(IMediaSearchResult.RESULT_TYPE_PARTIAL_MATCH);
		}
		
		sr.setTitle(getElementValue(item, "title"));
		sr.setYear(getElementValue(item, "release"));
		sr.setId(getElementValue(item, "id"));
		sr.setIMDBId(getElementValue(item, "imdb"));
		
		results.add(sr);
	}

	private float getScore(Element item) {
		try {
			return Float.parseFloat(getElementValue(item, "score"));
		} catch (Exception e) {
			return 0.0f;
		}
	}

	public static String getElementValue(Element el, String tag) {
		NodeList nl = el.getElementsByTagName(tag);
		if (nl.getLength()>0) {
			Node n = nl.item(0);
			return n.getTextContent().trim();
		}
		return null;
	}
}
