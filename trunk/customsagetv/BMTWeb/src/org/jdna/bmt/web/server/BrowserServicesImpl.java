package org.jdna.bmt.web.server;

import java.io.IOException;
import java.util.ArrayList;
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
import org.jdna.media.CompositeOrResourceFilter;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceFilter;
import org.jdna.media.IMediaResourceVisitor;
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
import org.jdna.util.IRunnableWithProgress;
import org.jdna.util.ProgressTracker;
import org.jdna.util.ProgressTrackerManager;
import org.jdna.util.ProgressTracker.FailedItem;

import sagex.SageAPI;
import sagex.api.AiringAPI;
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

    public class Scanner implements IRunnableWithProgress<ProgressTracker<IMediaFile>> {
        private IMediaFolder          folder;
        private IMediaResourceVisitor visitor;

        public Scanner(IMediaFolder folder, IMediaResourceVisitor visitor) {
            this.folder = folder;
            this.visitor = visitor;
        }

        public void run(ProgressTracker<IMediaFile> monitor) {
            try {
                log.debug("Scanning " + folder.members().size() + " items");
                monitor.beginTask("Scanning Media Items", folder.members().size());
                folder.accept(visitor);
            } finally {
                monitor.done();
            }
        }

        public IMediaFolder getFolder() {
            return folder;
        }

        public void setFolder(IMediaFolder folder) {
            this.folder = folder;
        }

        public IMediaResourceVisitor getVisitor() {
            return visitor;
        }

        public void setVisitor(IMediaResourceVisitor visitor) {
            this.visitor = visitor;
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
            CompositeOrResourceFilter filter = new CompositeOrResourceFilter();
            if (options.getScanMissingMetadata().get()) {
                filter.addFilter(new MissingMetadataFilter());
            }

            if (options.getScanMissingPoster().get()) {
                filter.addFilter(new MissingFanartFilter(MediaArtifactType.POSTER));
            }

            if (options.getScanMissingBackground().get()) {
                filter.addFilter(new MissingFanartFilter(MediaArtifactType.BACKGROUND));
            }

            if (options.getScanMissingBanner().get()) {
                filter.addFilter(new MissingFanartFilter(MediaArtifactType.BANNER));
            }

            if (filter.getFilterCount() == 0) {
                // add in an inclusive filter
                filter.addFilter(new IMediaResourceFilter() {
                    public boolean accept(IMediaResource resource) {
                        return true;
                    }
                });
            }

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

            Scanner scanner = new Scanner(folder, scanVisitor);
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
                    malist.add(new GWTMediaArt(ma));
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
                GWTMediaFile item = new GWTMediaFile((IMediaFile) res);
                if (res instanceof SageMediaFile) {
                    Object o = SageMediaFile.getSageMediaFileObject(res);
                    item.setSageMediaFileId(MediaFileAPI.GetMediaFileID(o));
                    if (MediaFileAPI.IsTVFile(o)) {
                        item.setTitle(ShowAPI.GetShowTitle(o));
                        item.setMinorTitle(ShowAPI.GetShowEpisode(o));
                    }
                }
                item.setPosterUrl("media/poster/" + item.getSageMediaFileId() + "?transform=[{name:scale,height:40},{name: reflection}]");
                item.setMessage(errStatus);
                items.add(item);

                // if (MediaFileAPI.IsTVFile(o)) {
                // item.setMediaTitle(item.getMediaTitle() + " - " +
                // ShowAPI.GetShowEpisode(o));
                // }
            }

            status.setItems(items);

            return status;
        } catch (Throwable t) {
            log.error("GetStatus Failed for " + id, t);
            throw new RuntimeException(t);
        }
    }

    public ServiceReply saveMetadata(GWTMediaFile file) {
        IMediaMetadataPersistence persist = new CompositeMediaMetadataPersistence(new SageCustomMetadataPersistence(), new SageShowPeristence(true), new SageTVPropertiesWithCentralFanartPersistence());
        SageMediaFile smf = new SageMediaFile(file.getSageMediaFileId());
        log.debug("Saving File: " + smf.getLocation());
        PersistenceOptions options = new PersistenceOptions();
        options.setOverwriteFanart(true);
        options.setOverwriteMetadata(true);
        try {
            Object sageObject = smf.getSageMediaFileObject(smf);
            if (MediaFileAPI.IsTVFile(sageObject) && !"TV".equals(file.getMetadata().getString(MetadataKey.MEDIA_TYPE))) {
                // moving a file from TV to non TV requiers some manipulation
                log.warn("Moving File from TV to NON TV: " + smf.getLocation());
            }
            
            persist.storeMetaData(file.getMetadata(), smf, options);
            
            ServiceReply reply = new ServiceReply(0, "ok");
            return reply;
        } catch (IOException e) {
            ServiceReply reply = new ServiceReply(99, "Failed: " + e.getMessage());
            return reply;
        }
    }
}
