package org.jdna.media;


public abstract class AbstractMediaFile extends AbstractMediaResource implements IMediaFile {
    public AbstractMediaFile(IPath uri) {
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
