package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UpdatablePanel {
    public String getHeader();
    public String getHelp();
    public void save(AsyncCallback<UpdatablePanel> callback);
    public boolean isReadonly();
}
