package org.jdna.bmt.web.client.media;

import java.io.Serializable;

import sagex.phoenix.metadata.IMediaArt;
import sagex.phoenix.metadata.MediaArtifactType;

public class GWTMediaArt implements IMediaArt, Serializable {
    private static final long serialVersionUID = 1L;
    private MediaArtifactType type;
    private int season;
    private String downloadUrl;
    private String localFile;
    private boolean exists = true;
    private boolean local = true;
    private boolean delete = false;
    private String displayUrl;

    public GWTMediaArt() {
    }
    
    public GWTMediaArt(IMediaArt copy) {
        this.type=copy.getType();
        this.season=copy.getSeason();
        this.downloadUrl=copy.getDownloadUrl();
        if (downloadUrl!=null && downloadUrl.startsWith("file:")) {
            local=true;
        }
    }
    
    public String getDownloadUrl() {
        return downloadUrl;
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

    public void setType(MediaArtifactType type) {
        this.type = type;
    }

    public void setSeason(int season) {
        this.season = season;
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

    public void setLocalFile(String localFile) {
        this.localFile = localFile;
    }

    public String getLocalFile() {
        return localFile;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }
}
