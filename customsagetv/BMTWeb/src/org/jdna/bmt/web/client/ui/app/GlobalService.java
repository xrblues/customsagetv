package org.jdna.bmt.web.client.ui.app;

import java.util.ArrayList;

import org.jdna.bmt.web.client.event.Notification;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.ui.BatchOperation;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("global")
public interface GlobalService extends RemoteService {
	public ArrayList<Notification> getNotices();

	public void batchOperation(GWTMediaFolder folder, BatchOperation op);
	public void batchOperation(BatchOperation op);
	
	public boolean showAboutDialog();
}
