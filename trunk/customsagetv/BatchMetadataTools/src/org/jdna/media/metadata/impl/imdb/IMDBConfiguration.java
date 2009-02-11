package org.jdna.media.metadata.impl.imdb;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(label="IMDB Configuration", name = "imdb", description = "Configuration for IMDB Metadata Parser", requiresKey = false)
public class IMDBConfiguration {
    @Field(label="Preferred Poster Width", description = "Preferred height or width of an imdb thumbnail")
    private int forcedIMDBImageSize = 512;

    public IMDBConfiguration() {
    }

    public int getForcedIMDBImageSize() {
        return forcedIMDBImageSize;
    }

    public void setForcedIMDBImageSize(int forcedIMDBImageSize) {
        this.forcedIMDBImageSize = forcedIMDBImageSize;
    }
}
