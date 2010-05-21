package org.jdna.media.metadata;

import sagex.phoenix.fanart.IMetadataProviderInfo;

public interface IProviderInfo extends IMetadataProviderInfo {
    public String getId();

    public String getName();

    public String getDescription();

    public String getIconUrl();
}
