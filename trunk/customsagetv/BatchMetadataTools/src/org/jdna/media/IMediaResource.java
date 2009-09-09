package org.jdna.media;



public interface IMediaResource extends Comparable<IMediaResource> {
    public static enum Type {File, Folder};
    
    public void accept(IMediaResourceVisitor visitor);

    public String getTitle();
    
    public String getName();

    public IPath getLocation();

    public String getBasename();

    public String getExtension();

    public IMediaResource getParent();

    public boolean isReadOnly();

    public boolean exists();

    public long lastModified();

    public void touch();
    
    public void delete();

    public Type getType();
    
    public boolean renameTo(String newName);
}
