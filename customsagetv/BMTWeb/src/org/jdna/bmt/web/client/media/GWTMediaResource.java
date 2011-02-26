package org.jdna.bmt.web.client.media;

import java.io.Serializable;
import java.util.ArrayList;

import sagex.phoenix.metadata.MediaType;

public class GWTMediaResource implements Serializable {
    private static final long serialVersionUID = 1L;

    private transient GWTMediaFolder parent = null;
    
    private String title;
    private String minorTitle;
    private String thumbnailUrl;
    private String resourceRef;
    private MediaType type;
    private String path;
    private String message = null;
    
    private ArrayList<String> hints = new ArrayList<String>();
    
    public ArrayList<String> getHints() {
		return hints;
	}
    
    public boolean hasHint(String hint) {
    	return hints.contains(hint);
    }

	public GWTMediaResource() {
    }
    
    public GWTMediaResource(GWTMediaFolder parent, String title) {
        this.parent = parent;
        this.title=title;
    }
    
    public GWTMediaFolder getParent() {
        return parent;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setParent(GWTMediaFolder parent) {
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

    public String getMinorTitle() {
        return minorTitle;
    }

    public void setMinorTitle(String minorTitle) {
        this.minorTitle = minorTitle;
    }

    /**
     * @return the type
     */
    public MediaType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(MediaType type) {
        this.type = type;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    public void setMessage(String message) {
        this.message=message;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * return true if this resources is not a real file, but rather an airing
     * @return
     */
    public boolean isAiring() {
    	return this instanceof GWTMediaFile && ((GWTMediaFile)this).getSageMediaFileId()==0;
    }
}
