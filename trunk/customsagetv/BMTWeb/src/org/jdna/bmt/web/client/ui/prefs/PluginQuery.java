package org.jdna.bmt.web.client.ui.prefs;

import java.io.Serializable;

public class PluginQuery implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String SOURCE_ALL = "all";
	public static final String SOURCE_INSTALLED = "installed";
	public static final String SOURCE_CLIENT_INSTALLED = "clientinstalled";
	
	public static final String TYPE_STANDARD = "Standard";
	public static final String TYPE_THEME = "Theme";
	public static final String TYPE_STV = "STV";
	public static final String TYPE_STVi = "STVi";
	public static final String TYPE_LIBRARY = "Library";

	public static final String QUERY_AUTHOR = "author";
	public static final String QUERY_SEARCH_ALL = "searchall";
	
	public String Source;
	public String Type;
	public String UIContext;

	public String QueryType;
	public String Query;
	
	public PluginQuery() {
	}
}
