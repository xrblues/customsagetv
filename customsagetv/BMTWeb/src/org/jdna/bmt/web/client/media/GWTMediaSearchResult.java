package org.jdna.bmt.web.client.media;

import java.io.Serializable;

import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MetadataID;

public class GWTMediaSearchResult implements Serializable, IMediaSearchResult {
    private String url;
    private String providerId;
    private float score;
    private String title;
    private String year;
    private MetadataID id;
    private int mediaFileId;
    
    public GWTMediaSearchResult() {
    }

    public String getUrl() {
        return url;
    }

    public void setProviderId(String providerId) {
        this.providerId=providerId;
    }

    public String getProviderId() {
        return providerId;
    }

    public float getScore() {
        return score;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public MetadataID getId() {
        return id;
    }

    public void setId(MetadataID id) {
        this.id = id;
    }

    public MetadataID getMetadataId() {
        return getId();
    }

    public int getMediaFileId() {
        return mediaFileId;
    }
    
    public void setMediaFileId(int id) {
        this.mediaFileId=id;
    }
}
