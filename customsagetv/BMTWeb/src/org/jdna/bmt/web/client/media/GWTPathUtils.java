package org.jdna.bmt.web.client.media;

import org.jdna.media.IPath;

public class GWTPathUtils {
    public static GWTPath createPath(IPath path) {
        return new GWTPath(path.toURI());
    }
}
