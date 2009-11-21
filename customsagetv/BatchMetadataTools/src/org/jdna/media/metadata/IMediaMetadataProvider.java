package org.jdna.media.metadata;

import java.util.List;

public interface IMediaMetadataProvider {
    public IProviderInfo getInfo();
    public IMediaMetadata getMetaData(IMediaSearchResult result) throws Exception;
    public IMediaMetadata getMetaDataByUrl(String url) throws Exception;
    public List<IMediaSearchResult> search(SearchQuery query) throws Exception;
    public SearchQuery.Type[] getSupportedSearchTypes();
    public String getUrlForId(MetadataID id) throws Exception;
}
