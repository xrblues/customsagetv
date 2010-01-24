package org.jdna.bmt.web.client.media;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaType;

public class GWTMediaSearchResult implements Serializable, IMetadataSearchResult {
    private static final long serialVersionUID = 1L;
    private String url;
    private String providerId;
    private float score;
    private String title;
    private String year;
    private int mediaFileId;
    private MediaType type;
    private String id;
    private Map<String,String> extra=new HashMap<String, String>();
    
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

    public int getMediaFileId() {
        return mediaFileId;
    }
    
    public void setMediaFileId(int id) {
        this.mediaFileId=id;
    }

    public MediaType getMediaType() {
        return type;
    }
    
    public void setMediaType(MediaType type) {
        this.type=type;
    }

    public Map<String, String> getExtra() {
        return extra;
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id=id;
    }
}
