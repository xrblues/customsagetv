package org.jdna.metadataupdater;

import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;
import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(name = "metadataUpdater", requiresKey = false, description = "Configuration for Main Metadata Updater")
public class MetadataUpdaterConfiguration {
    @Field(description = "Set to true, if you want to overwrite existing thumbnails")
    private boolean overwriteThumbnails = false;

    @Field(description = "Set to true, if you want to overwrite existing backdrop images")
    private boolean overwriteBackdrops = false;
    
    public MetadataUpdaterConfiguration() {
    }

    public void setOverwriteThumbnails(boolean b) {
        this.overwriteThumbnails = b;
    }

    public boolean isOverwriteThumbnails() {
        return overwriteThumbnails;
    }

    public boolean isOverwriteBackdrops() {
        return overwriteBackdrops;
    }

    public void setOverwriteBackdrops(boolean overwriteBackdrops) {
        this.overwriteBackdrops = overwriteBackdrops;
    }
}
