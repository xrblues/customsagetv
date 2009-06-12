package org.jdna.bmt.web.client.ui.browser;

import java.io.Serializable;

public class MediaResult implements Serializable {
    private int mediaId;
    private String mediaTitle;
    private String posterUrl;
    
    public MediaResult() {
    }

    public int getMediaId() {
        return mediaId;
    }

    public void setMediaId(int mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaTitle() {
        return mediaTitle;
    }

    public void setMediaTitle(String mediaTitle) {
        this.mediaTitle = mediaTitle;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}
