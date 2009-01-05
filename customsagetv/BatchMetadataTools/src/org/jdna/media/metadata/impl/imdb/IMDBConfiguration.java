package org.jdna.media.metadata.impl.imdb;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(name = "imdb", description = "Configuration for IMDB Metadata Parser", requiresKey = false)
public class IMDBConfiguration {
    @Field(description = "Preferred height or width of an imdb thumbnail")
    private int forcedIMDBImageSize = 0;

    public IMDBConfiguration() {
    }

    public int getForcedIMDBImageSize() {
        return forcedIMDBImageSize;
    }

    public void setForcedIMDBImageSize(int forcedIMDBImageSize) {
        this.forcedIMDBImageSize = forcedIMDBImageSize;
    }
}
