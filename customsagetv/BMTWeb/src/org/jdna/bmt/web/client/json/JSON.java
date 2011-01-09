package org.jdna.bmt.web.client.json;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class JSON {
	public static String getString(JSONValue v, String key) {
		if (v==null) return null;
		if (key==null) return null;
		JSONObject jo = v.isObject();
		if (jo!=null) {
			return getString(jo.get(key));
		}
		return null;
	}

	public static String getString(JSONValue v) {
		if (v==null) return null;
		JSONString s = v.isString();
		if (s!=null) {
			return s.stringValue();
		}
		return null;
	}
	
}
