package org.jdna.media.metadata;

import sagex.phoenix.fanart.MediaArtifactType;

public interface IMediaArt {
    public String getProviderId();
    public MediaArtifactType getType();
    public String getDownloadUrl();
    public String getLabel();
    public int getSeason();
}
