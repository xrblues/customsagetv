package org.jdna.media;

import sagex.phoenix.configuration.proxy.AField;
import sagex.phoenix.configuration.proxy.AGroup;
import sagex.phoenix.configuration.proxy.FieldProxy;
import sagex.phoenix.configuration.proxy.GroupProxy;

@AGroup(label="Media Configuration", path = "bmt/media", description = "Configuration for Media items")
public class MediaConfiguration extends GroupProxy {
    @AField(label="CD Stacking Regex", description = "Stacking Model regex (taken from xbmc group)")
    private FieldProxy<String>  stackingModelRegex      = new FieldProxy<String>("[ _\\\\.-]+(cd|dvd|part|disc)[ _\\\\.-]*([0-9a-d]+)");

    @AField(label="Supported Filenames", description = "Regular expression for the file extensions that are recognized")
    private FieldProxy<String>  videoExtensionsRegex    =  new FieldProxy<String>("avi|mpg|divx|mkv|wmv|mov|xvid|ts|m2ts|m4v|mp4|iso");

    @AField(label="Ignore These Folders", description = "Regular expression for the directory names to ignore")
    private FieldProxy<String>  excludeVideoDirsRegex   = new FieldProxy<String>(null);

    public MediaConfiguration() {
        super();
        init();
    }

    public String getStackingModelRegex() {
        return stackingModelRegex.getString();
    }

    public void setStackingModelRegex(String stackingModelRegex) {
        this.stackingModelRegex.set(stackingModelRegex);
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
