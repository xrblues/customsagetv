package org.jdna.media.metadata;

import java.util.List;

import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaType;

public interface IMediaMetadataProvider {
    public IProviderInfo getInfo();
    public IMediaMetadata getMetaData(IMetadataSearchResult result) throws Exception;
    public List<IMetadataSearchResult> search(SearchQuery query) throws Exception;
    public MediaType[] getSupportedSearchTypes();
}
