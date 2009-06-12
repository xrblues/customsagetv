package org.jdna.bmt.web.server;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.ui.browser.BrowserService;
import org.jdna.bmt.web.client.ui.browser.MediaItem;
import org.jdna.bmt.web.client.ui.browser.MediaResult;
import org.jdna.bmt.web.client.ui.browser.ScanOptions;
import org.jdna.media.CompositeOrResourceFilter;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.sage.MissingMetadataFilter;
import org.jdna.sage.media.SageMediaFile;
import org.jdna.sage.media.SageMediaFolder;

import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;

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

            StringBuffer types = new StringBuffer("");
            if (options.getScanAll().get()) {
                types.append("TVDB");
            } else {
                if (options.getScanDVD().get()) types.append("DB");
                if (options.getScanTV().get()) types.append("T");
                if (options.getScanVideo().get()) types.append("V");
            }

            CompositeOrResourceFilter filter = new CompositeOrResourceFilter();
            if (options.getScanMissingMetadata().get()) {
                filter.addFilter(new MissingMetadataFilter());
            }
            
            log.debug("Getting MediaFile for: " + types.toString());
            SageMediaFolder folder = new SageMediaFolder("sage://query/" + types);
            log.debug("Folder Items: " + folder.members().size());
            IMediaFolder filtered = folder.filter(filter);
            log.debug("Filtered Items: " + filtered.members().size() + " for filter: " + filter);
            for (IMediaResource mr : filtered.members()) {
                Object o = SageMediaFile.getSageMediaFileObject(mr);
                MediaResult item = new MediaResult();
                item.setMediaId(MediaFileAPI.GetMediaFileID(o));
                item.setMediaTitle(MediaFileAPI.GetMediaTitle(o));
                //item.setPosterUrl("media/poster/"+item.getMediaId() + "?tranform={name:scale,height:120}");
                item.setPosterUrl("media/poster/"+item.getMediaId());
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
        // TODO Auto-generated method stub
        return null;
    }
}
