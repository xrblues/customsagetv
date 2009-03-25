package org.jdna.media.util;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MediaMetadataUtils;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;

public class AutomaticUpdateMetadataVisitor implements IMediaResourceVisitor {
    private static final Logger    log = Logger.getLogger(AutomaticUpdateMetadataVisitor.class);

    private IMediaResourceVisitor  updatedHandler;
    private IMediaMetadataProvider provider;

    private IMediaResourceVisitor  notFoundHandler;
    private boolean                overwrite;

    private long                   persistenceOptions;
    private SearchQuery.Type defaultSearchType;

    public AutomaticUpdateMetadataVisitor(String providerId, boolean overwrite, SearchQuery.Type defaultSearchType, IMediaResourceVisitor updatedVisitor, IMediaResourceVisitor notFoundHandler) {
        this.provider = MediaMetadataFactory.getInstance().getProvider(providerId);
        this.updatedHandler = updatedVisitor;
        this.notFoundHandler = notFoundHandler;
        this.overwrite = overwrite;
        this.defaultSearchType=defaultSearchType;
    }

    /**
     * 
     */
    public void visit(IMediaResource resource) {
        if (resource.getType() == IMediaFile.TYPE_FILE) {
            try {
                fetchMetaData((IMediaFile) resource);
            } catch (Exception e) {
                log.error("Failed to find/update metadata for resource: " + resource.getLocationUri(), e);
                notFoundHandler.visit(resource);
            }
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
            file.updateMetadata(provider.getMetaData(result), overwrite);
            if (updatedHandler != null) updatedHandler.visit(file);
        } else {
            handleNotFoundResults(file, query, results);
        }
    }

    protected void handleNotFoundResults(IMediaFile file, SearchQuery query, List<IMediaSearchResult> results) {
        if (notFoundHandler != null) notFoundHandler.visit(file);
    }

    protected IMediaResourceVisitor getNotFoundVisitor() {
        return notFoundHandler;
    }

    protected IMediaResourceVisitor getUpdatedVisitor() {
        return updatedHandler;
    }

    protected boolean isOverwriteEnabled() {
        return overwrite;
    }

    protected IMediaMetadataProvider getProvider() {
        return provider;
    }

    public long getPersistenceOptions() {
        return persistenceOptions;
    }
}
