package org.jdna.bmt.web.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.ui.browser.BrowserService;
import org.jdna.bmt.web.client.ui.browser.MediaItem;
import org.jdna.bmt.web.client.ui.browser.MediaResult;
import org.jdna.bmt.web.client.ui.browser.MediaSearchResult;
import org.jdna.bmt.web.client.ui.browser.ProgressStatus;
import org.jdna.bmt.web.client.ui.browser.ScanOptions;
import org.jdna.bmt.web.client.ui.browser.MediaItem.NonEditField;
import org.jdna.media.CompositeOrResourceFilter;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceFilter;
import org.jdna.media.metadata.CompositeMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;
import org.jdna.media.metadata.SearchQuery.Field;
import org.jdna.media.metadata.SearchQuery.Type;
import org.jdna.media.metadata.impl.sage.FanartStorage;
import org.jdna.media.util.AutomaticUpdateMetadataVisitor;
import org.jdna.media.util.FilteredResourceVisitor;
import org.jdna.media.util.ProgressTrackerVisitor;
import org.jdna.sage.MissingFanartFilter;
import org.jdna.sage.MissingMetadataFilter;
import org.jdna.sage.media.SageCustomMetadataPersistence;
import org.jdna.sage.media.SageMediaFile;
import org.jdna.sage.media.SageMediaFolder;
import org.jdna.sage.media.SageShowPeristence;
import org.jdna.util.IRunnableWithProgress;
import org.jdna.util.ProgressTracker;
import org.jdna.util.ProgressTrackerManager;

