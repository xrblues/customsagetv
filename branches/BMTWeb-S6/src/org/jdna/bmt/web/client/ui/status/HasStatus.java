package org.jdna.bmt.web.client.ui.status;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public interface HasStatus {
    public String getStatus();
    public String getHelp();
    public Widget getStatusWidget();
    public void update(AsyncCallback<Void> callback);
    public Widget getHeaderActionsWidget();
}
