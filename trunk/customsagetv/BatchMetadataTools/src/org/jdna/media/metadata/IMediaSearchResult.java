package org.jdna.media.metadata;

public interface IMediaSearchResult {
    /**
     * returns the provider id that created this result so that we can know which provider can accept this result.
     * 
     * @return
     */
    public String getProviderId();
    
    public String getTitle();

    public String getYear();

    public SearchResultType getResultType();

    /**
     * Returns a Url that is understood by the provider, in order to fetch the complete details for a given search result
     * 
     * @return
     */
    public String getUrl();
}
