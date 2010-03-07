package org.jdna.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.ExcludeMediaFilter;
import org.jdna.media.MediaConfiguration;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MediaMetadataPersistence;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;
import org.jdna.media.metadata.SearchQuery.Field;

import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.progress.IProgressMonitor;
import sagex.phoenix.progress.ProgressTracker;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.IMediaResourceVisitor;
import sagex.phoenix.vfs.MediaFolderTraversal;
import sagex.phoenix.vfs.VirtualMediaFolder;
import sagex.phoenix.vfs.filters.AndResourceFilter;
import sagex.phoenix.vfs.filters.IResourceFilter;

public class MetadataProcessor {
    private Logger                                 log         = Logger.getLogger(this.getClass());

    private MediaType                              forcedType  = null;
    private PersistenceOptions                     options     = null;
    private IMediaMetadataPersistence              persistence = null;
    private Map<MediaType, IMediaMetadataProvider> providers   = null;
    private AndResourceFilter filter =  new AndResourceFilter();
    private boolean recurse = false;
    private String defaultQueryArgs = null;

    private MetadataConfiguration metadataConfig;
    private MediaConfiguration mediaConfig;
    private boolean collectItemsFirst = false;
    private int totalItems = IProgressMonitor.UNKNOWN;
    
    public MetadataProcessor(PersistenceOptions options) {
        init(null, null, null, options);
    }
    
    public MetadataProcessor(MediaType forceType, Map<MediaType, IMediaMetadataProvider> providers, IMediaMetadataPersistence persistence, PersistenceOptions options) {
        init(forceType, providers, persistence, options);
    }
    
    private void init(MediaType forceType, Map<MediaType, IMediaMetadataProvider> providers, IMediaMetadataPersistence persistence, PersistenceOptions options) {
        this.forcedType = forceType;
        this.providers = providers;
        this.persistence = persistence;
        this.options = options;
        this.metadataConfig = GroupProxy.get(MetadataConfiguration.class);
        this.mediaConfig = GroupProxy.get(MediaConfiguration.class);

        if (this.providers==null) {
            this.providers = new HashMap<MediaType, IMediaMetadataProvider>();

            String tv = metadataConfig.getTVProviders();
            this.providers.put(MediaType.TV, MediaMetadataFactory.getInstance().getProvider(tv, MediaType.TV));
    
            String movie = metadataConfig.getMovieProviders();
            this.providers.put(MediaType.MOVIE, MediaMetadataFactory.getInstance().getProvider(movie, MediaType.MOVIE));
        }
        
        if (this.persistence==null) {
            this.persistence = new MediaMetadataPersistence();
        }
        
        // if you are not overwriting metadata, then only process missing metadata
        if (!options.isOverwriteMetadata()) {
            log.info("Metadata Processor will only scan for items that are missing metadata.");
            includeOnlyMissingMetadata();
        }
        
        if (!StringUtils.isEmpty(mediaConfig.getExcludeVideoDirsRegex())) {
            String regex = mediaConfig.getExcludeVideoDirsRegex();
            try {
                Pattern p = Pattern.compile(regex);
                if (p!=null) {
                    log.info("Using Exclude Regex: " + regex);
                    addFilter(new ExcludeMediaFilter(p));
                }
            } catch (Exception e) {
                log.warn("Exclude Regex is not valid: Regex: " + regex, e);
            }
        }
    }
    
    public void addFilter(IResourceFilter filt) {
        this.filter.addFilter(filt);
    }
    
    public void setRecurse(boolean recurse) {
        this.recurse=recurse;
    }
    
    public void includeOnlyMissingMetadata() {
        addFilter(new MissingMetadataFilter(persistence));
    }

    public void process(final IMediaResource res, final ProgressTracker<MetadataItem> monitor) {
        try {
            IMediaResourceVisitor vis = new IMediaResourceVisitor() {
                public boolean visit(IMediaResource res1) {
                    if (!monitor.isCancelled()) {
                        if (filter.accept(res1)) {
                            if (res1 instanceof IMediaFile) {
                                monitor.setTaskName("Processing: " + res1.getTitle());

                                SearchQuery query = SearchQueryFactory.getInstance().createQuery(res1);
                                if (query!=null && defaultQueryArgs!=null) {
                                    try {
                                        SearchQueryFactory.getInstance().updateQueryFromJSON(query,defaultQueryArgs);
                                    } catch (Exception e) {
                                        log.warn("Failed to update the query using json default query args: " + defaultQueryArgs, e);
                                    }
                                }
                                if (forcedType != null) {
                                    if (query!=null) {
                                        log.info("Forcing media type on query to be " + forcedType);
                                        query.setMediaType(forcedType);
                                    }
                                }

                                if (query == null) {
                                    log.warn("Failed to create a metadata search query for: " + res1);
                                    monitor.addFailed(new MetadataItem((IMediaFile)res1, query, options, null, persistence, null), "Failed to create a metadata search query for: " + res1);
                                } else {
                                    scanMediaFile((IMediaFile) res1, query, monitor);
                                }
                            }
                        } else {
                            log.info("Resource: " + res1 + " was not accepted by filter.");
                            monitor.addSkipped(new MetadataItem((IMediaFile)res1, null, options, null, persistence, null), "Filter excluded this item.");
                        }
                        monitor.worked(1);
                    } else {
                        log.info("Scan was cancelled. Aborting...");
                    }

                    return !monitor.isCancelled();
                }
            };

            IMediaResource toScan = getFolderFor(res);
            monitor.beginTask("Processing Media Files...", totalItems);
            MediaFolderTraversal.walk(toScan, recurse, vis);
        } finally {
            monitor.done();
        }
    }

