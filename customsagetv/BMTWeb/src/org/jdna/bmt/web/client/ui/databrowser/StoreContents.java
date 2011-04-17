package org.jdna.bmt.web.client.ui.databrowser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class StoreContents implements Serializable {
	private static final long serialVersionUID = 1L;
	private String store;
	private TreeSet<String> columns = new TreeSet<String>();
	private ArrayList<HashMap<String, String>> records = new ArrayList<HashMap<String,String>>();
	
	
	public String getStore() {
		return store;
	}
	public void setStore(String store) {
		this.store = store;
	}
	public TreeSet<String> getColumns() {
		return columns;
	}
	public void setColumns(TreeSet<String> columns) {
		this.columns = columns;
	}
	public ArrayList<HashMap<String, String>> getRecords() {
		return records;
	}
	public void setRecords(ArrayList<HashMap<String, String>> records) {
		this.records = records;
	}
}
