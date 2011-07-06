package org.jdna.bmt.web.client.ui.debug;

import java.util.Map;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.ui.app.SupportOptions;
import org.jdna.bmt.web.client.ui.util.ServiceReply;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DebugServiceAsync {
    void getMetadata(String source, GWTMediaFile file,
			AsyncCallback<ServiceReply<Map<String, String>>> callback);
    public void updateTimestamp(GWTMediaFile file, AsyncCallback<Long> callback);
    public void createSupportRequest(SupportOptions options, AsyncCallback<String> callback);
    void removeMetadataProperties(AsyncCallback<Integer> callback);
    void backupWizBin(AsyncCallback<Void> callback);
    void getWizBinBackups(AsyncCallback<String[]> callback);
}
