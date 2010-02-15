package org.jdna.bmt.web.client.media;

import java.io.Serializable;

import org.jdna.bmt.web.client.util.Property;

public class GWTMediaFile extends GWTMediaResource implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean exists;
    private long lastModified;
    
    private int sageMediaFileId;
    
    public String fanartDir;
    
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

    /**
     * @return the fanartDir
     */
    public String getFanartDir() {
        return fanartDir;
    }

    /**
     * @param fanartDir the fanartDir to set
     */
    public void setFanartDir(String fanartDir) {
        this.fanartDir = fanartDir;
    }
}
