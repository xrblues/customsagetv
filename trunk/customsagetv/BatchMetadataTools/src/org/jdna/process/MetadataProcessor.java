package org.jdna.process;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;
import org.jdna.media.metadata.SearchQuery.Field;

import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.progress.IProgressMonitor;
import sagex.phoenix.progress.ProgressTracker;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.IMediaResourceVisitor;
import sagex.phoenix.vfs.MediaFolderTraversal;
import sagex.phoenix.vfs.filters.IResourceFilter;

public class MetadataProcessor {
    private Logger                                 log         = Logger.getLogger(this.getClass());

    private MediaType                              forcedType  = null;
    private PersistenceOptions                     options     = null;
    private IMediaMetadataPersistence              persistence = null;
    private Map<MediaType, IMediaMetadataProvider> providers   = null;

    public MetadataProcessor(MediaType forceType, Map<MediaType, IMediaMetadataProvider> providers, IMediaMetadataPersistence persistence, PersistenceOptions options) {
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
        IMediaMetadataProvider provider = providers.get(query.getMediaType());
        if (provider == null) {
            log.warn("No metadata provider was passed for the given media type: " + query.getMediaType());
            monitor.addFailed(new MetadataItem(mf, query, options, provider, persistence, null), "No metadata provider was passed for the given media type: " + query.getMediaType());
            return;
        }

        // if there is a search by id, then use it
        IMediaMetadata md = null;
        if (!StringUtils.isEmpty(query.get(Field.METADATA_ID))) {
            MetadataID mid = new MetadataID(query.get(Field.METADATA_ID));
            IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().findById(mid.getProvider());
            try {
                md = prov.getMetaDataByUrl(prov.getUrlForId(mid));
            } catch (Exception e) {
                log.warn("Failed to get metadata by id " + mid + ", but will continue to search by title, etc.", e);
                monitor.setTaskName("Failed to get metadata by id " + mid + ", but will continue to search by title, etc.");
            }
        }

        if (md == null && !StringUtils.isEmpty(query.get(Field.SERIES_ID)) && !StringUtils.isEmpty(query.get(Field.SEASON))) {
            MetadataID mid = new MetadataID(query.get(Field.SERIES_ID));
            MediaSearchResult sr = new MediaSearchResult(mid.getProvider(), MediaType.TV, 1.0f);
            sr.setTitle(query.get(Field.RAW_TITLE));
            sr.setYear(query.get(Field.YEAR));
            for (SearchQuery.Field f : SearchQuery.Field.values()) {
                if (!StringUtils.isEmpty(query.get(f))) {
                    sr.addExtraArg(f.name(), query.get(f));
                }
            }
            String url = sr.getUrl();
            IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().findById(mid.getProvider());
            try {
                md = prov.getMetaDataByUrl(url);
            } catch (Exception e) {
                log.warn("Failed to get metadata by series url " + mid + ", but will continue to search by title, etc.", e);
                monitor.setTaskName("Failed to get metadata by series url " + mid + ", but will continue to search by title, etc.");
            }
        }

        // check if there is a search by url

        // otherwise do a fuzzy logic search by title/year
        if (md == null) {
            try {
                List<IMediaSearchResult> results = searchByTitle(query, query.get(Field.RAW_TITLE), provider);
                IMediaSearchResult res = MediaMetadataFactory.getInstance().getBestResultForQuery(results, query);
                if (res == null) {
                    // TODO: Use a config flag to search for secondary title
                    // always, and then chose the best result
                    results = searchByTitle(query, query.get(Field.CLEAN_TITLE), provider);
                    res = MediaMetadataFactory.getInstance().getBestResultForQuery(results, query);
                }

                if (res != null) {
                    md = provider.getMetaData(res);
                } else {
                    monitor.addFailed(new MetadataItem(mf, query, options, provider, persistence, md), "Failed to find a metadata match for title");
                }
            } catch (Exception e) {
                monitor.addFailed(new MetadataItem(mf, query, options, provider, persistence, md), "Search Failed for an unknown reason");
            }
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

    private void persistMetadata(SearchQuery query, IMediaMetadata md, IMediaFile file, PersistenceOptions options) throws Exception {
        persistence.storeMetaData(md, file, options);
    }

    private List<IMediaSearchResult> searchByTitle(SearchQuery query, String searchTitle, IMediaMetadataProvider provider) throws Exception {
        SearchQuery newQuery = SearchQuery.copy(query);
        newQuery.set(Field.QUERY, searchTitle);
        return provider.search(newQuery);
    }
}
