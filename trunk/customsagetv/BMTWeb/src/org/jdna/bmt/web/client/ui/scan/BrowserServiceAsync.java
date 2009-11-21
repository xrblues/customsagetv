package org.jdna.bmt.web.client.ui.scan;


import java.util.List;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.media.GWTMediaSearchResult;
import org.jdna.bmt.web.client.media.GWTPersistenceOptions;
import org.jdna.bmt.web.client.media.GWTProviderInfo;
import org.jdna.bmt.web.client.ui.util.ServiceReply;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BrowserServiceAsync {
    void scan(ScanOptions options, AsyncCallback<String> callback);
    void loadMetadata(GWTMediaFile mediaFile, AsyncCallback<GWTMediaMetadata> callback);
    void getMetadata(GWTMediaSearchResult result, GWTPersistenceOptions options,AsyncCallback<GWTMediaMetadata> callback);
    void searchForMetadata(GWTMediaFile item, SearchQueryOptions options, AsyncCallback<List<GWTMediaSearchResult>> jsonReply);
    void getStatus(String id, AsyncCallback<ProgressStatus> status);
    void saveMetadata(GWTMediaFile file, SaveOptions options, AsyncCallback<ServiceReply<GWTMediaFile>> result);
    void getProviders(AsyncCallback<List<GWTProviderInfo>> result);
    void cancelScan(String scanId, AsyncCallback<Void> result);
}
