package org.jdna.media.metadata;

import sagex.phoenix.fanart.IMetadataSearchResult;

public interface IMediaSearchResult extends IMetadataSearchResult {
    public void setProviderId(String providerId);
    
    /**
     * Returns a Url that is understood by the provider, in order to fetch the complete details for a given search result
     * 
     * @return
     */
    public String getUrl();
    
    public MetadataID getMetadataId();
}
