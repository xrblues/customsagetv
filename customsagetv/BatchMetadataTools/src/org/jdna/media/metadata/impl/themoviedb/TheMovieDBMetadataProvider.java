package org.jdna.media.metadata.impl.themoviedb;

import java.util.List;

import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.ProviderInfo;

public class TheMovieDBMetadataProvider implements IMediaMetadataProvider {
	public static final String PROVIDER_ID = "themoviedb.com";
	private static IProviderInfo info = new ProviderInfo(PROVIDER_ID, "themoviedb.com", "Provider that uses themoviedb as metadata and coverart source.", "http://www.themoviedb.org/images/tmdb/header-logo.png");

	public IProviderInfo getInfo() {
		return info;
	}

	public IMediaMetadata getMetaData(IMediaSearchResult result) throws Exception {
		return getMetaData(String.format(TheMovieDBItemParser.ITEM_URL, result.getId()));
	}

	public IMediaMetadata getMetaData(String providerDataUrl) throws Exception {
		return new TheMovieDBItemParser(providerDataUrl).getMetadata();
	}

	public List<IMediaSearchResult> search(int searchType, String arg) throws Exception {
		if (searchType==IMediaMetadataProvider.SEARCH_TITLE) {
			return new TheMovieDBSearchParser(searchType, arg).getResults();
		} else {
			throw new Exception("Unsupported Search Type: " + searchType);
		}
	}

	/**
	 * This is the api key provided for the Batch Metadata Tools.  Other projects MUST NOT use this key.  If
	 * you are including these tools in your project, be sure to set the following System property, to 
	 * set your own key.
	 * <code>
	 * themoviedb.api_key=YOUR_KEY
	 * </code>
	 */
	public static Object getApiKey() {
		String key = System.getProperty("themoviedb.api_key");
		if (key==null) key = "d4ad46ee51d364386b6cf3b580fb5d8c";
		return key;
	}
	
	public IMediaMetadata getMetaDataByIMDBId(String imdbId) throws Exception, UnsupportedOperationException {
		// sort of convoluted, but we need to get the imdbid info, then get the real movidedb id
		// to get the full details
		TheMovieDBItemParser p = new TheMovieDBItemParser(String.format(TheMovieDBItemParser.IMDB_ITEM_URL, imdbId));
		if (p.getMetadata()!=null) {
			return getMetaData(String.format(TheMovieDBItemParser.ITEM_URL, p.getTheMovieDBID()));
		} else {
			throw new Exception("Failed to get metadata by imdb for imdbid: " + imdbId);
		}
	}
}
