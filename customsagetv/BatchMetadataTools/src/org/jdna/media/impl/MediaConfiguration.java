package org.jdna.media.impl;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(label="Media Configuration", name = "media", requiresKey = false, description = "Configuration for Media items")
public class MediaConfiguration {
    @Field(label="CD Stacking Regex", description = "Stacking Model regex (taken from xbmc group)")
    private String  stackingModelRegex      = "[ _\\\\.-]+(cd|dvd|part)[ _\\\\.-]*([0-9a-d]+)";

    @Field(label="Write Poster in TS Folder", description = "If true, then the thumbnail will be written to the VIDEO_TS folder.  Otherwise, it gets written to the normal DVD location")
    private boolean useTSFolderForThumbnail = false;

    @Field(label="Supported Filenames", description = "Regular expression for the file extensions that are recognized")
    private String  videoExtensionsRegex    = "avi|mpg|divx|mkv|wmv|mov|xvid";

    @Field(label="Ignore These Folders", description = "Regular expression for the directory names to ignore")
    private String  excludeVideoDirsRegex   = null;

    public MediaConfiguration() {
    }

    public String getStackingModelRegex() {
        return stackingModelRegex;
    }

    public void setStackingModelRegex(String stackingModelRegex) {
        this.stackingModelRegex = stackingModelRegex;
    }

    public boolean isUseTSFolderForThumbnail() {
        return useTSFolderForThumbnail;
    }

    public void setUseTSFolderForThumbnail(boolean useTSFolderForThumbnail) {
        this.useTSFolderForThumbnail = useTSFolderForThumbnail;
    }

    public String getVideoExtensionsRegex() {
        return videoExtensionsRegex;
    }

    public void setVideoExtensionsRegex(String videoExtensionsRegex) {
        this.videoExtensionsRegex = videoExtensionsRegex;
    }

    public String getExcludeVideoDirsRegex() {
        return excludeVideoDirsRegex;
    }

    public void setExcludeVideoDirsRegex(String excludeVideoDirsRegex) {
        this.excludeVideoDirsRegex = excludeVideoDirsRegex;
    }

}
