package org.jdna.media.util;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MediaMetadataUtils;

public class AutomaticUpdateMetadataVisitor implements IMediaResourceVisitor {
    private static final Logger    log = Logger.getLogger(AutomaticUpdateMetadataVisitor.class);

    private IMediaResourceVisitor  updatedHandler;
    private IMediaMetadataProvider provider;

    private IMediaResourceVisitor  notFoundHandler;
    private boolean                agressiveSearching;
    private boolean                overwriteThumbnails;

    private long persistenceOptions;

    public AutomaticUpdateMetadataVisitor(String providerId, boolean aggressive, long persistenceOptions, IMediaResourceVisitor updatedVisitor, IMediaResourceVisitor notFoundHandler) {
        this.provider = MediaMetadataFactory.getInstance().getProvider(providerId);
        this.agressiveSearching = aggressive;
        this.updatedHandler = updatedVisitor;
        this.notFoundHandler = notFoundHandler;
        this.persistenceOptions = persistenceOptions;
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
        String name = MediaMetadataUtils.cleanSearchCriteria(file.getTitle(), false);
        fetchMetaData(file, name);
    }

    protected List<IMediaSearchResult> getSearchResultsForTitle(String name) throws Exception {
        List<IMediaSearchResult> results = provider.search(IMediaMetadataProvider.SEARCH_TITLE, name);
        log.debug(String.format("Searched for: %s; Good: %s", name, isGoodSearch(results)));
        return results;
    }

    public static boolean isGoodSearch(List<IMediaSearchResult> results) {
        return (results.size() > 0 && (results.get(0).getResultType() == IMediaSearchResult.RESULT_TYPE_POPULAR_MATCH || results.get(0).getResultType() == IMediaSearchResult.RESULT_TYPE_EXACT_MATCH));
    }

    protected void fetchMetaData(IMediaFile file, String name) throws Exception {
        List<IMediaSearchResult> results = getSearchResultsForTitle(name);
        if (!isGoodSearch(results)) {
            log.debug("Not very sucessful with the search for: " + name);
            if (agressiveSearching) {
                String oldName = name;
                String newName = MediaMetadataUtils.cleanSearchCriteria(name, true);
                if (!oldName.equals(newName)) {
                    log.debug("We'll try again using: " + newName);

                }
                List<IMediaSearchResult> newResults = getSearchResultsForTitle(newName);
                if (isGoodSearch(newResults)) {
                    log.debug("Our other search returned better results.. We'll use these.");
                    results = newResults;
                }
            }
        }

        if (isGoodSearch(results)) {
            file.updateMetadata(provider.getMetaData(results.get(0)), persistenceOptions);
            if (updatedHandler != null) updatedHandler.visit(file);
        } else {
            handleNotFoundResults(file, name, results);
        }
    }

    protected void handleNotFoundResults(IMediaFile file, String title, List<IMediaSearchResult> results) {
        if (notFoundHandler != null) notFoundHandler.visit(file);
    }

    protected IMediaResourceVisitor getNotFoundVisitor() {
        return notFoundHandler;
    }

    protected IMediaResourceVisitor getUpdatedVisitor() {
        return updatedHandler;
    }

    protected boolean isOverwriteThumbnailsEnabled() {
        return overwriteThumbnails;
    }

    protected IMediaMetadataProvider getProvider() {
        return provider;
    }

    public long getPersistenceOptions() {
        return persistenceOptions;
    }
}
