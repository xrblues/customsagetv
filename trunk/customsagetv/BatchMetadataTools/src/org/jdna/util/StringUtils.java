package org.jdna.util;

public class StringUtils {
    public static String removeHtml(String html) {
        if (html==null) return null;
        return html.replaceAll("<[^>]+>", "");
    }
}
