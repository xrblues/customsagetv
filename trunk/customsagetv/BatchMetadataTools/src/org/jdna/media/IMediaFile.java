package org.jdna.media;


public interface IMediaFile extends IMediaResource {
    public static enum ContentType {UNKNOWN, MOVIE, HDFOLDER, TV};
    public ContentType getContentType();
}
