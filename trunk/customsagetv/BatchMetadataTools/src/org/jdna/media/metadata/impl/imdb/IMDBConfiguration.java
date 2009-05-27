package org.jdna.media.metadata.impl.imdb;

import org.jdna.configuration.Field;
import org.jdna.configuration.FieldProxy;
import org.jdna.configuration.Group;
import org.jdna.configuration.GroupProxy;

@Group(label="IMDB Configuration", path = "bmt/imdb", description = "Configuration for IMDB Metadata Parser")
public class IMDBConfiguration extends GroupProxy {
    @Field(label="Preferred Poster Width", description = "Preferred height or width of an imdb thumbnail")
    private FieldProxy<Integer> forcedIMDBImageSize = new FieldProxy<Integer>(512);

    public IMDBConfiguration() {
        super();
        init(this);
    }

    public int getForcedIMDBImageSize() {
        return forcedIMDBImageSize.getInt();
    }

    public void setForcedIMDBImageSize(int forcedIMDBImageSize) {
        this.forcedIMDBImageSize.set(forcedIMDBImageSize);
    }
}
