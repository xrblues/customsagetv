package org.jdna.media.metadata.impl.composite;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;

import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaType;

/**
 * A Container for multiple Metadata Providers.  When a search is performed, it will loop though the providers and search until it
 * finds a good match.  If not good match is found, then it will simply return the search results from all providers.
 * @author seans
 *
 */
public class MetadataProviderContainer implements IMediaMetadataProvider {
    private static final Logger log = Logger.getLogger(MetadataProviderContainer.class);
    
    private IProviderInfo info;
    private List<IMediaMetadataProvider> providers = new ArrayList<IMediaMetadataProvider>();
    
    public MetadataProviderContainer(String id) {
        info = new ProviderInfo(id, "Metadata Provider Container for: " + id , "Contains a list of metadata providers", null);
    }
    
    public IProviderInfo getInfo() {
        return info;
    }

    public IMediaMetadata getMetaData(IMetadataSearchResult result) throws Exception {
        return MediaMetadataFactory.getInstance().getProvider(result.getProviderId(), result.getMediaType()).getMetaData(result);
    }

    public List<IMetadataSearchResult> search(SearchQuery query) throws Exception {
        List<IMetadataSearchResult> list = null;
        List<IMetadataSearchResult> searches = new ArrayList<IMetadataSearchResult>();
        for (IMediaMetadataProvider p : providers) {
            try {
                if (MediaMetadataFactory.getInstance().canProviderAcceptQuery(p, query)) {
                    // do the search using this query
                    list = p.search(query);
                    if (!MediaMetadataFactory.getInstance().isGoodSearch(list)) {
                        log.debug("Not a good search using provider: " + p.getInfo().getId() + "; Query: " + query + "; will try another one.");
                        // add these results to our list.
                        searches.addAll(list);
                    } else {
                        // clear previous search and return this list
                        searches.clear();
                        break;
                    }
                } else {
                    log.debug("Provider: " + p.getInfo().getId() + " cannot handle query: " + query);
                }
            } catch (Exception e) {
                if (p==null || p.getInfo()==null) {
                    log.error("Failed Search; Provider or ProviderInfo is not known; Query: " + query, e);
                } else {
                    log.error("Failed Search using provider: " + p.getInfo().getId() + "; for query: " + query, e);
                }
                    
            }
        }
        
        if (searches!=null && searches.size()>0) {
            // return our non exact match searches...
            return searches;
        }
        
        if (list==null) {
            throw new Exception("You didn't specify any providers that handle the content type for this query: " + query);
        }

        return list;
    }

    public void add(IMediaMetadataProvider provider) {
        providers.add(provider);
    }

    /**
     * Container support all types
     */
    public MediaType[] getSupportedSearchTypes() {
        return MediaType.values();
    }

    public IMediaMetadata getMetaDataById(String id) throws Exception {
        log.warn("getMetadataById() is not implemented for a container");
        return null;
    }
}
