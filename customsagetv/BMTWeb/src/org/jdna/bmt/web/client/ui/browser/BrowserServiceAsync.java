package org.jdna.bmt.web.client.ui.browser;


import java.util.List;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaSearchResult;
import org.jdna.bmt.web.client.ui.util.ServiceReply;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BrowserServiceAsync {
    void scan(ScanOptions options, AsyncCallback<String> callback);
    void loadMetadata(GWTMediaFile mediaFile, AsyncCallback<GWTMediaMetadata> callback);
    void getMetadata(GWTMediaSearchResult result, AsyncCallback<GWTMediaMetadata> callback);
    void searchForMetadata(GWTMediaFile item, String provider, AsyncCallback<List<GWTMediaSearchResult>> jsonReply);
    void getStatus(String id, AsyncCallback<ProgressStatus> status);
    void saveMetadata(GWTMediaFile file, AsyncCallback<ServiceReply> result);
}
