package org.jdna.bmt.web.client.ui.browser;


public class MediaFile extends MediaResource {
    public String episodeTitle;
    
    public MediaFile() {
        super();
    }

    public MediaFile(MediaFolder parent, String title) {
        super(parent, title);
    }

    public String getEpisodeTitle() {
        return episodeTitle;
    }

    public void setEpisodeTitle(String episodeTitle) {
        this.episodeTitle = episodeTitle;
    }
}
