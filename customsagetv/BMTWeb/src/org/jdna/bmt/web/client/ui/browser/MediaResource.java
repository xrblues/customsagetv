package org.jdna.bmt.web.client.ui.browser;

import java.io.Serializable;

public class MediaResource implements Serializable {
    private transient MediaFolder parent = null;
    
    private String title;
    private String thumbnailUrl;
    private String resourceRef;
    
    public MediaResource() {
    }
    
    public MediaResource(MediaFolder parent, String title) {
        this.parent = parent;
        this.title=title;
    }
    
    public MediaFolder getParent() {
        return parent;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setParent(MediaFolder parent) {
        this.parent = parent;
    }
    
    public void setTitle(String name) {
        this.title = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getResourceRef() {
        return resourceRef;
    }

    public void setResourceRef(String resourceRef) {
        this.resourceRef = resourceRef;
    }
}
