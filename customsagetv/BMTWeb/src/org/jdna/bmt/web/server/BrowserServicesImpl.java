package org.jdna.bmt.web.server;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.ui.browser.BrowserService;
import org.jdna.bmt.web.client.ui.browser.MediaItem;
import org.jdna.bmt.web.client.ui.browser.MediaResult;
import org.jdna.bmt.web.client.ui.browser.ScanOptions;
import org.jdna.bmt.web.client.ui.browser.MediaItem.NonEditField;
import org.jdna.media.CompositeOrResourceFilter;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceFilter;
import org.jdna.media.metadata.CompositeMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.sage.MissingFanartFilter;
import org.jdna.sage.MissingMetadataFilter;
import org.jdna.sage.media.SageCustomMetadataPersistence;
import org.jdna.sage.media.SageMediaFile;
import org.jdna.sage.media.SageMediaFolder;
import org.jdna.sage.media.SageShowPeristence;

import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;
import sagex.phoenix.Phoenix;
import sagex.phoenix.fanart.FanartUtil;
import sagex.phoenix.fanart.MediaArtifactType;

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

    public MediaResult[] scan(ScanOptions options) {
        try {
            List<MediaResult> items = new LinkedList<MediaResult>();

            // setup the types
            StringBuffer types = new StringBuffer("");
            if (options.getScanAll().get()) {
                types.append("TVDB");
            } else {
                if (options.getScanDVD().get()) types.append("DB");
                if (options.getScanTV().get()) types.append("T");
                if (options.getScanVideo().get()) types.append("V");
            }
            
            // if nothing is checked, then scan all
            if (types.length()==0) {
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
            
            if (filter.getFilterCount()==0) {
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
            if (filt!=null && filt.trim().length()>0) {
                titleFilter = "?filterTitle=" + filt;
            }
            
            SageMediaFolder folder = new SageMediaFolder("sage://query/" + types + titleFilter);
            log.debug("Folder Items: " + folder.members().size());
            IMediaFolder filtered = folder.filter(filter);
            log.debug("Filtered Items: " + filtered.members().size() + " for filter: " + filter);
            for (IMediaResource mr : filtered.members()) {
                Object o = SageMediaFile.getSageMediaFileObject(mr);
                if (o==null) {
                    log.debug("Excluding Null Item");
                    continue;
                }
                MediaResult item = new MediaResult();
                item.setMediaId(MediaFileAPI.GetMediaFileID(o));
                if (item.getMediaId()==0) {
                    log.debug("Excluding Invalid Item: " + o);
                    continue;
                }
                item.setMediaTitle(MediaFileAPI.GetMediaTitle(o));
                item.setPosterUrl("media/poster/"+item.getMediaId() + "?transform=[{name:scale,height:40},{name: reflection}]");
                //item.setPosterUrl("media/poster/"+item.getMediaId());
                if (MediaFileAPI.IsTVFile(o)) {
                    item.setMediaTitle(item.getMediaTitle() + " - " + ShowAPI.GetShowEpisode(o));
                }
                items.add(item);
            }
            log.debug("Returning " + items.size() + " Media Files");
            return items.toArray(new MediaResult[items.size()]);
        } catch (Throwable e) {
            log.error(e);
            throw new RuntimeException("Scan Service Failed!", e);
        }
    }

    public MediaItem getMediaItem(MediaResult result) {
        try {
            log.debug("Fetching Current Metadata for Item: " + result.getMediaId() + "; " + result.getMediaTitle());
            SageMediaFile smf = new SageMediaFile(result.getMediaId());
            Object file = smf.getSageMediaFileObject(smf);
            IMediaMetadataPersistence persist = new CompositeMediaMetadataPersistence(new SageCustomMetadataPersistence(), new SageShowPeristence());
            
            IMediaMetadata md = persist.loadMetaData(smf);
            MediaItem mi = new MediaItem(md);
            mi.setSageMediaItemString(String.valueOf(SageMediaFile.getSageMediaFileObject(smf)));
            
            mi.getNonEditField(NonEditField.FILE_URI).set(smf.getLocationUri().toString());
            mi.getNonEditField(NonEditField.MEDIA_ID).set(String.valueOf(result.getMediaId()));
            
            mi.getNonEditField(NonEditField.POSTER_COUNT).set(String.valueOf(sizeOf(phoenix.api.GetFanartPosters(file))));
            mi.getNonEditField(NonEditField.BACKGROUND_COUNT).set(String.valueOf(sizeOf(phoenix.api.GetFanartBackgrounds(file))));
            mi.getNonEditField(NonEditField.BANNER_COUNT).set(String.valueOf(sizeOf(phoenix.api.GetFanartBanners(file))));

            mi.getNonEditField(NonEditField.POSTER_URI).set(String.valueOf(phoenix.api.GetFanartPosterPath(file)));
            mi.getNonEditField(NonEditField.BACKGROUND_URI).set(String.valueOf(phoenix.api.GetFanartBackgroundPath(file)));
            mi.getNonEditField(NonEditField.BANNER_URI).set(String.valueOf(phoenix.api.GetFanartBannerPath(file)));
            
            log.debug("Returning metadata");

            System.out.println("Has Banners: " + phoenix.api.HasFanartBanner(file));
            System.out.println("Banner: " + phoenix.api.GetFanartBanner(file));
            System.out.println("Banners: " + phoenix.api.GetFanartBanners(file));
            

            return mi;
        } catch (Throwable e) {
            log.error("Failed to get metadata: " + result.getMediaId() + "; " + result.getMediaTitle(), e);
            throw new RuntimeException(e);
        }
    }

    private int sizeOf(Object[] items) {
        if (items==null) {
            return 0;
        }
        return items.length;
    }
}
