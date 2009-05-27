package org.jdna.media;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * As Taken from http://xbmc.org/wiki/?title=Stacking
 * 
 * <pre>
 * It can be summarized as the following
 * [token][cd|part|dvd][num]
 * </pre>
 * 
 * @author seans
 * 
 */
public class CDStackingModel implements IMediaStackModel {
    private static final Logger         log      = Logger.getLogger(CDStackingModel.class);
    public static final CDStackingModel INSTANCE = new CDStackingModel();
    private Pattern                     pattern  = null;
    private MediaConfiguration cfg = new MediaConfiguration();

    public CDStackingModel() {
        String pat = cfg.getStackingModelRegex();
        log.debug("CD Stacking Regex: " + pat);
        pattern = Pattern.compile(pat, Pattern.CASE_INSENSITIVE);
    }

    public String getStackedTitle(IMediaResource resource) {
        return getStackedTitle(resource.getTitle());
    }

    public String getStackedTitle(String title) {
        log.debug("Title: " + title);
        if (title == null) return null;

        Matcher m = pattern.matcher(title);
        if (m.find()) {
            String s = title.substring(0, m.start());
            if (log.isDebugEnabled()) {
                log.debug(String.format("Title: %s; Stack: %s; MatchPos: %s; Regex: %s", title, s, m.start(), pattern.pattern()));
            }
            title = s;
        }

        return title;
    }
}