import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;
import sagex.phoenix.fanart.MediaArtifactType;
import sagex.phoenix.fanart.MediaType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class BrowserServicesImpl extends RemoteServiceServlet implements BrowserService {
    private static final Logger log = Logger.getLogger(BrowserServicesImpl.class);

    public BrowserServicesImpl() {
        ServicesInit.init();
    }

    public String scan(ScanOptions options) {
        try {
            MetadataConfiguration metadataConfig = new MetadataConfiguration();
            
            // setup the types
            StringBuffer types = new StringBuffer("");
            if (options.getScanDVD().get()) types.append("DB");
            if (options.getScanTV().get()) types.append("T");
            if (options.getScanVideo().get()) types.append("V");

            // if nothing is checked, then scan all
            if (types.length() == 0) {
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

            final SageMediaFolder folder = new SageMediaFolder("sage://query/" + types + titleFilter);
            log.debug("Folder Items: " + folder.members().size());

            CompositeMediaMetadataPersistence persistence = new CompositeMediaMetadataPersistence();
            PersistenceOptions persistOptions = new PersistenceOptions();
            persistOptions.setOverwriteFanart(options.getOverwriteFanart().get());
            persistOptions.setOverwriteMetadata(options.getOverwriteMetadata().get());
            
            
            final ProgressTrackerVisitor tracker = new ProgressTrackerVisitor();
            final FilteredResourceVisitor visitor = new FilteredResourceVisitor(filter, tracker);
            AutomaticUpdateMetadataVisitor autoUpdater = new AutomaticUpdateMetadataVisitor(metadataConfig.getDefaultProviderId(), persistence, persistOptions, SearchQuery.Type.MOVIE, tracker );

            // Start the scan in a new thread, we'll poll the thread for updates
            IRunnableWithProgress<ProgressTracker<IMediaFile>> runnable = new IRunnableWithProgress<ProgressTracker<IMediaFile>>() {
                public void run(ProgressTracker<IMediaFile> monitor) {
                    log.debug("Scanning " + folder.members().size() + " items");
                    monitor.beginTask("Scanning Media Items", folder.members().size());
                    folder.accept(visitor);
                    monitor.done();
                }
            };

            String id = ProgressTrackerManager.getInstance().runWithProgress(runnable, tracker);
            return id;
        } catch (Throwable e) {
            log.error(e);
            throw new RuntimeException("Scan Service Failed!", e);
        }
    }

    public MediaItem getMediaItem(MediaResult result) {
        try {
            log.debug("Fetching Current Metadata for Item: " + result.getMediaId() + "; " + result.getMediaTitle());
            SageMediaFile smf = new SageMediaFile(result.getMediaId());
            IMediaMetadataPersistence persist = new CompositeMediaMetadataPersistence(new SageCustomMetadataPersistence(), new SageShowPeristence());
            IMediaMetadata md = persist.loadMetaData(smf);
            return createMediaItem(smf, md);
        } catch (Throwable e) {
            log.error("Failed to get metadata: " + result.getMediaId() + "; " + result.getMediaTitle(), e);
            throw new RuntimeException(e);
        }
    }

    public MediaItem createMediaItem(SageMediaFile smf, IMediaMetadata md) {
        Object file = smf.getSageMediaFileObject(smf);

        MediaItem mi = new MediaItem(md);
        mi.setSageMediaItemString(String.valueOf(SageMediaFile.getSageMediaFileObject(smf)));

        mi.getNonEditField(NonEditField.FILE_URI).set(smf.getLocationUri().toString());
        mi.getNonEditField(NonEditField.MEDIA_ID).set(String.valueOf(MediaFileAPI.GetMediaFileID(smf.getSageMediaFileObject(smf))));

        mi.getNonEditField(NonEditField.POSTER_COUNT).set(String.valueOf(sizeOf(phoenix.api.GetFanartPosters(file))));
        mi.getNonEditField(NonEditField.BACKGROUND_COUNT).set(String.valueOf(sizeOf(phoenix.api.GetFanartBackgrounds(file))));
        mi.getNonEditField(NonEditField.BANNER_COUNT).set(String.valueOf(sizeOf(phoenix.api.GetFanartBanners(file))));

        mi.getNonEditField(NonEditField.POSTER_URI).set(String.valueOf(phoenix.api.GetFanartPosterPath(file)));
        mi.getNonEditField(NonEditField.BACKGROUND_URI).set(String.valueOf(phoenix.api.GetFanartBackgroundPath(file)));
        mi.getNonEditField(NonEditField.BANNER_URI).set(String.valueOf(phoenix.api.GetFanartBannerPath(file)));

        log.debug("Returning metadata");
        return mi;
    }

    private int sizeOf(Object[] items) {
        if (items == null) {
            return 0;
        }
        return items.length;
    }

    public List<MediaSearchResult> searchForMetadata(MediaItem item, String provider) {
        if (provider == null) {
            provider = new MetadataConfiguration().getDefaultProviderId();
        }

        SearchQuery query = null;
        try {
        if (!StringUtils.isEmpty((String) item.get(MetadataKey.MEDIA_TITLE)) && !StringUtils.isEmpty((String) item.get(MetadataKey.MEDIA_TYPE))) {
            query = new SearchQuery();
            if ("TV".equals(item.get(MetadataKey.MEDIA_TYPE))) {
                query.setType(Type.TV);
                query.set(Field.DISC, (String) item.get(MetadataKey.DVD_DISC));
                query.set(Field.EPISODE, (String) item.get(MetadataKey.EPISODE));
                query.set(Field.EPISODE_TITLE, (String) item.get(MetadataKey.EPISODE_TITLE));
                query.set(Field.SEASON, (String) item.get(MetadataKey.SEASON));
            } else {
                query.setType(Type.MOVIE);
            }
            query.set(Field.TITLE, (String) item.get(MetadataKey.MEDIA_TITLE));
        } else {
            log.debug("Searching Using Sage Media File");
            SageMediaFile smf = new SageMediaFile(NumberUtils.toInt(item.getNonEditField(NonEditField.MEDIA_ID).get(), 0));
            log.debug("Sage Media File Created");
            query = SearchQueryFactory.getInstance().createQuery(smf);
            log.debug("Sage Query Created");
        }

        log.info("WebUI Search: " + query);

        IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().getProvider(provider);
            List<IMediaSearchResult> results = prov.search(query);
            List<MediaSearchResult> reply = new ArrayList<MediaSearchResult>();
            for (IMediaSearchResult res : results) {
                MediaSearchResult rnew = new MediaSearchResult();
                rnew.setUrl(res.getUrl());
                rnew.setProviderId(res.getProviderId());
                rnew.setScore(res.getScore());
                rnew.setTitle(res.getTitle());
                rnew.setYear(res.getYear());
                rnew.setId(res.getMetadataId());
                reply.add(rnew);
            }
            return reply;
        } catch (Throwable e) {
            if (query!=null) {
                log.error("Search Query failed: " + query, e);
            } else {
                log.error("Search failed for item: " + item.getSageMediaItemString(), e);
            }
            throw new RuntimeException(e);
        }
    }

    public MediaItem getMediaItem(MediaSearchResult result) {
        IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().getProvider(result.getProviderId());
        try {
            return new MediaItem(prov.getMetaDataByUrl(result.getUrl()));
        } catch (Exception e) {
            log.error("Metadata Retreival Failed!");
            throw new RuntimeException(e);
        }
    }

    public ProgressStatus getStatus(String id) {
        ProgressStatus status = new ProgressStatus();
        ProgressTracker<IMediaResource> tracker = (ProgressTracker<IMediaResource>) ProgressTrackerManager.getInstance().getProgress(id);
        status.setComplete(tracker.internalWorked());
        status.setStatus(tracker.getTaskName());
        status.setIsDone(tracker.isDone());
        status.setIsCancelled(tracker.isCancelled());
        status.setTotalWork(tracker.getTotalWork());
        status.setWorked(tracker.getWorked());
        Queue<IMediaResource> queue = tracker.getSuccessfulItems();
        for (int i = 0; i < 10; i++) {
            IMediaResource res = queue.poll();
            if (res == null) {
                break;
            }
            log.debug("Handing Resources: " + res);
            Object o = SageMediaFile.getSageMediaFileObject(res);
            if (o == null) {
                log.debug("Excluding Null Item");
                continue;
            }
            MediaResult item = new MediaResult();
            item.setMediaId(MediaFileAPI.GetMediaFileID(o));
            if (item.getMediaId() == 0) {
                log.debug("Excluding Invalid Item: " + o);
                continue;
            }
            item.setMediaTitle(MediaFileAPI.GetMediaTitle(o));
            item.setPosterUrl("media/poster/" + item.getMediaId() + "?transform=[{name:scale,height:40},{name: reflection}]");
            // item.setPosterUrl("media/poster/"+item.getMediaId());
            if (MediaFileAPI.IsTVFile(o)) {
                item.setMediaTitle(item.getMediaTitle() + " - " + ShowAPI.GetShowEpisode(o));
            }
            status.getItems().add(item);
        }
        return status;
    }
}
