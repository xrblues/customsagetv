package org.jdna.media.metadata.impl.tvdb;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;

import sagex.phoenix.fanart.MediaType;

public class TVDBMetadataProvider implements IMediaMetadataProvider {
    private Logger log = Logger.getLogger(TVDBMetadataProvider.class);
    public static final String   PROVIDER_ID = "tvdb";
    private static IProviderInfo info        = new ProviderInfo(PROVIDER_ID, "thetvdb.com", "Provides Fanart and Metadata from thetvdb.com", "");
    private static final MediaType[] supportedSearchTypes = new MediaType[] {MediaType.TV};

    public IProviderInfo getInfo() {
        return info;
    }

    public IMediaMetadata getMetaData(IMediaSearchResult result) throws Exception {
    	return getMetaDataByUrl(result.getUrl());  
    }

    public List<IMediaSearchResult> search(SearchQuery query) throws Exception {
        if (query.getMediaType() ==  MediaType.TV) {
            return new TVDBSearchParser(query).getResults();
        } else {
            throw new Exception("Unsupported Search Type: " + query.getMediaType());
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
        String key = System.getProperty("thetvdb.api_key");
        if (key == null) key = "5645B594A3F32D27";
        return key;
    }

    public MediaType[] getSupportedSearchTypes() {
        return supportedSearchTypes;
    }

    public String getUrlForId(MetadataID id) throws Exception {
        MediaSearchResult sr = new MediaSearchResult();
        sr.setMetadataId(id);
        for (Map.Entry<String, String> me : id.getArgs().entrySet()) {
            sr.addExtraArg(me.getKey(), me.getValue());
        }
        sr.setProviderId(id.getProvider());
        sr.setUrl(id.getId());
        return sr.getUrlWithExtraArgs();
    }

    public IMediaMetadata getMetaDataByUrl(String url) throws Exception {
        return new TVDBItemParser(url).getMetadata();
    }
}
