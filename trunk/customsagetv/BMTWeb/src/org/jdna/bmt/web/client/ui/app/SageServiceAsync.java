package org.jdna.bmt.web.client.ui.app;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface SageServiceAsync {
    void refreshLibrary(boolean fullScan, AsyncCallback<String> callback);
}
