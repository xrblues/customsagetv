package org.jdna.bmt.web.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.jdna.bmt.web.client.ui.databrowser.DataBrowserService;
import org.jdna.bmt.web.client.ui.databrowser.StoreContents;

import sagex.api.UserRecordAPI;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DataBrowserServicesImpl extends RemoteServiceServlet implements DataBrowserService {
    private static final Logger log  = Logger.getLogger(DataBrowserServicesImpl.class);
    
    public DataBrowserServicesImpl() {
        ServicesInit.init();
    }

	@Override
	public ArrayList<String> getStores() {
		String[] stores = UserRecordAPI.GetAllUserStores();
		if (stores==null) return new ArrayList<String>();
		Arrays.sort(stores);
		return new ArrayList<String>(Arrays.asList(stores));
	}

	@Override
	public StoreContents getUserRecords(String store) {
		StoreContents contents = new StoreContents();
		contents.setStore(store);
		
		TreeSet<String> cols = contents.getColumns();
		Object[] records = UserRecordAPI.GetAllUserRecords(store);
		for (Object r: records) {
			String names[] = UserRecordAPI.GetUserRecordNames(r);
			for (String n: names) {
				cols.add(n);
			}
			
			HashMap<String,String> rec = new HashMap<String, String>(); 
			for (String n: names) {
				rec.put(n, UserRecordAPI.GetUserRecordData(r, n));
			}
			
			contents.getRecords().add(rec);
		}
		
		return contents;
	}
}
