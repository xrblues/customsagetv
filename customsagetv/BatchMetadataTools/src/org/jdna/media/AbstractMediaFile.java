package org.jdna.media;

import java.net.URI;

public abstract class AbstractMediaFile extends AbstractMediaResource implements IMediaFile {
    public AbstractMediaFile(URI uri) {
        super(uri);
    }

    protected ContentType contentType = ContentType.UNKNOWN; 
    
    public ContentType getContentType() {
        return contentType;
    }

    public Type getType() {
        return Type.File;
    }
}
