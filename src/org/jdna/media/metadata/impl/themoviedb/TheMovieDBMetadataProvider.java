package org.jdna.media.metadata.impl.themoviedb;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.HasFindByIMDBID;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;

import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaType;

public class TheMovieDBMetadataProvider implements IMediaMetadataProvider, HasFindByIMDBID {
    private Logger log = Logger.getLogger(this.getClass());
    
    public static final String   PROVIDER_ID = "themoviedb.org";
    private static IProviderInfo info        = new ProviderInfo(PROVIDER_ID, "themoviedb.org", "Provides Fanart and Metadata from themoviedb.org", "http://www.themoviedb.org/images/tmdb/header-logo.png");
    private static final MediaType[] supportedSearchTypes = new MediaType[] {MediaType.MOVIE};

    public IProviderInfo getInfo() {
        return info;
    }

    public IMediaMetadata getMetaData(IMetadataSearchResult result) throws Exception {
        if (MetadataUtil.hasMetadata(result)) return MetadataUtil.getMetadata(result);

        return new TheMovieDBItemParser(result.getUrl()).getMetadata();
    }

    public List<IMetadataSearchResult> search(SearchQuery query) throws Exception {
        // search by ID, if the ID is present
        if (!StringUtils.isEmpty(query.get(SearchQuery.Field.ID))) {
            List<IMetadataSearchResult> res = MetadataUtil.searchById(this, query, query.get(SearchQuery.Field.ID));
            if (res!=null) {
                return res;
            }
        }
        
        // carry on normal search
        if (query.getMediaType() ==  MediaType.MOVIE) {
            return new TheMovieDBSearchParser(query).getResults();
        }
        throw new Exception("Unsupported Search Type: " + query.getMediaType());
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

    public MediaType[] getSupportedSearchTypes() {
        return supportedSearchTypes;
    }

    public IMediaMetadata getMetadataForIMDBId(String imdbid) {
        MediaSearchResult sr = new MediaSearchResult();
        sr.setUrl(String.format(TheMovieDBItemParser.IMDB_ITEM_URL, imdbid));
        try {
            return getMetaData(sr);
        } catch (Exception e) {
            log.warn("Failed to find result for imdb: " + imdbid, e);
        }
        return null;
    }
}
