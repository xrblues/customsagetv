package org.jdna.bmt.web.server;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.media.GWTCastMember;
import org.jdna.bmt.web.client.media.GWTMediaArt;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaResource;
import org.jdna.bmt.web.client.media.GWTMediaSearchResult;
import org.jdna.bmt.web.client.media.GWTPersistenceOptions;
import org.jdna.bmt.web.client.media.GWTProviderInfo;
import org.jdna.bmt.web.client.ui.browser.MetadataService;
import org.jdna.bmt.web.client.ui.browser.PersistenceOptionsUI;
import org.jdna.bmt.web.client.ui.browser.ProgressStatus;
import org.jdna.bmt.web.client.ui.browser.SearchQueryOptions;
import org.jdna.bmt.web.client.ui.util.ServiceReply;
import org.jdna.media.metadata.CompositeMediaMetadataPersistence;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MediaMetadataPersistence;
import org.jdna.media.metadata.MediaMetadataUtils;
import org.jdna.media.metadata.MediaSearchResult;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Field;
import org.jdna.process.MetadataItem;
import org.jdna.process.MetadataProcessor;
import org.jdna.sage.media.SageCustomMetadataPersistence;
import org.jdna.sage.media.SageShowPeristence;
import org.jdna.url.UrlUtil;

import sagex.api.MediaFileAPI;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaArtifactType;
import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.progress.IRunnableWithProgress;
import sagex.phoenix.progress.ProgressTracker;
import sagex.phoenix.progress.ProgressTrackerManager;
import sagex.phoenix.progress.TrackedItem;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaFolder;
import sagex.phoenix.vfs.MediaResourceType;
import sagex.phoenix.vfs.filters.AndResourceFilter;
import sagex.phoenix.vfs.filters.MediaTypeFilter;
import sagex.phoenix.vfs.sage.SageMediaFile;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class MetadataServicesImpl extends RemoteServiceServlet implements MetadataService {
    private Logger log = Logger.getLogger(MetadataServicesImpl.class);
    private ProgressTrackerManager trackerManager = new ProgressTrackerManager();
    private MetadataConfiguration config = GroupProxy.get(MetadataConfiguration.class);

    public MetadataServicesImpl() {
    }
    
    public GWTMediaMetadata getMetadata(GWTMediaSearchResult result, GWTPersistenceOptions mdOptions) {
        IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().getProvider(result.getProviderId(), result.getMediaType());
        try {
            IMediaFile smf = phoenix.api.GetMediaFile(MediaFileAPI.GetMediaFileForID(result.getMediaFileId()));
            
            // create a set of persistence options for just returning metadata
            PersistenceOptions options = new PersistenceOptions();
            options.setUseTitleMasks(mdOptions.isUseTitleMasks());
            options.setImportAsTV(mdOptions.isImportAsTV());
            MediaSearchResult msr = new MediaSearchResult(result);
            return newMetadata(MetadataAPI.normalizeMetadata(smf, prov.getMetaData(msr), options));
        } catch (Exception e) {
            log.error("Metadata Retreival Failed!", e);
            throw new RuntimeException(e);
        }
    }

    public List<GWTProviderInfo> getProviders() {
        List<IMediaMetadataProvider> providers = MediaMetadataFactory.getInstance().getMetaDataProviders();
        List<GWTProviderInfo> info = new ArrayList<GWTProviderInfo>();
        for (IMediaMetadataProvider p : providers) {
            info.add(new GWTProviderInfo(p.getInfo()));
        }
        return info;
    }

    public ProgressStatus[] getScansInProgress() {
        List<ProgressStatus> all = new ArrayList<ProgressStatus>();
        for (String id : trackerManager.getProgressIds()) {
            all.add(getStatus(id));
        }
        return all.toArray(new ProgressStatus[] {});
    }

    public ProgressStatus getStatus(String id) {
        log.debug("Getting Progress Status for: " + id);
        ProgressStatus status = new ProgressStatus();
        status.setProgressId(id);
        
        ProgressTracker<MetadataItem> tracker = (ProgressTracker<MetadataItem>) trackerManager.getProgress(id);
        if (tracker==null) {
            log.debug("No Tracker for Id: " + id);
            status.setIsDone(true);
        } else {
            updateProgressStatus(status, tracker);
        }
        return status;
    }
    
    private void updateProgressStatus(ProgressStatus status, ProgressTracker<MetadataItem> tracker) {
        log.debug("Tracker: "+ tracker.getLabel()+ "; %: " + tracker.internalWorked() + "; total items: " + tracker.getTotalWork() + "; worked: " + tracker.getWorked());
        status.setComplete(tracker.internalWorked());
        status.setIsCancelled(tracker.isCancelled());
        status.setIsDone(tracker.isDone());
        status.setStatus(tracker.getTaskName());
        status.setTotalWork(tracker.getTotalWork());
        status.setWorked(tracker.getWorked());
        status.setSuccessCount(tracker.getSuccessfulItems().size());
        status.setFailedCount(tracker.getFailedItems().size());
        status.setLabel(tracker.getLabel());
        status.setDate(tracker.getLastUpdated());
    }

    public void cancelScan(String id) {
        ProgressTracker<MetadataItem> tracker = (ProgressTracker<MetadataItem>) trackerManager.getProgress(id);
        if (tracker!=null) {
            tracker.setCancelled(true);
            log.debug("Cancelled Scan: " + id);
        }
    }
    
    public void removeScan(String progressId) {
        cancelScan(progressId);
        trackerManager.removeProgress(progressId);
    }

    public GWTMediaResource[] getProgressItems(String progressId, boolean b) {
        log.debug("Getting Items for progress: " + progressId);
        ProgressTracker<MetadataItem> tracker = (ProgressTracker<MetadataItem>) trackerManager.getProgress(progressId);
        LinkedList<TrackedItem<MetadataItem>> items = null;
        if (b) {
            items = tracker.getSuccessfulItems();
        } else {
            items = tracker.getFailedItems();
        }

        List<GWTMediaResource> gwtitems = new ArrayList<GWTMediaResource>();
        for (TrackedItem<MetadataItem> i : items) {
            GWTMediaResource res = BrowsingServicesImpl.convertResource(i.getItem().getFile(), getThreadLocalRequest());
            gwtitems.add(res);
            if (i.getMessage()!=null) {
                res.setMessage(i.getMessage());
            }
        }
        GWTMediaResource all[] = (gwtitems.toArray(new GWTMediaResource[] {}));
        log.debug("Created Reply with " + all.length + " items.");
        return all;
    }

    public GWTMediaMetadata loadMetadata(GWTMediaFile mediaFile) {
        try {
            log.debug("Fetching Current Metadata for Item: " + mediaFile.getSageMediaFileId() + "; " + mediaFile.getTitle());
            
            // TODO: load metadata using EITHER the properties or show metadata
            IMediaMetadataPersistence persist = new CompositeMediaMetadataPersistence(new SageShowPeristence(), new SageCustomMetadataPersistence());
            IMediaMetadata md = persist.loadMetaData(new SageMediaFile(null, phoenix.api.GetSageMediaFile(mediaFile.getSageMediaFileId())));
            return newMetadata(md);
        } catch (Throwable e) {
            log.error("Failed to get metadata: " + mediaFile.getSageMediaFileId() + "; " + mediaFile.getTitle(), e);
            throw new RuntimeException(e);
        }
    }

    public GWTMediaMetadata newMetadata(IMediaMetadata source) {
        GWTMediaMetadata mi = new GWTMediaMetadata();
        if (source != null) {
            MetadataAPI.copy(source, mi);

            if (mi.getCastMembers() != null) {
                List<GWTCastMember> cmlist = new ArrayList<GWTCastMember>();
                for (ICastMember cm : mi.getCastMembers()) {
                    cmlist.add(new GWTCastMember(cm));
                }
                mi.getCastMembers().clear();
                mi.getCastMembers().addAll(cmlist);
            }

            if (mi.getFanart() != null) {
                List<GWTMediaArt> malist = new ArrayList<GWTMediaArt>();
                for (IMediaArt ma : mi.getFanart()) {
                    GWTMediaArt gma = new GWTMediaArt(ma);
                    if (gma.getDownloadUrl()!=null && gma.getDownloadUrl().startsWith("file")) {
                        gma.setLocal(true);
                        File f;
                        try {
                            f = new File(new URI(gma.getDownloadUrl()));
                            gma.setLabel(f.getAbsolutePath());
                            gma.setDownloadUrl(makeLocalMediaUrl(f.getAbsolutePath()));
                        } catch (URISyntaxException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        gma.setLabel(gma.getDownloadUrl());
                        gma.setLocal(false);
                    }
                    malist.add(gma);
                }
                mi.getFanart().clear();
                mi.getFanart().addAll(malist);
            }
        }
        return mi;
    }

    public List<GWTMediaSearchResult> searchForMetadata(GWTMediaFile item, SearchQueryOptions options) {
        List<GWTMediaSearchResult> results = new ArrayList<GWTMediaSearchResult>();
        try {
            MediaType type = MediaType.toMediaType(options.getType().get());
            String prov = options.getProvider().get();
            System.out.println("*** Using Search Provider: " + prov);
            IMediaMetadataProvider provider = null;
            if (prov!=null) {
                provider = MediaMetadataFactory.getInstance().findById(prov); 
            } else {
                provider = MediaMetadataFactory.getInstance().getProvider(null, type);
            }
                
            
            SearchQuery query = new SearchQuery();
            query.setMediaType(type);
            query.set(Field.QUERY, options.getSearchTitle().get());
            query.set(Field.YEAR, options.getYear().get());
            if (type == MediaType.TV) {
                query.set(Field.EPISODE_TITLE,options.getEpisodeTitle().get());
                query.set(Field.EPISODE,options.getEpisode().get());
                query.set(Field.SEASON,options.getSeason().get());
            }
            List<IMetadataSearchResult> sresults = provider.search(query);
            if (sresults==null) throw new Exception("Search Failed for: " + query);
            for (IMetadataSearchResult r : sresults) {
                GWTMediaSearchResult res = new GWTMediaSearchResult();
                res.setId(r.getId());
                res.setMediaType(r.getMediaType());
                res.setProviderId(r.getProviderId());
                res.setScore(r.getScore());
                res.setTitle(r.getTitle());
                res.setUrl(r.getUrl());
                res.setYear(r.getYear());
                res.setMediaFileId(item.getSageMediaFileId());
                res.getExtra().putAll(r.getExtra());
                results.add(res);
            }
        } catch (Exception e) {
            log.warn("WebUI Search failed for: " + options.getSearchQuery(), e);
        }
        return results;
    }

    public String scan(final GWTMediaFolder folder, final PersistenceOptionsUI options) {
        log.debug("Scanning Folder: " + folder);
        ProgressTracker<MetadataItem> tracker = new ProgressTracker<MetadataItem>();
        tracker.setLabel(folder.getTitle());
        
        Map<MediaType, IMediaMetadataProvider> providers = new HashMap<MediaType, IMediaMetadataProvider>();
        providers.put(MediaType.TV, MediaMetadataFactory.getInstance().getProvider(config.getTVProviders(), MediaType.TV));
        providers.put(MediaType.MOVIE, MediaMetadataFactory.getInstance().getProvider(config.getMovieProviders(), MediaType.MOVIE));
        
        IMediaMetadataPersistence persistence = new MediaMetadataPersistence();
        
        PersistenceOptions poptions = new PersistenceOptions();
        poptions.setImportAsTV(options.getImportTV().get());
        poptions.setUpdateFanart(options.getUpdateFanart().get());
        poptions.setOverwriteFanart(options.getOverwriteFanart().get());
        poptions.setUpdateMetadata(options.getUpdateMetadata().get());
        poptions.setOverwriteMetadata(options.getOverwriteMetadata().get());
        poptions.setCreateProperties(options.getCreatePropertyFiles().get());
        poptions.setTouchingFiles(!options.getUpdateWizBin().get());
        poptions.setUpdateWizBin(options.getUpdateWizBin().get());
        poptions.setUseTitleMasks(true);
        
        final AndResourceFilter filter = new AndResourceFilter(new MediaTypeFilter(MediaResourceType.ANY_VIDEO));
        final IMediaFolder realFolder = BrowsingServicesImpl.getFolderRef(folder, getThreadLocalRequest());
        
        final MetadataProcessor processor = new MetadataProcessor(null, providers, persistence, poptions);
        processor.setCollectItemsFirst(true);
        String trackerId = trackerManager.runWithProgress(new IRunnableWithProgress<ProgressTracker<MetadataItem>>() {
            public void run(ProgressTracker<MetadataItem> monitor) {
                try {
                    log.info("Starting Scan on folder: " + realFolder.getTitle());
                    processor.setRecurse(options.getIncludeSubDirs().get());
                    processor.addFilter(filter);
                    processor.process(realFolder, monitor);
                } catch (Throwable t) {
                    log.error("Scan Failed!", t);
                } finally {
                    log.info("Scan completed: " + realFolder.getTitle());
                    monitor.done();
                }
            }
        }, tracker);
        return trackerId;
    }



    private String makeLocalMediaUrl(String url) {
        return "media/get?i=" + UrlUtil.encode(url);
    }

    
    public ServiceReply<GWTMediaFile> saveMetadata(GWTMediaFile file, PersistenceOptionsUI uiOptions) {
        Object sageMF = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
        
        IMediaFile smf = phoenix.api.GetMediaFile(sageMF);
        
        log.debug("Saving File: " + smf);
        PersistenceOptions options = new PersistenceOptions();
        options.setOverwriteFanart(uiOptions.getOverwriteFanart().get());
        options.setOverwriteMetadata(uiOptions.getOverwriteMetadata().get());
        options.setImportAsTV(file.getSageRecording().get());
        options.setCreateDefaultSTVThumbnail(uiOptions.getCreateDefaultSTVThumbnail().get());
        options.setCreateProperties(uiOptions.getCreatePropertyFiles().get());
        options.setUpdateFanart(uiOptions.getUpdateFanart().get());
        options.setUpdateMetadata(uiOptions.getUpdateMetadata().get());

        //options.setTouchingFiles(uiOptions.getTouchingFile().get());
        options.setUpdateWizBin(uiOptions.getUpdateWizBin().get());

        // TODO: Should probably ask the use if we want to use the default masks, etc.
        options.setUseTitleMasks(false);

        IMediaMetadataPersistence persist = new MediaMetadataPersistence();
        
        try {
            if (MediaFileAPI.IsTVFile(sageMF) && !file.getSageRecording().get()) {
                // moving a file from TV to non TV requiers some manipulation
                log.warn("Moving File from TV to NON TV: " + smf);
                Object newMF = phoenix.api.RemoveMetadataFromMediaFile(sageMF);
                if (newMF == null) {
                    log.error("Failed strip metadata from TV File: " + file);
                } else {
                    smf = phoenix.api.GetMediaFile(newMF);
                }
            }
            
            // process the fanart images
            for (Iterator<IMediaArt> i = file.getMetadata().getFanart().iterator(); i.hasNext();) {
                IMediaArt ma = i.next();
                if (ma instanceof GWTMediaArt) {
                    GWTMediaArt gma = (GWTMediaArt) ma;
                    if (gma.isLocal()) {
                        log.debug("Skipping Download of Local Fanart: " + gma.getLabel());
                        i.remove();
                    }
                    if (gma.isDeleted() && gma.isLocal()) {
                        try {
                            File f = new File(gma.getLabel());
                            f.delete();
                        } catch (Throwable t) {
                            log.error("Unablet to delete: " + gma.getDownloadUrl());
                        }
                    }
                }
            }
            
            persist.storeMetaData(file.getMetadata(), smf, options);
            
            log.debug("Metadata Saved... Reloading");

            GWTMediaFile newMediaFile = (GWTMediaFile) BrowsingServicesImpl.convertResource(smf, getThreadLocalRequest());
            if (newMediaFile==null) {
                throw new IOException("Unable to create GWT MediaFile from SageMediaFile: " + smf);
            }
            
            newMediaFile.attachMetadata(loadMetadata(newMediaFile));
            
            // return back the new metadata object
            ServiceReply<GWTMediaFile> reply = new ServiceReply<GWTMediaFile>(0, "ok", newMediaFile);
            return reply;
        } catch (IOException e) {
            ServiceReply<GWTMediaFile> reply = new ServiceReply<GWTMediaFile>(99, "Failed: " + e.getMessage(), null);
            return reply;
        }
    }

    public ArrayList<GWTMediaArt> getFanart(GWTMediaFile file, MediaArtifactType artifact) {
        Object sageMF = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
        if (sageMF == null) {
            log.error("Failed to get sage mediafile for: " + file);
            return null;
        }
        ArrayList<GWTMediaArt> files = new ArrayList<GWTMediaArt>();
        log.debug("Getting fanart: " + file.getType() + "; " + artifact + "; " + sageMF);
        String fanart[] = phoenix.api.GetFanartArtifacts(sageMF, null, null, artifact.name(), null, null);
        if (fanart!=null) {
            for (String fa : fanart) {
                GWTMediaArt ma = new GWTMediaArt();
                ma.setDeleted(false);
                ma.setLocal(true);
                ma.setLocalFile(fa);
                ma.setLabel(fa);
                ma.setDisplayUrl(makeLocalMediaUrl(fa));
                files.add(ma);
            }
        }
        return files;
    }

    public GWTMediaArt downloadFanart(GWTMediaFile file, MediaArtifactType artifact, GWTMediaArt ma) {
        Object sageMF = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
        if (sageMF == null) {
            log.error("Failed to get sage mediafile for: " + file);
            return null;
        }
        String fanartDir = phoenix.api.GetFanartArtifactDir(sageMF, null, null, artifact.name(), null, null, true);
        File dir =new File(fanartDir);
        String name = new File(ma.getDownloadUrl()).getName();
        File local = new File(dir, name);
        MediaMetadataUtils.writeImageFromUrl(ma.getDownloadUrl(), local);
        ma.setLocal(true);
        ma.setLocalFile(local.getAbsolutePath());
        ma.setDisplayUrl(makeLocalMediaUrl(local.getAbsolutePath()));
        return ma;
    }

    public boolean deleteFanart(GWTMediaArt art) {
        File f = new File(art.getLocalFile());
        if (f.exists()) {
            log.info("Removing Fanart Image: " + f);
            return f.delete();
        }
        return false;
    }

    public void makeDefaultFanart(GWTMediaFile file, MediaArtifactType type, GWTMediaArt art) {
        Object sageMF = phoenix.api.GetSageMediaFile(file.getSageMediaFileId());
        if (sageMF == null) {
            log.error("Failed to get sage mediafile for: " + file);
            return;
        }
        
        File img=new File(art.getLocalFile());
        if (img.exists()) {
            if (type == MediaArtifactType.POSTER) {
                phoenix.api.SetFanartPoster(sageMF, img);
            } else if (type==MediaArtifactType.BACKGROUND) {
                phoenix.api.SetFanartBackground(sageMF, img);
            } else if (type==MediaArtifactType.BANNER) {
                phoenix.api.SetFanartBanner(sageMF, img);
            }
        }
    }
}
