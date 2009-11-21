package org.jdna.media.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.metadata.IMediaMetadata;
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
import org.jdna.util.ProgressTracker;

public class AutomaticUpdateMetadataVisitor implements IMediaResourceVisitor {
    private static final Logger    log = Logger.getLogger(AutomaticUpdateMetadataVisitor.class);

    private IMediaMetadataProvider provider;
    private IMediaMetadataPersistence persistence;
    private PersistenceOptions options;

    private SearchQuery.Type defaultSearchType;
    
    private ProgressTracker<IMediaFile> tracker;

    public AutomaticUpdateMetadataVisitor(String providerId, IMediaMetadataPersistence persistence, PersistenceOptions options, SearchQuery.Type defaultSearchType, ProgressTracker<IMediaFile> tracker) {
        this.provider = MediaMetadataFactory.getInstance().getProvider(providerId);
        this.options = options;
        this.persistence =  persistence;
        this.defaultSearchType=defaultSearchType;
        this.tracker=tracker;
    }

    public void visit(IMediaResource resource) {
        if (tracker.isCancelled()) {
            log.debug("Tracker is cancelled; Won't accept file: " + resource.getLocation());
            return;
        }
        
        try {
            if (resource.getType() == IMediaResource.Type.File) {
                tracker.setTaskName("Scanning: " + resource.getLocation());
                fetchMetaData((IMediaFile) resource);
            } else {
                log.debug("Not a Media File: " + resource.getLocation());
            }
        } catch (Exception e) {
            log.error("Failed to find/update metadata for resource: " + resource.getLocation(), e);
            tracker.addFailed((IMediaFile) resource, "Failed to find/update metadata for resource: " + resource.getLocation(), e);
            log.error("Failed to visit MediaResource: " + resource.getLocation(), e);
        } finally {
            tracker.worked(1);
        }
    }

    protected void fetchMetaData(IMediaFile file) throws Exception {
        SearchQuery query = null;
        if (defaultSearchType!=null && defaultSearchType==SearchQuery.Type.TV) {
            query = SearchQueryFactory.getInstance().createQuery(file, SearchQuery.Type.TV);
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
        MetadataID mid = ConfigurationManager.getInstance().getMetadataIdForTitle(query.get(SearchQuery.Field.TITLE));
        if (mid!=null) {
            log.debug("Found a Configured MetadataID: " + mid + "; for Title: " + query.get(SearchQuery.Field.TITLE));
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
                IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().getProvider(mid.getKey());
                MediaSearchResult sr = new MediaSearchResult();
                for (SearchQuery.Field f : SearchQuery.Field.values()) {
                    if (f==SearchQuery.Field.TITLE) continue;
                    String s = query.get(f);
                    if (!StringUtils.isEmpty(s)) {
                        sr.addExtraArg(f.name(), s);
                        mid.addArg(f.name(), s);
                    }
                }
                
                sr.setMetadataId(mid);
                sr.setProviderId(mid.getKey());
                sr.setScore(1.0f);
                sr.setTitle(query.get(SearchQuery.Field.TITLE));
                sr.setUrl(prov.getUrlForId(mid));
                result=sr;
                log.debug("Returning this handcrafted result: " + sr);
            }
        } else {
            if (!MediaMetadataFactory.getInstance().isGoodSearch(results)) {
                log.debug("Not very sucessful with the search for: " + query);
                String oldName = query.get(SearchQuery.Field.TITLE);
                String newName = MediaMetadataUtils.cleanSearchCriteria(oldName, true);
                if (!oldName.equals(newName)) {
                    log.debug("We'll try again using: " + newName);
                    SearchQuery newQuery = new SearchQuery(query);
                    newQuery.set(SearchQuery.Field.TITLE, newName);
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
        log.debug("Nothing Found for query: " + query + "; File: " + file.getLocation());
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
