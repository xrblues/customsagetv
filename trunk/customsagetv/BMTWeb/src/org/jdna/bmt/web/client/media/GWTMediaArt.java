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
    private boolean exists = true;
    private boolean local = true;
    private boolean delete = false;

    public GWTMediaArt() {
    }
    
    public GWTMediaArt(IMediaArt copy) {
        this.type=copy.getType();
        this.season=copy.getSeason();
        this.id=copy.getProviderId();
        this.label=copy.getLabel();
        this.downloadUrl=copy.getDownloadUrl();
        if (downloadUrl!=null && downloadUrl.startsWith("file:")) {
            local=true;
        }
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

    public boolean exists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(MediaArtifactType type) {
        this.type = type;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public boolean isLocal() {
        return local ;
    }
    
    public void setLocal(boolean local) {
        this.local=local;
    }
    
    public boolean isDeleted() {
        return delete;
    }
    
    public void setDeleted(boolean deleted) {
        this.delete=deleted;
    }
}
