package org.jdna.bmt.web.client.ui.browser;

import java.io.Serializable;

import org.jdna.media.metadata.MetadataID;

public class MediaSearchResult implements Serializable {
    private String url;
    private String providerId;
    private float score;
    private String title;
    private String year;
    private MetadataID id;
    
    public MediaSearchResult() {
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
}
