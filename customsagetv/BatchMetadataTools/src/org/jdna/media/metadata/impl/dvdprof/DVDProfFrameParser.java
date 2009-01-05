package org.jdna.media.metadata.impl.dvdprof;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.jdna.url.URLSaxParser;
import org.jdna.url.UrlUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Looks for <frame src="/onlinecollections/dvd/.../List.aspx" marginheight="1"
 * marginwidth="1" name="list"> and parses the url.
 * 
 * @author seans
 * 
 */
public class DVDProfFrameParser extends URLSaxParser {
    public static final Logger log      = Logger.getLogger(DVDProfFrameParser.class);

    private String             url      = null;
    private String             origUrl  = null;
    private String             pathName = null;

    public DVDProfFrameParser(String url) {
        super(url);
        this.origUrl = url;
        this.pathName = UrlUtil.getPathName(url);
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
        if (url != null) return;

        if (isTag("frame", localName)) {
            log.debug("Got A Frame... Checking for List Frame.");
            String nameAttr = atts.getValue("name");
            if ("list".equalsIgnoreCase(nameAttr)) {
                log.debug("Frame is a list frame.");
                try {
                    url = parseBaseUrl(origUrl) + atts.getValue("src");
                    if (url.contains("?")) {
                        url += "&";
                    } else {
                        url += "?";
                    }
                    // we add a unique userid so that the url caching doesn't
                    // bomb.
                    url += "juuid=" + pathName;
                    log.debug("Movie List Url: " + url);
                } catch (MalformedURLException e) {
                    log.error("Failed to parse profile url from : " + uri, e);
                }
            }
        }
    }

    public String getMovieListUrl() {
        return url;
    }

    private String parseBaseUrl(String url) throws MalformedURLException {
        URL u = new URL(url);
        return u.getProtocol() + "://" + u.getHost();
    }

}
