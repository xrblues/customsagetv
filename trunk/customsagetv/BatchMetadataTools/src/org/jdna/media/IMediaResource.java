package org.jdna.media;


public interface IMediaResource extends Comparable<IMediaResource> {
    public static final int CONTENT_TYPE_UNKNOWN = 0;
    public static final int CONTENT_TYPE_MOVIE   = 1;
    public static final int CONTENT_TYPE_DVD     = 2;
    public static final int CONTENT_TYPE_TV      = 3;

    public void accept(IMediaResourceVisitor visitor);

    public void copy();

    public void delete();

    public String getTitle();

    public String getName();

    public String getLocationUri();

    public String getPath();

    public String getBasename();

    public String getExtension();

    public IMediaResource getParent();

    public boolean isReadOnly();

    public boolean exists();

    public long lastModified();

    public void touch();

    public int getType();

    public int getContentType();
}
