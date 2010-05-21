package bmt;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MediaMetadataPersistence;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;
import org.jdna.media.metadata.SearchQuery.Field;
import org.jdna.process.MetadataItem;
import org.jdna.process.MetadataProcessor;
import org.jdna.sage.OnDemandConfiguration;

import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.IMetadataSupport;
import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.progress.IProgressMonitor;
import sagex.phoenix.progress.IRunnableWithProgress;
import sagex.phoenix.progress.ProgressTracker;
import sagex.phoenix.progress.ProgressTrackerManager;
import sagex.phoenix.progress.TrackedItem;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaFolder;
import sagex.phoenix.vfs.MediaResourceType;
import sagex.phoenix.vfs.filters.AndResourceFilter;
import sagex.phoenix.vfs.filters.MediaTypeFilter;
import sagex.phoenix.vfs.impl.FileResourceFactory;

/**
 * Allows BMT to provide metadata support to the Phoenix Metadata apis.
 * 
 * @author seans
 * 
 */
public class BMTMetadataSupport implements IMetadataSupport {
    private final Logger log = Logger.getLogger(BMTMetadataSupport.class);
    private OnDemandConfiguration ondemandConfig = GroupProxy.get(OnDemandConfiguration.class);
    private MetadataConfiguration metadataConfig = GroupProxy.get(MetadataConfiguration.class); 
    
    private ProgressTrackerManager trackerManager = new ProgressTrackerManager();
    
    public BMTMetadataSupport() {
        BMT.init();
    }

    public boolean updateMetadataForResult(Object media, IMetadataSearchResult result, Map<String, String> perOptions) {
        try {
            IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().getProvider(result.getProviderId(), result.getMediaType());
            IMediaMetadata md = prov.getMetaData(result);
            IMediaMetadataPersistence persistence = null; 

            // TODO: Does MediaMetadataPersistence handle Airings, i hope so.
            persistence = new MediaMetadataPersistence();
            PersistenceOptions options = createPersistenceOptions(perOptions);
            
            IMediaFile smf = phoenix.api.GetMediaFile(media);
            MetadataAPI.normalizeMetadata(smf, md, options);
            
            // Save the sage properties, the update sage's wiz.bin, then download the fanart.
            persistence.storeMetaData(md, smf, options);
            
            try {
                // TODO: Store this using the new MediaTitles.xml and add a new entry for this airing
            } catch (Exception ex) {
                log.error("Failed to update title mappings!", ex);
            }
        } catch (Exception e) {
            log.error("Failed to update metadata!", e);
        }
        
        return true;
    }
    
    private PersistenceOptions createPersistenceOptions(Map<String, String> perOptions) {
        PersistenceOptions options = new PersistenceOptions();
        options.setImportAsTV(ondemandConfig.getImportTVAsRecordings());
        options.setOverwriteFanart(ondemandConfig.getOverwriteFanart());
        options.setOverwriteMetadata(ondemandConfig.getOverwriteMetadata());
        options.setUseTitleMasks(true);
        options.setCreateProperties(false);
        options.setUpdateWizBin(ondemandConfig.getUpdateWizBin());
        if (ondemandConfig.getUpdateWizBin()) {
            options.setCreateProperties(false);
            options.setTouchingFiles(false);
        } else {
            options.setCreateProperties(true);
            options.setTouchingFiles(true);
        }
        options.setCreateDefaultSTVThumbnail(ondemandConfig.getCreateDefaultSTVThumbnail());
        
        if (perOptions != null) {
            // override with user defined options
            String ofanart = perOptions.get("overwrite-fanart");
            //String ufanart = perOptions.get("update-fanart");
            String ometadata = perOptions.get("overwrite-metadata");
            //String umetadata = perOptions.get("update-metadata");
            if (ofanart!=null) options.setOverwriteFanart(BooleanUtils.toBoolean(ofanart));
            if (ometadata!=null) options.setOverwriteMetadata(BooleanUtils.toBoolean(ometadata));
        }
        
        return options;
    }

