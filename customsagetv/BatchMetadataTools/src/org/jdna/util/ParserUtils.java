package org.jdna.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserUtils {
    /**
     * return a 2 element array.  0 = title; 1=date 
     * @param title
     * @return
     */
    public static String[] parseTitle(String title) {
        String v[] = {"",""};
        if (title==null) return v;
        
        Pattern p = Pattern.compile("(.*)\\s+\\(?([0-9]{4})\\)?[\\s$]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(title);
        if (m.find()) {
            v[0]=m.group(1);
            v[1]=m.group(2);
        } else {
            // SEAN: this this was causing too many issues with tvdb searching
            //v[0] = parseTitleWithLanguage(title);
            v[0] = title;
        }
        return v;
    }

    private static String parseTitleWithLanguage(String title) {
        // first find Title (US) (en)
        Pattern p = Pattern.compile("(.*\\s+\\([^)]+\\))s+\\([^)]+\\)",Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(title);
        if (m.find()) {
            return m.group(1);
        }

        // next try
        // Title (xx) or Title (####)
        p = Pattern.compile("([^(]+)\\s+\\([a-z]{2,4}\\)", Pattern.CASE_INSENSITIVE);
        m = p.matcher(title);
        if (m.find()) {
            return m.group(1);
        } else {
            return title;
        }
    }
}
