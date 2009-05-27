package org.jdna.media;

import org.jdna.configuration.Field;
import org.jdna.configuration.FieldProxy;
import org.jdna.configuration.Group;
import org.jdna.configuration.GroupProxy;

@Group(label="Media Configuration", path = "bmt/media", description = "Configuration for Media items")
public class MediaConfiguration extends GroupProxy {
    @Field(label="CD Stacking Regex", description = "Stacking Model regex (taken from xbmc group)")
    private FieldProxy<String>  stackingModelRegex      = new FieldProxy<String>("[ _\\\\.-]+(cd|dvd|part|disc)[ _\\\\.-]*([0-9a-d]+)");

    @Field(label="Write Poster in TS Folder", description = "If true, then the thumbnail will be written to the VIDEO_TS folder.  Otherwise, it gets written to the normal DVD location")
    private FieldProxy<Boolean> useTSFolderForThumbnail = new FieldProxy<Boolean>(false);

    @Field(label="Supported Filenames", description = "Regular expression for the file extensions that are recognized")
    private FieldProxy<String>  videoExtensionsRegex    =  new FieldProxy<String>("avi|mpg|divx|mkv|wmv|mov|xvid|ts|m2ts|m4v|mp4|iso");

    @Field(label="Ignore These Folders", description = "Regular expression for the directory names to ignore")
    private FieldProxy<String>  excludeVideoDirsRegex   = new FieldProxy<String>(null);

    public MediaConfiguration() {
        super();
        init(this);
    }

    public String getStackingModelRegex() {
        return stackingModelRegex.getString();
    }

    public void setStackingModelRegex(String stackingModelRegex) {
        this.stackingModelRegex.set(stackingModelRegex);
    }

    public boolean isUseTSFolderForThumbnail() {
        return useTSFolderForThumbnail.getBoolean();
    }

    public void setUseTSFolderForThumbnail(boolean useTSFolderForThumbnail) {
        this.useTSFolderForThumbnail.set(useTSFolderForThumbnail);
    }

    public String getVideoExtensionsRegex() {
        return videoExtensionsRegex.getString();
    }

    public void setVideoExtensionsRegex(String videoExtensionsRegex) {
        this.videoExtensionsRegex.set(videoExtensionsRegex);
    }

    public String getExcludeVideoDirsRegex() {
        return excludeVideoDirsRegex.getString();
    }

    public void setExcludeVideoDirsRegex(String excludeVideoDirsRegex) {
        this.excludeVideoDirsRegex.set(excludeVideoDirsRegex);
    }
}
