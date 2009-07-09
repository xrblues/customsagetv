package org.jdna.bmt.web.client.ui.browser;



import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("browser")
public interface BrowserService extends RemoteService {
    public String scan(ScanOptions options);
    public MediaItem getMediaItem(MediaResult result);
    public MediaItem getMediaItem(MediaSearchResult result);
    public List<MediaSearchResult> searchForMetadata(MediaItem item, String provider);
    public ProgressStatus getStatus(String id);
}
