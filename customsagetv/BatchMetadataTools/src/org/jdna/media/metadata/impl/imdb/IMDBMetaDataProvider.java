package org.jdna.media.metadata.impl.imdb;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoMetaDataProvider;
import org.jdna.media.metadata.IVideoSearchResult;
import org.jdna.media.metadata.SearchException;
import org.xml.sax.SAXException;

public class IMDBMetaDataProvider implements IVideoMetaDataProvider {
	private static final Logger log = Logger.getLogger(IMDBMetaDataProvider.class);
	
	public static final String IMDB_FIND_URL = "http://www.imdb.com/find?s=tt&q={0}&x=0&y=0";
	public static final String PROVIDER_ID = "imdb";
	public static final String PROVIDER_NAME = "IMDB Provider by Stuckless";
	public static final String PROVIDER_ICON_URL = "http://i.media-imdb.com/images/nb15/logo2.gif";

	public String getIconUrl() {
		return PROVIDER_ICON_URL;
	}

	public Object getId() {
		return PROVIDER_ID;
	}

	public String getName() {
		return PROVIDER_NAME;
	}

	public List<IVideoSearchResult> search(int searchType, String arg) throws SearchException {
		List<IVideoSearchResult> results=null;
		if (searchType==IVideoMetaDataProvider.SEARCH_TITLE) {
			String eArg = URLEncoder.encode(arg);
			String url = MessageFormat.format(IMDB_FIND_URL, eArg);
			log.debug("IMDB Search Url: " + url);
			IMDBSearchResultParser parser = new IMDBSearchResultParser(url);
			// don't follow the redirected urls
			parser.setFollowRedirects(false);
			try {
				parser.parse();
				if (parser.getFollowRedirects()==false && parser.isRedirecting()) {
					return returnSingleResult(parser.getRedirectUrl());
				}
				if (parser.hasError()) {
					throw new IOException("Failed to Parse the IMDB url correctly");
				}
				results = parser.getResults();
			} catch (IOException e) {
				log.error("Error Performing Search: " + arg, e);
			} catch (SAXException e) {
				log.error("Error Parsing Search: " + arg, e);
			}
		} else {
			log.error("Search Type no Supported: " + searchType);
			throw new SearchException("Seach Type Not Supported: " + searchType, new Exception());
		}
		return results;
	}

	private List<IVideoSearchResult> returnSingleResult(String redirectUrl) throws IOException {
		List<IVideoSearchResult> result = new ArrayList<IVideoSearchResult>();
		
		// the IMDBMovieMetaData implements the IVideoSearchResult interface
		result.add((IVideoSearchResult) getMetaData(redirectUrl));
		
		return result;
	}

	public IVideoMetaData getMetaData(String providerDataUrl) throws IOException {
		IMDBMovieMetaDataParser parser = new IMDBMovieMetaDataParser(providerDataUrl);
		try {
			parser.parse();
		} catch (SAXException e) {
			log.error("Failed to getMetaData for url: " + providerDataUrl);
			throw new IOException("Failed to parse providerDataUrl: " + providerDataUrl, e);
		}
		return parser.getMetatData();
	}
}
