package org.jdna.bmt.web.client.ui.browser;


import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BrowserServiceAsync {
    void scan(ScanOptions options, AsyncCallback<MediaResult[]> callback);
    void getMediaItem(MediaResult result, AsyncCallback<MediaItem> callback);

}
