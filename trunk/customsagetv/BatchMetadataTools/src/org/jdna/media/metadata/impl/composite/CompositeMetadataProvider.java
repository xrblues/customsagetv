package org.jdna.media.metadata.impl.composite;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Type;

public class CompositeMetadataProvider implements IMediaMetadataProvider {
    public static final int                MODE_PREFER_SEARCHER = 1;
    public static final int                MODE_PREFER_DETAILS  = 2;

    private static final Logger            log                  = Logger.getLogger(CompositeMetadataProvider.class);

    private IProviderInfo                  info;
    private String                         searcherProviderId;
    private String                         detailsProviderId;
    private int                            mode                 = 0;

    public CompositeMetadataProvider(CompositeMetadataConfiguration conf) {
        info = new ProviderInfo(conf.getId(), conf.getName(), conf.getDescription(), conf.getIconUrl());
        this.searcherProviderId = conf.getSearchProviderId();
        this.detailsProviderId = conf.getDetailProviderId();
        this.mode = conf.getCompositeMode();
        log.debug("Composite Provider Created with id: " + getInfo().getId() + "; search: " + searcherProviderId + "; details: " + detailsProviderId + "; mode: " + mode);
    }

    public IProviderInfo getInfo() {
        return info;
    }

    public IMediaMetadata getMetaData(IMediaSearchResult result) throws Exception {
        log.debug("Getting Details from the Primary Provider: " + searcherProviderId);
        // get the primary results from the search
        IMediaMetadata searcher = MediaMetadataFactory.getInstance().getProvider(searcherProviderId).getMetaData(result);

        // now get the search results from the title search
        IMediaMetadata details = searchDetailsByResult(result);

        // return the merged details
        return mergeDetails(details, searcher);
    }

    private IMediaMetadata searchDetailsByResult(IMediaSearchResult result) {
        try {
            log.debug("Searching the details provider: " + detailsProviderId + " for movie id: " + result.getMetadataId());
            try {
                IMediaMetadata md = MediaMetadataFactory.getInstance().getProvider(detailsProviderId).getMetaDataById(result.getMetadataId());
                if (md==null) {
                    throw new Exception("Failed to get details by id: " + result.getMetadataId());
                }
                return md;
            } catch (Exception e) {
                log.debug("Searching the details provider: " + detailsProviderId + " for movie title: " + result.getTitle());
                List<IMediaSearchResult> results = MediaMetadataFactory.getInstance().getProvider(detailsProviderId).search(new SearchQuery(result.getTitle()));
                if (MediaMetadataFactory.getInstance().isGoodSearch(results)) {
                    return MediaMetadataFactory.getInstance().getProvider(detailsProviderId).getMetaData(results.get(0));
                }
            }
        } catch (Exception e) {
            log.error("Could not find a valid details for result: " + result);
        }
        return null;
    }

    private IMediaMetadata searchDetailsByTitle(String title) {
        try {
            log.debug("Searching the details provider: " + detailsProviderId + " for movie title: " + title);
            List<IMediaSearchResult> results = MediaMetadataFactory.getInstance().getProvider(detailsProviderId).search(new SearchQuery(title));
            if (MediaMetadataFactory.getInstance().isGoodSearch(results)) {
                return searchDetailsByResult(results.get(0));
            }
        } catch (Exception e) {
            log.error("Failed to find an Exact Match for " + title + " using provider: " + detailsProviderId);
        }
        return null;
    }

    public List<IMediaSearchResult> search(SearchQuery query) throws Exception {
        String providerId = getInfo().getId();
        log.debug("Searching using composite provider: " + providerId);
        List<IMediaSearchResult> results = MediaMetadataFactory.getInstance().getProvider(searcherProviderId).search(query);

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

        if (sec.getFanart()!=null) {
            List<IMediaArt> l1 = md.getFanart();
            for (IMediaArt ma : sec.getFanart()) {
                if (!l1.contains(ma)) {
                    l1.add(ma);
                }
            }
        }

        if (sec.getCastMembers()!=null) {
            if (md.getCastMembers()==null || md.getCastMembers().size()==0) {
                md.getCastMembers().addAll(sec.getCastMembers());
            }
        }

        if (sec.getGenres()!=null) {
            List<String> l3 = md.getGenres();
            for (String s : sec.getGenres()) {
                if (!l3.contains(s)) {
                    l3.add(s);
                }
            }
        }

        // set the pimary to be the searcher
        MetadataAPI.setProviderDataUrl(md, MetadataAPI.getProviderDataUrl(pri));
        MetadataAPI.setProviderDataId(md, MetadataAPI.getProviderDataId(pri));
        return md;
    }

    private boolean isEmpty(Object o) {
        if (o == null || (o.getClass().isArray() && ((Object[]) o).length == 0) || (o instanceof String && ((String) o).trim().length() == 0)) {
            return true;
        } else {
            return false;
        }
    }

    public Type[] getSupportedSearchTypes() {
        return MediaMetadataFactory.getInstance().getProvider(searcherProviderId).getSupportedSearchTypes();
    }

    public IMediaMetadata getMetaDataById(MetadataID id) throws Exception {
        throw new Exception("Search by MetadataID not supported: " + id);
    }

    public IMediaMetadata getMetaDataByUrl(String url) throws Exception {
        IMediaMetadata searcher = MediaMetadataFactory.getInstance().getProvider(searcherProviderId).getMetaDataByUrl(url);
        IMediaMetadata details = null;
        try {
            if (MetadataAPI.getProviderDataId(searcher) != null) {
                details = MediaMetadataFactory.getInstance().getProvider(searcherProviderId).getMetaDataById(new MetadataID(MetadataAPI.getProviderDataId(searcher)));
            }
        } catch (Exception e) {
            log.error("Failed to find details using searcher's metadataid: " + MetadataAPI.getProviderDataId(searcher) + " will try using title search", e);
        }
        details = searchDetailsByTitle(MetadataAPI.getMediaTitle(searcher));
        return mergeDetails(details, searcher);
    }
}
