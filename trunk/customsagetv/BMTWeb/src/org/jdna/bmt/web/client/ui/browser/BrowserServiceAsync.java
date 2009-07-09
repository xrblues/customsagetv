package org.jdna.bmt.web.client.ui.browser;


import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BrowserServiceAsync {
    void scan(ScanOptions options, AsyncCallback<String> callback);
    void getMediaItem(MediaResult result, AsyncCallback<MediaItem> callback);
    void getMediaItem(MediaSearchResult result, AsyncCallback<MediaItem> callback);
    void searchForMetadata(MediaItem item, String provider, AsyncCallback<List<MediaSearchResult>> jsonReply);
    void getStatus(String id, AsyncCallback<ProgressStatus> status);
}
