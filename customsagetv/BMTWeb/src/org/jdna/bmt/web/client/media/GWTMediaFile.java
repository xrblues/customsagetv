package org.jdna.bmt.web.client.media;

import java.io.Serializable;

import org.jdna.bmt.web.client.util.Property;

public class GWTMediaFile extends GWTMediaResource implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean exists;
    private long lastModified;
    
    private int sageMediaFileId;
    
    public String fanartDir;
    public String formattedTitle;
    
	private Property<Boolean> sageRecording = new Property<Boolean>(false);
    private Property<Boolean> libraryFile = new Property<Boolean>(false);
    private Property<Boolean> watched = new Property<Boolean>(false);
    
    private GWTMediaMetadata metadata;
    private GWTAiringDetails airingDetails;
    
	private String showId;
    private String airingId;
    private String seriesInfoId;
    private String vfsId;
    
    private boolean isPlayable = false;
    
    private long duration;
    private long size;
    
    public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public GWTMediaFile() {
    }
    
    public GWTMediaFile(GWTMediaFolder parent, String title) {
        super(parent, title);
    }

    public String getFormattedTitle() {
		return formattedTitle;
	}

	public void setFormattedTitle(String formattedTitle) {
		this.formattedTitle = formattedTitle;
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

    public Property<Boolean> getIsLibraryFile() {
        return libraryFile;
    }

    public Property<Boolean> getIsWatched() {
        return watched;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }
    
    public String getSeriesInfoId() {
    	return seriesInfoId;
    }
    
    public void setSeriesInfoId(String id) {
    	this.seriesInfoId=id;
    }

    public String getVFSID() {
    	return vfsId;
    }
    
    public void setVFSID(String id) {
    	this.vfsId=id;
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

    public void setLastModified(long lastModified2) {
        this.lastModified=lastModified2;
    }

	public void setPlayable(boolean isPlayable) {
		this.isPlayable = isPlayable;
	}

	public boolean isPlayable() {
		return isPlayable;
	}

	public GWTAiringDetails getAiringDetails() {
		return airingDetails;
	}

	public void setAiringDetails(GWTAiringDetails airingDetails) {
		this.airingDetails = airingDetails;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getSize() {
		return size;
	}

}
