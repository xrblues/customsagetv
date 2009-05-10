package org.jdna.media;

import java.net.URI;
import java.net.URISyntaxException;

public class VirtualMediaFile extends AbstractMediaFile implements IMediaFile {
    private long lastUpdated = 0;
    
    public VirtualMediaFile(String uri) throws URISyntaxException {
        super(new URI(uri));
    }

    public VirtualMediaFile(URI uri) {
        super(uri);
    }

    public void delete() {
    }

    public boolean exists() {
        return false;
    }

    public IMediaResource getParent() {
        return null;
    }

    public Type getType() {
        return IMediaResource.Type.File;
    }

    public boolean isReadOnly() {
        return false;
    }

    public long lastModified() {
        return lastUpdated;
    }

    public void touch() {
        lastUpdated=System.currentTimeMillis();
    }

    public void setContentType(ContentType type) {
        this.contentType=type;
    }
}
