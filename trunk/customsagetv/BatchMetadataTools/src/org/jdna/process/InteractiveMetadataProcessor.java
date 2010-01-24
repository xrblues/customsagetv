package org.jdna.process;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;
import org.jdna.media.metadata.SearchQuery.Field;

import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.progress.IProgressMonitor;
import sagex.phoenix.progress.ProgressTracker;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.IMediaResourceVisitor;
import sagex.phoenix.vfs.MediaFolderTraversal;
import sagex.phoenix.vfs.filters.IResourceFilter;

/**
 * Interactive Processor allows a subclass to select a result from the given search results.
 * 
 * @author seans
 *
 */
public abstract class InteractiveMetadataProcessor {
    private Logger                                 log         = Logger.getLogger(this.getClass());

    private MediaType                              forcedType  = null;
    private PersistenceOptions                     options     = null;
    private IMediaMetadataPersistence              persistence = null;
    private Map<MediaType, IMediaMetadataProvider> providers   = null;
    
    protected enum State {Search, Save, Ignore};
    private State state = State.Search;
    
    public InteractiveMetadataProcessor(MediaType forceType, Map<MediaType, IMediaMetadataProvider> providers, IMediaMetadataPersistence persistence, PersistenceOptions options) {
        this.forcedType = forceType;
        this.providers = providers;
        this.persistence = persistence;
        this.options = options;
    }

    public void process(final IMediaResource res, boolean recurse, final IResourceFilter filter, final ProgressTracker<MetadataItem> monitor) {
        try {
            monitor.beginTask("Processing Media Files...", IProgressMonitor.UNKNOWN);
            IMediaResourceVisitor vis = new IMediaResourceVisitor() {
                public boolean visit(IMediaResource res) {
                    if (!monitor.isCancelled()) {
                        if (filter.accept(res)) {
                            if (res instanceof IMediaFile) {
                                monitor.setTaskName("Processing: " + res.getTitle());

                                SearchQuery query = SearchQueryFactory.getInstance().createQuery(res);
                                if (forcedType != null) {
                                    if (query!=null) {
                                        log.info("Forcing media type on query to be " + forcedType);
                                        query.setMediaType(forcedType);
                                    }
                                }

                                if (query == null) {
                                    log.warn("Failed to create a metadata search query for: " + res);
                                    monitor.addFailed(new MetadataItem((IMediaFile)res, query, options, null, persistence, null), "Failed to create a metadata search query for: " + res);
                                } else {
                                    state=State.Search;
                                    query.set(Field.QUERY, query.get(Field.RAW_TITLE));
                                    scanMediaFile((IMediaFile) res, query, monitor);
                                }
                            }
                        } else {
                            log.debug("Resource: " + res + " was not accepted by filter.");
                        }
                    } else {
                        log.info("Scan was cancelled. Aborting...");
                    }

                    return !monitor.isCancelled();
                }
            };

            MediaFolderTraversal.walk(res, recurse, vis);
        } finally {
            monitor.done();
        }
    }

    public void scanMediaFile(IMediaFile mf, SearchQuery query, ProgressTracker<MetadataItem> monitor) {
        while (state==State.Search) {
            IMediaMetadataProvider provider = providers.get(query.getMediaType());
            if (provider==null) {
                monitor.setTaskName("No Provider for Media Type: " + provider);
                throw new RuntimeException("Can't process media files without a provider!");
            }
            
            IMetadataSearchResult result = null;
            monitor.setTaskName("Searching for: " + query.get(Field.QUERY));
            List<IMetadataSearchResult> results=null;
            try {
                results = searchByTitle(query, query.get(Field.QUERY), provider);
            } catch (Exception e1) {
                log.error("Search Failed", e1);
            }

            result = selectResult(mf, query, results, monitor);
            
            if (state==State.Ignore) {
                monitor.addFailed(new MetadataItem(mf, query, options, provider, persistence, null), "User Skipped");
                break;
            }
            
            // when selected, set the sate to save
            if (result!=null) {
                state=State.Save;
            }
        
            if (state==State.Save) {
                IMediaMetadata md = null;
                try {
                    md = provider.getMetaData(result);
                } catch (Exception e1) {
                    monitor.addFailed(new MetadataItem(mf, query, options, provider, persistence, null), "Fetch Error: " + e1.getMessage(), e1);
                    return;
                }
                
                if (md != null) {
                    try {
                        persistMetadata(query, md, mf, options);
                        monitor.addSuccess(new MetadataItem(mf, query, options, provider, persistence, md));
                    } catch (Exception e) {
                        monitor.addFailed(new MetadataItem(mf, query, options, provider, persistence, md), "Failed to save metadata");
                    }
                } else {
                    monitor.addFailed(new MetadataItem(mf, query, options, provider, persistence, md), "Failed to find a metadata match");
                }
            }
        }
    }

    /**
     * This method should return a selected result from the list, or null, if no result is selected.  This method can change
     * the state of the processor to Save, Search or Ignore.  If Ignore is set, then this result is skipped, and the method must return null.
     * if Save is set, then a result must be returned.  If search is selected, then the the SearchQuery can be be modified, and null must
     * be returned.  When a state of Search is used, then a new query will be issues using the modified search query.
     * 
     * @param mf
     * @param query
     * @param results
     * @param monitor
     * @return selected result of null, if no result selected.
     */
    protected abstract IMetadataSearchResult selectResult(IMediaFile mf, SearchQuery query, List<IMetadataSearchResult> results, ProgressTracker<MetadataItem> monitor);

    private void persistMetadata(SearchQuery query, IMediaMetadata md, IMediaFile file, PersistenceOptions options) throws Exception {
        persistence.storeMetaData(md, file, options);
    }

    private List<IMetadataSearchResult> searchByTitle(SearchQuery query, String searchTitle, IMediaMetadataProvider provider) throws Exception {
        SearchQuery newQuery = SearchQuery.copy(query);
        newQuery.set(Field.QUERY, searchTitle);
        return provider.search(newQuery);
    }
    
    protected void setState(State newState) {
        this.state = newState;
    }
}
