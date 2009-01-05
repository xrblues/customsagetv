package org.jdna.media.impl;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class URIAdapterFactory {
    private static final Logger       log      = Logger.getLogger(URIAdapterFactory.class);
    private static Map<String, Class> adapters = new HashMap<String, Class>();

    static {
        adapters.put("file", FileURIAdapter.class);
    }

    public static URIAdapter getAdapter(String uri) {
        try {
            return getAdapter(new URI(uri));
        } catch (URISyntaxException e) {
            log.error("Invalid Uri: " + uri, e);
            return null;
        }
    }

    public static URIAdapter getAdapter(URI uri) {
        Class cl = adapters.get(uri.getScheme());
        if (cl == null) {
            cl = StubURIAdapter.class;
        }

        try {
            Constructor c = cl.getConstructor(URI.class);
            URIAdapter ua = (URIAdapter) c.newInstance(uri);
            return ua;
        } catch (Exception e) {
            log.error("Invalid Uri: " + uri.toString(), e);
            return null;
        }
    }
}
