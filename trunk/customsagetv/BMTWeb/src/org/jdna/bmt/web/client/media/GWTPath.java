package org.jdna.bmt.web.client.media;

import java.io.Serializable;

import org.jdna.media.IPath;

public class GWTPath implements IPath, Serializable {
    private String uri = null;
    
    public GWTPath() {
    }
    
    public GWTPath(String uri) {
        this.uri=uri;
    }
    
    public String getPath() {
        // TODO: Fix This
        return uri;
    }

    public String toURI() {
        return uri;
    }

    public int compareTo(IPath o) {
        return 0;
    }

    public String toString() {
        return toURI();
    }
}
