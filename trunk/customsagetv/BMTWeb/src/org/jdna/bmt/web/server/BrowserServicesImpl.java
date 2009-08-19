package org.jdna.bmt.web.server;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.media.GWTCastMember;
import org.jdna.bmt.web.client.media.GWTMediaArt;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaSearchResult;
import org.jdna.bmt.web.client.ui.browser.BrowserService;
import org.jdna.bmt.web.client.ui.browser.ProgressStatus;
import org.jdna.bmt.web.client.ui.browser.ScanOptions;
import org.jdna.bmt.web.client.ui.util.ServiceReply;
import org.jdna.media.ConditionalResourceFilter;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.MovieResourceFilter;
import org.jdna.media.metadata.CompositeMediaMetadataPersistence;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;
import org.jdna.media.metadata.SearchQuery.Field;
import org.jdna.media.metadata.SearchQuery.Type;
import org.jdna.media.metadata.impl.sage.CentralFanartPersistence;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesPersistence;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesWithCentralFanartPersistence;
import org.jdna.media.util.AutomaticUpdateMetadataVisitor;
import org.jdna.media.util.FilteredResourceVisitor;
import org.jdna.media.util.ProgressTrackerVisitor;
import org.jdna.sage.MissingFanartFilter;
import org.jdna.sage.MissingMetadataFilter;
import org.jdna.sage.UpdateMediaFileTimeStamp;
import org.jdna.sage.media.SageCustomMetadataPersistence;
import org.jdna.sage.media.SageMediaFile;
import org.jdna.sage.media.SageMediaFolder;
import org.jdna.sage.media.SageShowPeristence;
import org.jdna.util.IProgressMonitor;
import org.jdna.util.MediaScanner;
import org.jdna.util.ProgressTracker;
import org.jdna.util.ProgressTrackerManager;
import org.jdna.util.ProgressTracker.FailedItem;

