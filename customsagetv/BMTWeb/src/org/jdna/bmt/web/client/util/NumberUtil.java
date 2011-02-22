package org.jdna.bmt.web.client.util;

public class NumberUtil {
	public static int toInt(String in, int def) {
		try {
			if (in==null) return def;
			return Integer.parseInt(in);
		} catch (Exception e) {
			return def;
		}
	}
}	
