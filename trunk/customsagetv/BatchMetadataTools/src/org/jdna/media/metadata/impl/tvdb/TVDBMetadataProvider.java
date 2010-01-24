package org.jdna.media.metadata.impl.tvdb;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;

import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaType;

public class TVDBMetadataProvider implements IMediaMetadataProvider {
    public static final String   PROVIDER_ID = "tvdb";
    private static IProviderInfo info        = new ProviderInfo(PROVIDER_ID, "thetvdb.com", "Provides Fanart and Metadata from thetvdb.com", "");
    private static final MediaType[] supportedSearchTypes = new MediaType[] {MediaType.TV};

    public IProviderInfo getInfo() {
        return info;
    }

    public IMediaMetadata getMetaData(IMetadataSearchResult result) throws Exception {
        if (MetadataUtil.hasMetadata(result)) return MetadataUtil.getMetadata(result);

        return new TVDBItemParser(result).getMetadata();
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
        if (query.getMediaType() ==  MediaType.TV) {
            return new TVDBSearchParser(query).getResults();
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
        String key = System.getProperty("thetvdb.api_key");
        if (key == null) key = "5645B594A3F32D27";
        return key;
    }

    public MediaType[] getSupportedSearchTypes() {
        return supportedSearchTypes;
    }
}
