package org.jdna.media;

import java.net.URI;


public interface IMediaResource extends Comparable<IMediaResource> {
    public static enum Type {File, Folder};
    
    public void accept(IMediaResourceVisitor visitor);

    public String getTitle();
    
    public String getName();

    public URI getLocationUri();

    public String getBasename();

    public String getExtension();

    public IMediaResource getParent();

    public boolean isReadOnly();

    public boolean exists();

    public long lastModified();

    public void touch();
    
    public void delete();

    public Type getType();
}
