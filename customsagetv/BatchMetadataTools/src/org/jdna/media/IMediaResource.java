package org.jdna.media;

import java.io.IOException;

import org.jdna.media.metadata.IMediaMetadata;

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

    public String getRelativePath(IMediaResource res);

    public void touch();

    public int getType();

    public int getContentType();

    public IMediaMetadata getMetadata();

    public String getLocalPosterUri();

    public String getLocalMetadataUri();

    public String getLocalSubtitlesUri();
    
    public String getLocalBackdropUri();

    /**
     * Convenience Method for saving/storing the metadata for a given resource.
     * 
     * @param metadata
     * @param options Option from IMetadataPersistence
     * @throws IOException
     */
    public void updateMetadata(IMediaMetadata metadata, long options) throws IOException;
}
