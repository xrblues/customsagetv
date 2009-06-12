package org.jdna.media.metadata.impl.imdb;

import sagex.phoenix.configuration.proxy.AField;
import sagex.phoenix.configuration.proxy.AGroup;
import sagex.phoenix.configuration.proxy.FieldProxy;
import sagex.phoenix.configuration.proxy.GroupProxy;

@AGroup(label="IMDB Configuration", path = "bmt/imdb", description = "Configuration for IMDB Metadata Parser")
public class IMDBConfiguration extends GroupProxy {
    @AField(label="Preferred Poster Width", description = "Preferred height or width of an imdb thumbnail")
    private FieldProxy<Integer> forcedIMDBImageSize = new FieldProxy<Integer>(512);

    public IMDBConfiguration() {
        super();
        init();
    }

    public int getForcedIMDBImageSize() {
        return forcedIMDBImageSize.getInt();
    }

    public void setForcedIMDBImageSize(int forcedIMDBImageSize) {
        this.forcedIMDBImageSize.set(forcedIMDBImageSize);
    }
}
