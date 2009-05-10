package org.jdna.media;

import java.net.URI;
import java.util.List;

public abstract class DecoratedMediaFolder implements IMediaFolder {
    private IMediaFolder folder = null;
    
    public DecoratedMediaFolder(IMediaFolder orig) {
        this.folder = orig;
    }
    
    protected IMediaFolder getUndecoratedFolder() {
        return folder;
    }
    
    public void accept(IMediaResourceVisitor visitor, boolean recurse) {
        folder.accept(visitor, recurse);
    }
    public void accept(IMediaResourceVisitor visitor) {
        folder.accept(visitor);
    }
    public int compareTo(IMediaResource o) {
        return folder.compareTo(o);
    }
    public void delete() {
        folder.delete();
    }
    public boolean exists() {
        return folder.exists();
    }
    public String getBasename() {
        return folder.getBasename();
    }
    public String getExtension() {
        return folder.getExtension();
    }
    public URI getLocationUri() {
        return folder.getLocationUri();
    }
    public String getName() {
        return folder.getName();
    }
    public IMediaResource getParent() {
        return folder.getParent();
    }
    public IMediaResource getResource(String path) {
        return folder.getResource(path);
    }
    public String getTitle() {
        return folder.getTitle();
    }
    public Type getType() {
        return folder.getType();
    }
    public boolean isReadOnly() {
        return folder.isReadOnly();
    }
    public long lastModified() {
        return folder.lastModified();
    }
    public List<IMediaResource> members() {
        return folder.members();
    }
    public void touch() {
        folder.touch();
    }
}
