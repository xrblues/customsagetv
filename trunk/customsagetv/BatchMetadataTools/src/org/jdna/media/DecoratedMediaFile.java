package org.jdna.media;



public class DecoratedMediaFile implements IMediaFile {
    private IMediaFile file = null;

    public DecoratedMediaFile(IMediaFile file) {
        this.file=file;
    }
    
    protected IMediaFile getUndecoratedMediaFile() {
        return file;
    }
    
    public void accept(IMediaResourceVisitor visitor) {
        file.accept(visitor);
    }

    public int compareTo(IMediaResource o) {
        return file.compareTo(o);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof DecoratedMediaFile) && file.equals(((DecoratedMediaFile)obj).file);
    }

    public void delete() {
        file.delete();
    }

    public boolean exists() {
        return file.exists();
    }

    public String getBasename() {
        return file.getBasename();
    }

    public ContentType getContentType() {
        return file.getContentType();
    }

    public String getExtension() {
        return file.getExtension();
    }

    public IPath getLocation() {
        return file.getLocation();
    }

    public String getName() {
        return file.getName();
    }

    public IMediaResource getParent() {
        return file.getParent();
    }

    public String getTitle() {
        return file.getTitle();
    }

    public Type getType() {
        return file.getType();
    }

    public boolean isReadOnly() {
        return file.isReadOnly();
    }

    public long lastModified() {
        return file.lastModified();
    }

    public void touch() {
        file.touch();
    }
}
