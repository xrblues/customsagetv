package org.jdna.bmt.web.client.ui.scan;

import java.io.Serializable;

import org.jdna.bmt.web.client.util.Property;

public class SaveOptions implements Serializable {
    private Property<Boolean> updateMetadata = new Property<Boolean>(Boolean.TRUE);
    private Property<Boolean> updateFanart = new Property<Boolean>(Boolean.TRUE);
    private Property<Boolean> overwriteMetadata = new Property<Boolean>(Boolean.TRUE);
    private Property<Boolean> overwriteFanart = new Property<Boolean>(Boolean.TRUE);
    
    public Property<Boolean> getUpdateMetadata() {
        return updateMetadata;
    }
    
    public Property<Boolean> getUpdateFanart() {
        return updateFanart;
    }
    
    public Property<Boolean> getOverwriteMetadata() {
        return overwriteMetadata;
    }
    
    public Property<Boolean> getOverwriteFanart() {
        return overwriteFanart;
    }
}
