package org.jdna.bmt.web.client.ui.status;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StatusServicesAsync {
    public void getStatusInfo(String base, AsyncCallback<List<StatusValue>> result);
    public void getSystemMessages(AsyncCallback<List<SystemMessage>> callback);
}
