package org.jdna.media;

import java.net.URI;
import java.net.URISyntaxException;

public class Path implements IPath {
    private URI uri = null;
    public Path(String uri) {
        try {
            this.uri = new URI(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    
    public String getPath() {
        return uri.getPath();
    }

    public String toURI() {
        return uri.toASCIIString();
    }

    public int compareTo(IPath path) {
        return this.toURI().compareTo(path.toURI());
    }
    
    public String toString() {
        return uri.toString();
    }
}
