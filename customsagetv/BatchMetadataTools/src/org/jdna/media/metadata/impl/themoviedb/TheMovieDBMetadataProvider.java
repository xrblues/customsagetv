package org.jdna.media.metadata.impl.themoviedb;

import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Type;

public class TheMovieDBMetadataProvider implements IMediaMetadataProvider {
    public static final String   PROVIDER_ID = "themoviedb.org";
    private static IProviderInfo info        = new ProviderInfo(PROVIDER_ID, "themoviedb.org", "Provider that uses themoviedb as metadata and coverart source.", "http://www.themoviedb.org/images/tmdb/header-logo.png");
    private static final Type[] supportedSearchTypes = new SearchQuery.Type[] {SearchQuery.Type.MOVIE};

    public IProviderInfo getInfo() {
        return info;
    }

    public IMediaMetadata getMetaData(IMediaSearchResult result) throws Exception {
    	return getMetaData(String.format(TheMovieDBItemParser.ITEM_URL, result.getUrl()));  
    }

    public IMediaMetadata getMetaData(String providerDataUrl) throws Exception {
        return new TheMovieDBItemParser(providerDataUrl).getMetadata();
    }

    public List<IMediaSearchResult> search(SearchQuery query) throws Exception {
        if (query.getType() ==  SearchQuery.Type.MOVIE) {
            return new TheMovieDBSearchParser(query).getResults();
        } else {
            throw new Exception("Unsupported Search Type: " + query.getType());
        }
    }

    /**
     * This is the api key provided for the Batch Metadata Tools. Other projects
     * MUST NOT use this key. If you are including these tools in your project,
     * be sure to set the following System property, to set your own key. <code>
     * themoviedb.api_key=YOUR_KEY
     * </code>
     */
    public static Object getApiKey() {
        String key = System.getProperty("themoviedb.api_key");
        if (key == null) key = "d4ad46ee51d364386b6cf3b580fb5d8c";
        return key;
    }

    public IMediaMetadata getMetaDataFromCompositeId(String compositeId) throws Exception, UnsupportedOperationException {
        // sort of convoluted, but we need to get the imdbid info, then get the
        // real movidedb id
        // to get the full details
    	if (compositeId.startsWith("tt")){
	        TheMovieDBItemParser p = new TheMovieDBItemParser(String.format(TheMovieDBItemParser.IMDB_ITEM_URL, compositeId));
	        if (p.getMetadata() != null) {
	            return getMetaData(String.format(TheMovieDBItemParser.ITEM_URL, p.getTheMovieDBID()));
	        } else {
	            throw new Exception("Failed to get metadata by imdb for compositeId: " + compositeId);
	        }
        } else if (StringUtils.isNumeric(compositeId)){
        	//assume it's a tvdb id
        	return getMetaData(compositeId);
        }
    	else
    		return null;
    }

    public Type[] getSupportedSearchTypes() {
        return supportedSearchTypes;
    }
}