    private IMediaResource getFolderFor(IMediaResource res) {
        if (collectItemsFirst) {
            log.info("Collecting Items for the scan...");
            if (res instanceof IMediaFile) {
                totalItems = 1;
                return res;
            } else {
                final VirtualMediaFolder mf = new VirtualMediaFolder(null, "Folder");
                IMediaResourceVisitor vis = new IMediaResourceVisitor() {
                    public boolean visit(IMediaResource res) {
                        mf.addMediaResource(res);
                        return true;
                    }
                };
                MediaFolderTraversal.walk(res, recurse, vis);
                totalItems = mf.getChildren().size();
                log.info("Initial Folder has " + totalItems);
                return mf;
            }
        }
        return res;
    }

    public void scanMediaFile(IMediaFile mf, SearchQuery query, ProgressTracker<MetadataItem> monitor) {
        if (query.getMediaType()==null) {
            log.debug("Failed to determine the media type, using Movie");
            query.setMediaType(MediaType.MOVIE);
        }
        if (providers==null) {
            throw new RuntimeException("Misconfiguration!  No Providers were set for the provider!");
        }
        IMediaMetadataProvider provider = providers.get(query.getMediaType());
        if (provider == null) {
            log.warn("No metadata provider was passed for the given media type: " + query.getMediaType());
            monitor.addFailed(new MetadataItem(mf, query, options, provider, persistence, null), "No metadata provider was passed for the given media type: " + query.getMediaType());
            return;
        }

        // if there is a search by id, then use it
        IMediaMetadata md = null;
        if (!StringUtils.isEmpty(query.get(Field.ID)) && !StringUtils.isEmpty(query.get(Field.PROVIDER))) {
            IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().findById(query.get(Field.PROVIDER));
            MediaSearchResult sr = new MediaSearchResult();
            MetadataUtil.copySearchQueryToSearchResult(query, sr);
            sr.setId(query.get(Field.ID));
            sr.setProviderId(query.get(Field.PROVIDER));
            
            try {
                md = prov.getMetaData(sr);
            } catch (Exception e) {
                log.warn("Failed to get metadata by id; " + sr + ";, but will continue to search by title, etc.", e);
                monitor.setTaskName("Failed to get metadata by id; " + sr + ";, but will continue to search by title, etc.");
            }
        }

        // otherwise do a fuzzy logic search by title/year
        if (md == null) {
            try {
                List<IMetadataSearchResult> results = searchByTitle(query, query.get(Field.RAW_TITLE), provider);
                IMetadataSearchResult res = MediaMetadataFactory.getInstance().getBestResultForQuery(results, query);
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
                persistMetadata(md, mf);
                monitor.addSuccess(new MetadataItem(mf, query, options, provider, persistence, md));
            } catch (Exception e) {
                monitor.addFailed(new MetadataItem(mf, query, options, provider, persistence, md), "Failed to save metadata");
            }
        } else {
            log.warn("Failed to find a metadata match for " + mf);
        }
    }

    private void persistMetadata(IMediaMetadata md, IMediaFile file) throws Exception {
        persistence.storeMetaData(md, file, options);
    }

    private List<IMetadataSearchResult> searchByTitle(SearchQuery query, String searchTitle, IMediaMetadataProvider provider) throws Exception {
        SearchQuery newQuery = SearchQuery.copy(query);
        newQuery.set(Field.QUERY, searchTitle);
        return provider.search(newQuery);
    }

    /**
     * @return the defaultQueryArgs
     */
    public String getDefaultQueryArgs() {
        return defaultQueryArgs;
    }

    /**
     * Sets a JSON String of the default query args.  This will be passed to {@link SearchQuery}.updateFromJSON().
     * The default query args will be used on ALL querries, so use with caution, since it could lead to some
     * unintended consequences
     * 
     * @param defaultQueryArgs the defaultQueryArgs to set
     */
    public void setDefaultQueryArgs(String defaultQueryArgs) {
        this.defaultQueryArgs = defaultQueryArgs;
    }

    /**
     * @return the collectItemsFirst
     */
    public boolean isCollectItemsFirst() {
        return collectItemsFirst;
    }

    /**
     * @param collectItemsFirst the collectItemsFirst to set
     */
    public void setCollectItemsFirst(boolean collectItemsFirst) {
        this.collectItemsFirst = collectItemsFirst;
    }
}