    public float getMetadataScanComplete(Object tracker) {
        ProgressTracker<MetadataItem> mt = getTracker(tracker);
        if (mt!=null) {
            return (float) mt.internalWorked();
        }
        return 0;
    }

    public boolean isMetadataScanRunning(Object tracker) {
        ProgressTracker<MetadataItem> mt = getTracker(tracker);
        if (mt!=null) {
            return !(mt.isDone() || mt.isCancelled());
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private ProgressTracker<MetadataItem> getTracker(Object tracker) {
        if (tracker!=null) {
            return (ProgressTracker<MetadataItem>) trackerManager.getProgress((String) tracker);
        }
        return null;
    }
    
    public Object startMetadataScan(Object sageMediaFiles, Map<String, String> perOptions) {
        if (sageMediaFiles==null) {
            log.warn("ignoring scan for null media items");
            return null;
        }
        ProgressTracker<MetadataItem> tracker = new ProgressTracker<MetadataItem>();
        
        IMediaFolder fold = null;
        int size = IProgressMonitor.UNKNOWN;
        if (sageMediaFiles instanceof File) {
            fold = FileResourceFactory.createFolder((File) sageMediaFiles);
        } else {
            size = ((Object[]) sageMediaFiles).length;
            fold = phoenix.api.GetMediaAsFolder((Object[]) sageMediaFiles, "Scan from STV UI");
        }
        
        final IMediaFolder folder = fold;
        tracker.setLabel(folder.getTitle());
        
        Map<MediaType, IMediaMetadataProvider> providers = new HashMap<MediaType, IMediaMetadataProvider>();
        providers.put(MediaType.TV, MediaMetadataFactory.getInstance().getProvider(metadataConfig.getTVProviders(), MediaType.TV));
        providers.put(MediaType.MOVIE, MediaMetadataFactory.getInstance().getProvider(metadataConfig.getMovieProviders(), MediaType.MOVIE));
        
        IMediaMetadataPersistence persistence = new MediaMetadataPersistence();
        PersistenceOptions poptions = createPersistenceOptions(perOptions);
        
        final int itemCount = size;
        final AndResourceFilter filter = new AndResourceFilter(new MediaTypeFilter(MediaResourceType.ANY_VIDEO));
        final MetadataProcessor processor = new MetadataProcessor(null, providers, persistence, poptions);
        processor.setCollectItemsFirst(true);
        String trackerId = trackerManager.runWithProgress(new IRunnableWithProgress<ProgressTracker<MetadataItem>>() {
            public void run(ProgressTracker<MetadataItem> monitor) {
                try {
                    log.info("Starting Scan on folder: " + folder.getTitle());
                    monitor.beginTask("Scanning items...", itemCount);
                    processor.setRecurse(false);
                    processor.addFilter(filter);
                    processor.process(folder, monitor);
                } catch (Throwable t) {
                    log.error("Scan Failed!", t);
                } finally {
                    log.info("Scan completed: " + folder.getTitle());
                    monitor.done();
                }
            }
        }, tracker);
        return trackerId;
    }

    public boolean cancelMetadataScan(Object tracker) {
        ProgressTracker<MetadataItem> mt = getTracker(tracker);
        if (mt!=null) {
            log.info("Cancelling Media Scan: " + tracker);
            mt.setCancelled(true);
        }
        return true;
    }
    
    public IMetadataSearchResult[] getMetadataSearchResults(Object media, String title, String type) {
        try {
            IMediaFile smf = phoenix.api.GetMediaFile(media);
            if (smf==null) {
                log.warn("Failed to convert resource into a vfs resource for: " + media);
                return null;
            }

            // TODO: Update search query to handle Sage Airings
            SearchQuery q = SearchQueryFactory.getInstance().createQuery(smf);
            if (title!=null) {
                log.info("Using Specified title: " + title + " for mediafile: " + media);
                q.set(Field.QUERY, title);
            } else {
                q.set(Field.QUERY, q.get(Field.RAW_TITLE));
            }
            
            if (type!=null) {
                MediaType mt = MediaType.toMediaType(type);
                if (mt!=null) {
                    log.info("Using Specified media type: " + mt + " for mediafile: " + media);
                    q.setMediaType(mt);
                } else {
                    log.warn("failed to convert media type: " + type + " to a valid media type");
                }
            }

            log.info("Metadata Search for: " + q + "; media: " + media);
            IMediaMetadataProvider prov = null;
            if (q.getMediaType() == MediaType.TV) {
                prov = MediaMetadataFactory.getInstance().getProvider(metadataConfig.getTVProviders(), MediaType.TV);
            } else {
                prov = MediaMetadataFactory.getInstance().getProvider(metadataConfig.getMovieProviders(), MediaType.MOVIE);
            }
            
            List<IMetadataSearchResult> l = prov.search(q);
            if (l == null || l.size() == 0) {
                log.debug("No matches for: " + q);
            } else {
                return l.toArray(new IMetadataSearchResult[l.size()]);
            }
        } catch (Exception e) {
            log.warn("Failed to do a metadata lookup for: " + media, e);
        }
        return null;
    }

    public IMetadataSearchResult[] getMetadataSearchResults(Object media) {
        return getMetadataSearchResults(media, null, null);
    }

    public Object[] getFailed(Object progress) {
        LinkedList<TrackedItem<MetadataItem>> items = getFailedItems(progress);
        if (items!=null) {
            IMediaFile files[] = new IMediaFile[items.size()];
            for (int i=0;i<files.length;i++) {
                files[i] = items.get(i).getItem().getFile();
            }
            return files;
        }
        return new Object[] {};
    }

    public int getFailedCount(Object progress) {
        LinkedList<TrackedItem<MetadataItem>> items = getFailedItems(progress);
        if (items!=null) {
            return items.size();
        }
        return 0;
    }

    public Object[] getSkipped(Object progress) {
        LinkedList<TrackedItem<MetadataItem>> items = getSkippedItems(progress);
        if (items!=null) {
            IMediaFile files[] = new IMediaFile[items.size()];
            for (int i=0;i<files.length;i++) {
                files[i] = items.get(i).getItem().getFile();
            }
        }
        return new Object[] {};
    }

    public int getSkippedCount(Object progress) {
        LinkedList<TrackedItem<MetadataItem>> items = getSkippedItems(progress);
        if (items!=null) {
            return items.size();
        }
        return 0;
    }

    public Object[] getSuccess(Object progress) {
        LinkedList<TrackedItem<MetadataItem>> items = getSuccessfulItems(progress);
        if (items!=null) {
            IMediaFile files[] = new IMediaFile[items.size()];
            for (int i=0;i<files.length;i++) {
                files[i] = items.get(i).getItem().getFile();
            }
        }
        return new Object[] {};
    }

    public int getSuccessCount(Object progress) {
        LinkedList<TrackedItem<MetadataItem>> items = getSuccessfulItems(progress);
        if (items!=null) {
            return items.size();
        }
        return 0;
    }

    public LinkedList<TrackedItem<MetadataItem>> getSuccessfulItems(Object tracker) {
        ProgressTracker<MetadataItem> mt = getTracker(tracker);
        if (mt!=null) {
            return mt.getSuccessfulItems();
        }
        return null;
    }
    
    public LinkedList<TrackedItem<MetadataItem>> getFailedItems(Object tracker) {
        ProgressTracker<MetadataItem> mt = getTracker(tracker);
        if (mt!=null) {
            return mt.getFailedItems();
        }
        return null;
    }

    public LinkedList<TrackedItem<MetadataItem>> getSkippedItems(Object tracker) {
        ProgressTracker<MetadataItem> mt = getTracker(tracker);
        if (mt!=null) {
            return mt.getSkippedItems();
        }
        return null;
    }
}
