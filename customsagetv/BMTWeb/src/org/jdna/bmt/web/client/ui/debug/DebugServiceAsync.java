package org.jdna.bmt.web.client.ui.debug;

import java.util.Map;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.ui.app.SupportOptions;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DebugServiceAsync {
    public void getMetadata(String source, GWTMediaFile file, AsyncCallback<Map<String, String>> callback);
    public void updateTimestamp(GWTMediaFile file, AsyncCallback<Long> callback);
    public void createSupportRequest(SupportOptions options, AsyncCallback<String> callback);
    void removeMetadataProperties(AsyncCallback<Integer> callback);
    void backupWizBin(AsyncCallback<Void> callback);
    void getWizBinBackups(AsyncCallback<String[]> callback);
}
