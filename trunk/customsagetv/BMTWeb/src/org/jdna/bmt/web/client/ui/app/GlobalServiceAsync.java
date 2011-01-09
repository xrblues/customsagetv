package org.jdna.bmt.web.client.ui.app;


import java.util.ArrayList;

import org.jdna.bmt.web.client.event.Notification;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.ui.BatchOperation;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GlobalServiceAsync {
	void getNotices(AsyncCallback<ArrayList<Notification>> callback);
	void batchOperation(GWTMediaFolder folder, BatchOperation op, AsyncCallback<Void> callback);
	void batchOperation(BatchOperation op, AsyncCallback<Void> callback);
	void showAboutDialog(AsyncCallback<Boolean> callback);
}
