package org.jdna.util;

public class Strings {
    /**
     * Return true if the String is null, or zero length when trimmed
     * 
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }
}
