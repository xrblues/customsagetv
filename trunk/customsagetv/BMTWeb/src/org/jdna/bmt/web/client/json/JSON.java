package org.jdna.bmt.web.client.json;

import org.jdna.bmt.web.client.util.NumberUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
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
	
	public static JSONArray getArray(JSONValue v, String key) {
		if (v==null) return null;
		if (key==null) return null;
		JSONObject jo = v.isObject();
		if (jo!=null) {
			JSONValue jarr = jo.get(key);
			if (jarr!=null) {
				return jarr.isArray();
			} else {
				GWT.log("Type for key " + key + " was not an array in " + v);
			}
		}
		return null;
	}
	
	public static int getInt(JSONValue v, String key) {
		return NumberUtil.toInt(getString(v, key),0);
	}
}
