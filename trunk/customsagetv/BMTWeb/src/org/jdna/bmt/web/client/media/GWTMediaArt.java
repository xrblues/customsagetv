package org.jdna.bmt.web.client.media;

import java.io.Serializable;

import org.jdna.media.metadata.IMediaArt;

import sagex.phoenix.fanart.MediaArtifactType;

public class GWTMediaArt implements IMediaArt, Serializable {

    private MediaArtifactType type;
    private int season;
    private String id;
    private String label;
    private String downloadUrl;

    public GWTMediaArt() {
    }
    
    public GWTMediaArt(IMediaArt copy) {
        this.type=copy.getType();
        this.season=copy.getSeason();
        this.id=copy.getProviderId();
        this.label=copy.getLabel();
        this.downloadUrl=copy.getDownloadUrl();
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getLabel() {
        return label;
    }

    public String getProviderId() {
        return id;
    }

    public int getSeason() {
        return season;
    }

    public MediaArtifactType getType() {
        return type;
    }

}
