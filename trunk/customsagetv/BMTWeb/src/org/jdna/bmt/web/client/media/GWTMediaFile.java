package org.jdna.bmt.web.client.media;

import java.io.Serializable;

import org.jdna.bmt.web.client.util.Property;

public class GWTMediaFile extends GWTMediaResource implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean exists;
    private long lastModified;
    
    private int sageMediaFileId;
    
    public GWTMediaArt defaultPoster;
    public GWTMediaArt defaultBackground;
    public GWTMediaArt defaultBanner;
    
    public String defaultPosterDir;
    public String defaultBackgroundDir;
    public String defaultBannerDir;
    
    
    private Property<Boolean> sageRecording = new Property<Boolean>(false);
    
    private GWTMediaMetadata metadata;
    
    private String showId;
    private String airingId;

    public GWTMediaFile() {
    }
    
    public GWTMediaFile(GWTMediaFolder parent, String title) {
        super(parent, title);
    }

    public boolean exists() {
        return exists;
    }

    public boolean isReadOnly() {
        return true;
    }

    public long lastModified() {
        return lastModified;
    }

    public void attachMetadata(GWTMediaMetadata metadata) {
        this.metadata=metadata;
    }
    
    public GWTMediaMetadata getMetadata() {
        return metadata;
    }

    public int getSageMediaFileId() {
        return sageMediaFileId;
    }

    public void setSageMediaFileId(int sageMediaFileId) {
        this.sageMediaFileId = sageMediaFileId;
    }

    public GWTMediaArt getDefaultPoster() {
        return defaultPoster;
    }

    public void setDefaultPoster(GWTMediaArt defaultPoster) {
        this.defaultPoster = defaultPoster;
    }

    public GWTMediaArt getDefaultBackground() {
        return defaultBackground;
    }

    public void setDefaultBackground(GWTMediaArt defaultBackground) {
        this.defaultBackground = defaultBackground;
    }

    public GWTMediaArt getDefaultBanner() {
        return defaultBanner;
    }

    public void setDefaultBanner(GWTMediaArt defaultBanner) {
        this.defaultBanner = defaultBanner;
    }

    public Property<Boolean> getSageRecording() {
        return sageRecording;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getAiringId() {
        return airingId;
    }

    public void setAiringId(String airingId) {
        this.airingId = airingId;
    }

    public String getDefaultPosterDir() {
        return defaultPosterDir;
    }

    public void setDefaultPosterDir(String defaultPosterDir) {
        this.defaultPosterDir = defaultPosterDir;
    }

    public String getDefaultBackgroundDir() {
        return defaultBackgroundDir;
    }

    public void setDefaultBackgroundDir(String defaultBackgroundDir) {
        this.defaultBackgroundDir = defaultBackgroundDir;
    }

    public String getDefaultBannerDir() {
        return defaultBannerDir;
    }

    public void setDefaultBannerDir(String defaultBannerDir) {
        this.defaultBannerDir = defaultBannerDir;
    }
}
