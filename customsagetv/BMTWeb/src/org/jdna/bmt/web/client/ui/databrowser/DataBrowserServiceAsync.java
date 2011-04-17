package org.jdna.bmt.web.client.ui.databrowser;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface DataBrowserServiceAsync {
	void getStores(AsyncCallback<ArrayList<String>> callback);
	void getUserRecords(String store, AsyncCallback<StoreContents> callback);
}
