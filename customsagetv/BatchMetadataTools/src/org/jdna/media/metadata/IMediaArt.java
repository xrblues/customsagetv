package org.jdna.media.metadata;

import sagex.phoenix.fanart.FanartUtil;

public interface IMediaArt {
    public String getProviderId();

    public FanartUtil.MediaArtifactType getType();

    public String getDownloadUrl();

    public String getLabel();
}
