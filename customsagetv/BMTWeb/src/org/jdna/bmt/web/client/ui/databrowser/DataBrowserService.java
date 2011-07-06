package org.jdna.bmt.web.client.ui.databrowser;


import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("databrowser")
public interface DataBrowserService extends RemoteService {
	public ArrayList<String> getStores();
	public StoreContents getUserRecords(String store);
}
