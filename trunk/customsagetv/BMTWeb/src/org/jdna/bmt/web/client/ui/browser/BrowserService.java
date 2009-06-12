package org.jdna.bmt.web.client.ui.browser;



import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("browser")
public interface BrowserService extends RemoteService {
    public MediaResult[] scan(ScanOptions options);
    public MediaItem getMediaItem(MediaResult result);
}
