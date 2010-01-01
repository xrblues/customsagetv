package org.jdna.media.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MediaMetadataUtils;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;

import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.progress.ProgressTracker;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.IMediaResourceVisitor;
import sagex.phoenix.vfs.util.PathUtils;

public class AutomaticUpdateMetadataVisitor implements IMediaResourceVisitor {
    private static final Logger    log = Logger.getLogger(AutomaticUpdateMetadataVisitor.class);

    private IMediaMetadataProvider provider;
    private IMediaMetadataPersistence persistence;
    private PersistenceOptions options;

    private MediaType defaultSearchType;
    
    private ProgressTracker<IMediaFile> tracker;

    public AutomaticUpdateMetadataVisitor(String providerId, IMediaMetadataPersistence persistence, PersistenceOptions options, MediaType defaultSearchType, ProgressTracker<IMediaFile> tracker) {
        this.provider = MediaMetadataFactory.getInstance().getProvider(providerId, defaultSearchType);
        this.options = options;
        this.persistence =  persistence;
        this.defaultSearchType=defaultSearchType;
        this.tracker=tracker;
    }

    public boolean visit(IMediaResource resource) {
        if (tracker.isCancelled()) {
            log.debug("Tracker is cancelled; Won't accept file: " + PathUtils.getLocation(resource));
            return false;
        }
        
        try {
            if (resource instanceof IMediaFile) {
                tracker.setTaskName("Scanning: " + PathUtils.getLocation(resource));
                fetchMetaData((IMediaFile) resource);
            } else {
                log.debug("Not a Media File: " + PathUtils.getLocation(resource));
            }
        } catch (Exception e) {
            log.error("Failed to find/update metadata for resource: " + PathUtils.getLocation(resource), e);
            tracker.addFailed((IMediaFile) resource, "Failed to find/update metadata for resource: " + PathUtils.getLocation(resource), e);
            log.error("Failed to visit MediaResource: " + PathUtils.getLocation(resource), e);
        } finally {
            tracker.worked(1);
        }
        
        return !tracker.isCancelled();
    }

    protected void fetchMetaData(IMediaFile file) throws Exception {
        SearchQuery query = null;
        if (defaultSearchType!=null && defaultSearchType==MediaType.TV) {
            query = SearchQueryFactory.getInstance().createQuery(file, MediaType.TV);
        } else {
            query = SearchQueryFactory.getInstance().createQuery(file);
        }
        fetchMetaData(file, query);
    }

    protected List<IMediaSearchResult> getSearchResultsForTitle(SearchQuery query) throws Exception {
        List<IMediaSearchResult> results = provider.search(query);
        log.debug(String.format("Searched for: %s; Good: %s", query, MediaMetadataFactory.getInstance().isGoodSearch(results)));
        return results;
    }

    protected void fetchMetaData(IMediaFile file, SearchQuery query) throws Exception {
        List<IMediaSearchResult> results = getSearchResultsForTitle(query);
        IMediaSearchResult result = null;
        
        // check to see if there is a configured title id for this query, if so, then use it.
        MetadataID mid = ConfigurationManager.getInstance().getMetadataIdForTitle(query.get(SearchQuery.Field.QUERY));
        if (mid!=null) {
            log.debug("Found a Configured MetadataID: " + mid + "; for Title: " + query.get(SearchQuery.Field.QUERY));
            // see if we have a result that matches
            for (IMediaSearchResult sr : results) {
                if (mid.equals(sr.getMetadataId())) {
                    log.info("Automatically Selecting Result: " + sr.getTitle() + " based on it being in the metadata-titles.properties configuration");
                    result =sr;
                    break;
                }
            }
            
            if (result==null) {
                log.warn("Could not find search result for the metadata-titles id: " + mid + " will create it dynamically.");
                IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().getProvider(mid.getProvider(), query.getMediaType());
                MediaSearchResult sr = new MediaSearchResult();
                for (SearchQuery.Field f : SearchQuery.Field.values()) {
                    if (f==SearchQuery.Field.QUERY) continue;
                    String s = query.get(f);
                    if (!StringUtils.isEmpty(s)) {
                        sr.addExtraArg(f.name(), s);
                        mid.addArg(f.name(), s);
                    }
                }
                
                sr.setMetadataId(mid);
                sr.setProviderId(mid.getProvider());
                sr.setScore(1.0f);
                sr.setTitle(query.get(SearchQuery.Field.QUERY));
                sr.setUrl(prov.getUrlForId(mid));
                result=sr;
                log.debug("Returning this handcrafted result: " + sr);
            }
        } else {
            if (!MediaMetadataFactory.getInstance().isGoodSearch(results)) {
                log.debug("Not very sucessful with the search for: " + query);
                String oldName = query.get(SearchQuery.Field.QUERY);
                String newName = MediaMetadataUtils.cleanSearchCriteria(oldName, true);
                if (!oldName.equals(newName)) {
                    log.debug("We'll try again using: " + newName);
                    SearchQuery newQuery = new SearchQuery(query);
                    newQuery.set(SearchQuery.Field.QUERY, newName);
                    List<IMediaSearchResult> newResults = getSearchResultsForTitle(newQuery);
                    if (MediaMetadataFactory.getInstance().isGoodSearch(newResults)) {
                        log.debug("Our other search returned better results.. We'll use these.");
                        results = newResults;
                    }
                }
            }

            if (MediaMetadataFactory.getInstance().isGoodSearch(results)) {
                result = results.get(0);
            }
        }
        
        if (result!=null) {
            log.info("Automatically Selecting Search Result: " + result.getTitle() + "; Score: " + result.getScore());
            persistence.storeMetaData(provider.getMetaData(result), file, options);
            tracker.addSuccess(file);
        } else {
            handleNotFoundResults(file, query, results);
        }
    }

    protected void handleNotFoundResults(IMediaFile file, SearchQuery query, List<IMediaSearchResult> results) {
        log.debug("Nothing Found for query: " + query + "; File: " + PathUtils.getLocation(file));
        tracker.addFailed(file, "Nothing Found for Query: " + query);
    }

    protected IMediaMetadataProvider getProvider() {
        return provider;
    }
    
    public PersistenceOptions getPersistenceOptions() {
        return options;
    }
    
    public IMediaMetadataPersistence getPersistence() {
        return persistence;
    }
    
    public ProgressTracker<IMediaFile> getProgressTracker() {
        return tracker;
    }
}
