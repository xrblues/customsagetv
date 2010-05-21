package org.jdna.bmt.web.client.ui.status;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StatusServicesAsync {
    public void getStatusInfo(String base, AsyncCallback<List<StatusValue>> result);
    public void getSystemMessages(AsyncCallback<List<SystemMessage>> callback);
    void getBMTVersion(AsyncCallback<String> callback);
    void clearSystemMessages(AsyncCallback<Void> callback);
    void deleteSystemMessage(int id, AsyncCallback<Void> callback);
}
