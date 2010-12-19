package org.jdna.bmt.web.client.util;

public class StringUtils {
    public static boolean isEmpty(String s) {
        return s==null || s.trim().length()==0;
    }
    
    public static int parseInt(String s, int def) {
    	try {
    		return Integer.parseInt(s);
    	} catch (NumberFormatException nfe) {
    		return def;
    	}
    }
}