import sagex.SageAPI;
import sagex.api.AiringAPI;
import sagex.api.Global;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;
import sagex.phoenix.fanart.MediaArtifactType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class BrowserServicesImpl extends RemoteServiceServlet implements BrowserService {
    private static final Logger log = Logger.getLogger(BrowserServicesImpl.class);

    public class ScanProgressTracker extends ProgressTracker<IMediaFile> {
        private ScanOptions options = null;

        public ScanProgressTracker(ScanOptions options) {
            super();
            this.options = options;
        }

        public ScanProgressTracker(ScanOptions options, IProgressMonitor monitor) {
            super(monitor);
            this.options = options;
        }

        public ScanOptions getOptions() {
            return options;
        }
    }

    public BrowserServicesImpl() {
        ServicesInit.init();
    }

    public String scan(ScanOptions options) {
        try {
            // update the options for Remote API debugging
            if (SageAPI.isRemote()) {
                options.getDontUpdate().set(true);
            }

            MetadataConfiguration metadataConfig = new MetadataConfiguration();

            // setup the types
            StringBuffer types = new StringBuffer("L");
            if (options.getScanDVD().get()) types.append("DB");
            if (options.getScanTV().get()) types.append("T");
            if (options.getScanVideo().get()) types.append("V");

            // if nothing is checked, then scan all
            if (types.length() == 1) {
                types.append("TVDB");
            }

            // setup the filters
            ConditionalResourceFilter filter = new ConditionalResourceFilter();
            if (options.getScanMissingMetadata().get()) {
                filter.or(new MissingMetadataFilter());
            }

            if (options.getScanMissingPoster().get()) {
                filter.or(new MissingFanartFilter(MediaArtifactType.POSTER));
            }

            if (options.getScanMissingBackground().get()) {
                filter.or(new MissingFanartFilter(MediaArtifactType.BACKGROUND));
            }

            if (options.getScanMissingBanner().get()) {
                filter.or(new MissingFanartFilter(MediaArtifactType.BANNER));
            }

            filter.and(new MovieResourceFilter());

            // do the work
            log.debug("Getting MediaFile for: " + types.toString() + "; Filter: " + options.getFilter().get());

            String titleFilter = "";
            String filt = options.getFilter().get();
            if (filt != null && filt.trim().length() > 0) {
                titleFilter = "?filterTitle=" + filt;
            }

            SageMediaFolder folder = new SageMediaFolder("sage://query/" + types + titleFilter);
            if (folder.members().size() == 0) {
                return null;
            }
            log.debug("Folder Items: " + folder.members().size());

            // track scanning status
            ScanProgressTracker tracker = new ScanProgressTracker(options);
            FilteredResourceVisitor scanVisitor = null;

            if (options.getDontUpdate().get()) {
                // for search/scan only
                ProgressTrackerVisitor scanOnlyVisitor = new ProgressTrackerVisitor(tracker);

                // use scan only visitor/tracker
                scanVisitor = new FilteredResourceVisitor(filter, scanOnlyVisitor);
            } else {
                CompositeMediaMetadataPersistence persistence = new CompositeMediaMetadataPersistence();
                if (options.getUpdateMetadata().get()) {
                    persistence.add(new SageTVPropertiesPersistence());
                    persistence.add(new SageCustomMetadataPersistence());
                    persistence.add(new SageShowPeristence(options.getImportTV().get()));
                }

                if (options.getUpdateFanart().get()) {
                    persistence.add(new CentralFanartPersistence());
                }

                persistence.add(new UpdateMediaFileTimeStamp());

                PersistenceOptions persistOptions = new PersistenceOptions();
                persistOptions.setOverwriteFanart(options.getOverwriteFanart().get());
                persistOptions.setOverwriteMetadata(options.getOverwriteMetadata().get());

                // for automatic updates
                AutomaticUpdateMetadataVisitor autoUpdater = new AutomaticUpdateMetadataVisitor(metadataConfig.getDefaultProviderId(), persistence, persistOptions, SearchQuery.Type.MOVIE, tracker);

                // use the automatic updater
                scanVisitor = new FilteredResourceVisitor(filter, autoUpdater);
            }

            MediaScanner scanner = new MediaScanner(folder, scanVisitor);
            String id = ProgressTrackerManager.getInstance().runWithProgress(scanner, tracker);
            return id;
        } catch (Throwable e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            throw new RuntimeException("Scan Service Failed!", e);
        }
    }

    public GWTMediaMetadata loadMetadata(GWTMediaFile mediaFile) {
        try {
            log.debug("Fetching Current Metadata for Item: " + mediaFile.getSageMediaFileId() + "; " + mediaFile.getTitle());
            SageMediaFile smf = new SageMediaFile(mediaFile.getSageMediaFileId());
            IMediaMetadataPersistence persist = new CompositeMediaMetadataPersistence(new SageCustomMetadataPersistence(), new SageShowPeristence());
            IMediaMetadata md = persist.loadMetaData(smf);
            
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

    public List<GWTMediaSearchResult> searchForMetadata(GWTMediaFile mediaFile, String provider) {
        if (provider == null) {
            provider = new MetadataConfiguration().getDefaultProviderId();
        }

        GWTMediaMetadata item = mediaFile.getMetadata();

        SearchQuery query = null;
        try {
            if (!StringUtils.isEmpty(item.getString(MetadataKey.MEDIA_TITLE)) && !StringUtils.isEmpty(item.getString(MetadataKey.MEDIA_TYPE))) {
                query = new SearchQuery();
                if ("TV".equals(item.getString(MetadataKey.MEDIA_TYPE))) {
                    query.setType(Type.TV);
                    query.set(Field.DISC, item.getString(MetadataKey.DVD_DISC));
                    query.set(Field.EPISODE, item.getString(MetadataKey.EPISODE));
                    query.set(Field.EPISODE_TITLE, item.getString(MetadataKey.EPISODE_TITLE));
                    query.set(Field.SEASON, item.getString(MetadataKey.SEASON));
                } else {
                    query.setType(Type.MOVIE);
                }
                query.set(Field.TITLE, item.getString(MetadataKey.MEDIA_TITLE));
            } else {
                log.debug("Searching Using Sage Media File");
                SageMediaFile smf = new SageMediaFile(mediaFile.getSageMediaFileId());
                log.debug("Sage Media File Created");
                query = SearchQueryFactory.getInstance().createQuery(smf);
                log.debug("Sage Query Created");
            }

            log.info("WebUI Search: " + query);

            IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().getProvider(provider);
            List<IMediaSearchResult> results = prov.search(query);
            List<GWTMediaSearchResult> reply = new ArrayList<GWTMediaSearchResult>();
            for (IMediaSearchResult res : results) {
                GWTMediaSearchResult rnew = new GWTMediaSearchResult();
                rnew.setUrl(res.getUrl());
                rnew.setProviderId(res.getProviderId());
                rnew.setScore(res.getScore());
                rnew.setTitle(res.getTitle());
                rnew.setYear(res.getYear());
                rnew.setId(res.getMetadataId());
                rnew.setMediaFileId(mediaFile.getSageMediaFileId());
                reply.add(rnew);
            }
            return reply;
        } catch (Throwable e) {
            if (query != null) {
                log.error("Search Query failed: " + query, e);
            } else {
                log.error("Search failed for item: " + mediaFile.getLocation(), e);
            }
            throw new RuntimeException(e);
        }
    }

    public GWTMediaMetadata getMetadata(GWTMediaSearchResult result) {
        IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().getProvider(result.getProviderId());
        try {
            SageMediaFile smf = new SageMediaFile(result.getMediaFileId());
            return newMetadata(MetadataAPI.normalizeMetadata(smf, prov.getMetaDataByUrl(result.getUrl())));
        } catch (Exception e) {
            log.error("Metadata Retreival Failed!", e);
            throw new RuntimeException(e);
        }
    }

    public ProgressStatus getStatus(String id) {
        try {
            ProgressStatus status = new ProgressStatus();
            ScanProgressTracker tracker = (ScanProgressTracker) ProgressTrackerManager.getInstance().getProgress(id);
            status.setComplete(tracker.internalWorked());
            status.setStatus(tracker.getTaskName());
            status.setIsDone(tracker.isDone());
            status.setIsCancelled(tracker.isCancelled());
            status.setTotalWork(tracker.getTotalWork());
            status.setWorked(tracker.getWorked());

            Queue queue = null;
            tracker.getSuccessfulItems();
            if (tracker.getOptions().getDontUpdate().get()) {
                queue = tracker.getSuccessfulItems();
            } else {
                queue = tracker.getFailedItems();
            }

            List<GWTMediaFile> items = new ArrayList<GWTMediaFile>();

            for (int i = 0; i < 10; i++) {
                String errStatus = null;
                IMediaResource res = null;
                if (tracker.getOptions().getDontUpdate().get()) {
                    res = (IMediaResource) queue.poll();
                } else {
                    FailedItem<IMediaFile> failed = (FailedItem<IMediaFile>) queue.poll();
                    if (failed != null) {
                        res = failed.getItem();
                        errStatus = failed.getMessage();
                    }
                }

                if (res == null) {
                    break;
                }

                log.debug("Handing Resources: " + res);
                GWTMediaFile item = createGWTMediaFile(res);
                item.setMessage(errStatus);
                items.add(item);

            }

            status.setItems(items);

            return status;
        } catch (Throwable t) {
            log.error("GetStatus Failed for " + id, t);
            throw new RuntimeException(t);
        }
    }

    private String makeLocalMediaUrl(String url) {
        return "media/get?i=" + URLEncoder.encode(url);
    }
    
    private GWTMediaFile createGWTMediaFile(IMediaResource res) {
        GWTMediaFile item = new GWTMediaFile((IMediaFile) res);
        if (res instanceof SageMediaFile) {
            Object o = SageMediaFile.getSageMediaFileObject(res);
            item.setSageMediaFileId(MediaFileAPI.GetMediaFileID(o));
            if (MediaFileAPI.IsTVFile(o)) {
                item.getSageRecording().set(true);
                item.setTitle(ShowAPI.GetShowTitle(o));
                item.setMinorTitle(ShowAPI.GetShowEpisode(o));
            }
            
            // add in default fanart locations
            GWTMediaArt art = new GWTMediaArt();
            String file =phoenix.api.GetFanartPoster(o);
            if (file!=null) {
                File f = new File(file);
                art.setDownloadUrl(makeLocalMediaUrl(f.getAbsolutePath()));
                art.setLabel(f.getAbsolutePath());
                art.setExists(f.exists());
                art.setLocal(true);
                item.setDefaultPoster(art);
            }

            art = new GWTMediaArt();
            file = phoenix.api.GetFanartBackground(o);
            if (file!=null) {
                File f = new File(file);
                art.setDownloadUrl(makeLocalMediaUrl(f.getAbsolutePath()));
                art.setLabel(f.getAbsolutePath());
                art.setExists(f.exists());
                art.setLocal(true);
                item.setDefaultBackground(art);
            }

            art = new GWTMediaArt();
            file = phoenix.api.GetFanartBanner(o);
            if (file!=null) {
                File f = new File(file);
                art.setDownloadUrl(makeLocalMediaUrl(f.getAbsolutePath()));
                art.setLabel(f.getAbsolutePath());
                art.setExists(f.exists());
                art.setLocal(true);
                item.setDefaultBanner(art);
            }
            
            
            // add in default fanart locations
            item.setDefaultPosterDir(phoenix.api.GetFanartPosterPath(o));
            item.setDefaultBackgroundDir(phoenix.api.GetFanartBackgroundPath(o));
            item.setDefaultBannerDir(phoenix.api.GetFanartBannerPath(o));
            
            Object airing = MediaFileAPI.GetMediaFileAiring(o);
            Object origShow = AiringAPI.GetShow(airing);
            if (airing!=null) {
                item.setAiringId(String.valueOf(AiringAPI.GetAiringID(airing)));
            }
            if (origShow!=null) {
                item.setShowId(ShowAPI.GetShowExternalID(origShow));
            }
        }
        item.setPosterUrl("media/poster/" + item.getSageMediaFileId() + "?transform=[{name:scale,height:40},{name: reflection}]");
        return item;
    }
    
    private SageMediaFile createSageMediaFile(String uri) {
        try {
            File f = new File(new URI(uri));
            Object mf = MediaFileAPI.GetMediaFileForFilePath(f);
            return new SageMediaFile(mf);
        } catch (Exception e) {
            log.error("Failed to get SageMediaFile for: " + uri);
            return null;
        }
        
    }

    public ServiceReply<GWTMediaFile> saveMetadata(GWTMediaFile file) {
        IMediaMetadataPersistence persist = new CompositeMediaMetadataPersistence(new SageCustomMetadataPersistence(), new SageShowPeristence(file.getSageRecording().get()), new SageTVPropertiesWithCentralFanartPersistence());
        SageMediaFile smf = new SageMediaFile(file.getSageMediaFileId());
        log.debug("Saving File: " + smf.getLocation());
        PersistenceOptions options = new PersistenceOptions();
        options.setOverwriteFanart(true);
        options.setOverwriteMetadata(true);
        try {
            Object sageObject = smf.getSageMediaFileObject(smf);
            if (MediaFileAPI.IsTVFile(sageObject) && !file.getSageRecording().get()) {
                // moving a file from TV to non TV requiers some manipulation
                log.warn("Moving File from TV to NON TV: " + smf.getLocation());
                Object newMF = phoenix.api.RemoveMetadataFromMediaFile(sageObject);
                if (newMF == null) {
                    log.error("Failed strip metadata from TV File: " + file.getLocation());
                } else {
                    smf = new SageMediaFile(newMF);
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

            smf = createSageMediaFile(file.getLocation().toURI());
            
            // touch the file and tell sage to reload the metadata
            smf.touch();
            Global.RunLibraryImportScan(false);
            
            if (smf==null) {
                throw new IOException("Unable to reload MediaFile after metadata was saved.");
            }
            GWTMediaFile newMediaFile = createGWTMediaFile(smf);
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
}
