package org.jdna.media.metadata.impl.composite;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.HasFindByIMDBID;
import org.jdna.media.metadata.HasIMDBID;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Field;

import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaType;

public class CompositeMetadataProvider implements IMediaMetadataProvider {
    public static final int     MODE_PREFER_SEARCHER = 1;
    public static final int     MODE_PREFER_DETAILS  = 2;

    private static final Logger log                  = Logger.getLogger(CompositeMetadataProvider.class);

    private IProviderInfo       info;
    private String              searcherProviderId;
    private String              detailsProviderId;
    private int                 mode                 = 0;

    public CompositeMetadataProvider(CompositeMetadataConfiguration conf) {
        info = new ProviderInfo(conf.getId(), conf.getName(), conf.getDescription(), conf.getIconUrl());
        this.searcherProviderId = conf.getSearchProviderId();
        this.detailsProviderId = conf.getDetailProviderId();
        this.mode = conf.getCompositeMode();
        log.info("Composite Provider Created with id: " + getInfo().getId() + "; search: " + searcherProviderId + "; details: " + detailsProviderId + "; mode: " + mode);
    }

    public IProviderInfo getInfo() {
        return info;
    }

    public IMediaMetadata getMetaData(IMetadataSearchResult result) throws Exception {
        log.info("Getting Details from the Primary Search Provider: " + searcherProviderId);
        // get the primary results from the search
        IMediaMetadata searcher = MediaMetadataFactory.getInstance().getProvider(searcherProviderId, result.getMediaType()).getMetaData(result);

        if (searcher!=null) {
            // copy the imdbid from the searcher's metadata to the search result, so that
            // a fanart provider can potentially use it.
            if (result instanceof MediaSearchResult) {
                if (((MediaSearchResult) result).getIMDBId() == null) {
                    ((MediaSearchResult)result).setIMDBId(MetadataAPI.getIMDBID(searcher));
                }
            }
        }
        
        // now get the search results from the title search
        IMediaMetadata details = searchDetailsByResult(result);

        if (details==null) {
            log.warn("Details (fanart) lookup failed for result: " + result + " only source metadata will be used.");
            return searcher;
        }
        
        // return the merged details
        return mergeDetails(details, searcher);
    }

    private IMediaMetadata searchDetailsByResult(IMetadataSearchResult result) {
        try {
            IMediaMetadata md = null;
            IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().getProvider(detailsProviderId, result.getMediaType());
            if (prov==null) {
                log.warn("Detail Provider for Composite provider is null.  Detail Provider Id: " + detailsProviderId);
                return null;
            }
            
            // if we can use imdbid for the details, then use it 
            if ((result instanceof HasIMDBID) && (prov instanceof HasFindByIMDBID)) {
                log.info("Attempting to find details using the imdbid: " + ((HasIMDBID) result).getIMDBId());
                md = ((HasFindByIMDBID) prov).getMetadataForIMDBId(((HasIMDBID) result).getIMDBId());
            }
            
            // if we don't have metadata, the find by title/year
            if (md == null) {
                log.info("Attempting to find details using title: " + result.getTitle());
                SearchQuery query = new SearchQuery(result.getMediaType(), result.getTitle());
                query.set(Field.QUERY, result.getTitle());
                query.set(Field.YEAR, result.getYear());
                List<IMetadataSearchResult> results = prov.search(query);
                IMetadataSearchResult res = MediaMetadataFactory.getInstance().getBestResultForQuery(results, query);
                if (res!=null) {
                    md = prov.getMetaData(result);
                } else {
                    log.warn("Failed to get a good result for query: " + query);
                }
            }

            // if we still have metadata, then fire off an error
            if (md==null) {
                throw new Exception("Failed to find details for result: " + result);
            }
            
            // we have metadata, return it
            return md;
        } catch (Exception e) {
            log.warn("searchDetailsByResult() failed for: " + result, e);
        }
        
        return null;
    }

    public List<IMetadataSearchResult> search(SearchQuery query) throws Exception {
        String providerId = getInfo().getId();
        log.debug("Searching using composite provider: " + providerId);
        List<IMetadataSearchResult> results = MediaMetadataFactory.getInstance().getProvider(searcherProviderId, query.getMediaType()).search(query);

        for (int i = 0; i < results.size(); i++) {
            results.get(i).setProviderId(providerId);
        }

        return results;
    }

    private IMediaMetadata mergeDetails(IMediaMetadata details, IMediaMetadata searcher) {
        log.debug("Merging the metadata from the primary and secondary metadata providers.");
        MediaMetadata md = null;
        IMediaMetadata pri = null;
        IMediaMetadata sec = null;

        if (mode == MODE_PREFER_DETAILS && details != null) {
            pri = details;
            sec = searcher;
        } else {
            pri = searcher;
            sec = details;
        }

        md = new MediaMetadata(pri);
        md.setProviderId(getInfo().getId());
        if (sec == null) return md;

        // now find all fields that null in the primary, and set them from the
        // seconday
        for (MetadataKey k : MetadataKey.values()) {
            String o = pri.getString(k);
            if (isEmpty(o)) {
                md.set(k, sec.getString(k));
            }
        }

        if (sec.getFanart() != null) {
            List<IMediaArt> l1 = md.getFanart();
            for (IMediaArt ma : sec.getFanart()) {
                if (!l1.contains(ma)) {
                    l1.add(ma);
                }
            }
        }

        if (sec.getCastMembers() != null) {
            if (md.getCastMembers() == null || md.getCastMembers().size() == 0) {
                md.getCastMembers().addAll(sec.getCastMembers());
            }
        }

        if (sec.getGenres() != null) {
            List<String> l3 = md.getGenres();
            for (String s : sec.getGenres()) {
                if (!l3.contains(s)) {
                    l3.add(s);
                }
            }
        }

        // set the pimary to be the searcher
        MetadataAPI.setProviderId(md, getInfo().getId());
        MetadataAPI.setProviderDataId(md, MetadataAPI.getProviderDataId(pri));
        return md;
    }

    private boolean isEmpty(Object o) {
        if (o == null || (o.getClass().isArray() && ((Object[]) o).length == 0) || (o instanceof String && ((String) o).trim().length() == 0)) {
            return true;
        }
        return false;
    }

    public MediaType[] getSupportedSearchTypes() {
        return MediaMetadataFactory.getInstance().findById(searcherProviderId).getSupportedSearchTypes();
    }
}
