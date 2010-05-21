package org.jdna.bmt.web.client.media;

public class SageQueryFolder extends GWTMediaFolder {
    private String mediaMask = null;
    
    public SageQueryFolder() {
        super();
    }

    public SageQueryFolder(String mediaMask, String title) {
        super(null, title);
        this.mediaMask=mediaMask;
    }
    
    public String getMediaMask() {
        return mediaMask;
    }
    public void setMediaMask(String mediaMask) {
        this.mediaMask = mediaMask;
    }

}
