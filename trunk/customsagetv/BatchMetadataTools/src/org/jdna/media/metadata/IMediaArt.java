package org.jdna.media.metadata;

public interface IMediaArt {
    public static final int BACKGROUND = 0;
    public static final int POSTER     = 1;
    public static final int OTHER      = 99;
    public static final int ALL        = 999;

    public String getProviderId();

    public int getType();

    public String getDownloadUrl();

    public String getLabel();
}
