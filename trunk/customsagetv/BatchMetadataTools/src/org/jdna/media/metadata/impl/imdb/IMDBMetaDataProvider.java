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
import org.jdna.media.metadata.VideoSearchResult;
import org.xml.sax.SAXException;

public class IMDBMetaDataProvider implements IVideoMetaDataProvider {
	private static final Logger log = Logger.getLogger(IMDBMetaDataProvider.class);
	
	public static final String IMDB_FIND_URL = "http://www.imdb.com/find?s=tt&q={0}&x=0&y=0";
	public static final String PROVIDER_ID = "imdb";
	public static final String PROVIDER_NAME = "IMDB Provider (Stuckless)";
	public static final String PROVIDER_ICON_URL = "http://i.media-imdb.com/images/nb15/logo2.gif";

	
	public String getIconUrl() {
		return PROVIDER_ICON_URL;
	}

	public String getId() {
		return PROVIDER_ID;
	}

	public String getName() {
		return PROVIDER_NAME;
	}

	public List<IVideoSearchResult> search(int searchType, String arg) throws Exception {
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
			throw new Exception("Seach Type Not Supported: " + searchType);
		}
		return results;
	}

	private List<IVideoSearchResult> returnSingleResult(String redirectUrl) throws IOException {
		List<IVideoSearchResult> result = new ArrayList<IVideoSearchResult>();
		
		IVideoMetaData md = getMetaData(redirectUrl);
		VideoSearchResult vsr = new VideoSearchResult();
		vsr.setId(md.getProviderDataUrl());
		vsr.setTitle(md.getTitle());
		vsr.setYear(md.getYear());
		vsr.setResultType(IVideoSearchResult.RESULT_TYPE_EXACT_MATCH);
		
		// the IMDBMovieMetaData implements the IVideoSearchResult interface
		result.add(vsr);
		
		return result;
	}

	public IVideoMetaData getMetaData(String providerDataUrl) throws IOException {
		try {
			String url = providerDataUrl;
			IMDBMovieMetaDataParser parser = new IMDBMovieMetaDataParser(url);
			parser.parse();
			if (parser.hasError()) throw new IOException("Failed to Parse MetaData for url: " + url);
			return parser.getMetatData();
		} catch (SAXException e) {
			log.error("Failed to getMetaData for url: " + providerDataUrl);
			throw new IOException("Failed to parse providerDataUrl: " + providerDataUrl, e);
		}
	}

	public IVideoMetaData getMetaData(IVideoSearchResult result) throws Exception {
		return getMetaData(result.getId());
	}
}
