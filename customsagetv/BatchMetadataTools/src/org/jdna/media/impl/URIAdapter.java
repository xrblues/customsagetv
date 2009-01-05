package org.jdna.media.impl;

import java.net.URI;

public abstract class URIAdapter {
    private URI uri;

    public URIAdapter(URI uri) {
        this.uri = uri;
    }

    public URI getUri() {
        return uri;
    }

    public void touch() {
    }

    public boolean exists() {
        return false;
    }

    public boolean canWrite() {
        return false;
    }

    public long lastModified() {
        return 0;
    }

    public abstract String getName();

    public abstract URI getParentUri();

    public abstract URIAdapter createUriAdapter(String string);

    public URI[] listMembers() {
        return null;
    }

    public boolean isDirectory() {
        return false;
    }

    public String toString() {
        return getUri().toString();
    }

    public String toURIString() {
        return getUri().toString();
    }
}
