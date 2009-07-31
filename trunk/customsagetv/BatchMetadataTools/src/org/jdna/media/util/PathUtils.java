package org.jdna.media.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.jdna.media.IPath;
import org.jdna.media.Path;

public class PathUtils {
    private static final Logger log = Logger.getLogger(PathUtils.class);
    
    public static URI toURI(IPath path) {
        try {
            return new URI(path.toURI());
        } catch (URISyntaxException e) {
            log.error("Failed to create URI from IPath: " + path, e);
            throw new RuntimeException(e);
        }
    }

    public static File toFile(IPath path) {
        return new File(toURI(path));
    }

    public static IPath createPath(URI uri) {
        return new Path(uri.toString());
    }

    public static IPath createPath(File file) {
        return createPath(file.toURI());
    }
    
    public static IPath createPath(String uri) {
        try {
            return createPath(new URI(uri));
        } catch (URISyntaxException e) {
            log.error("Failed to create IPath from uri: " + uri, e);
            throw new RuntimeException("Failed to create IPath from uri: " + uri, e);
        }
    }
}
