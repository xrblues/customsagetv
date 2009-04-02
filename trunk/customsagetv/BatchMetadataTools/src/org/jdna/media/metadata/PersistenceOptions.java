package org.jdna.media.metadata;

/**
 * Options that effect how things are persisted.  Not all persistence engines will support all options.
 * 
 * @author seans
 *
 */
public class PersistenceOptions {
    private boolean overwriteFanart=false;
    private boolean overwriteMetadata=false;
    
    public PersistenceOptions() {
    }
    
    public boolean isOverwriteFanart() {
        return overwriteFanart;
    }
    public void setOverwriteFanart(boolean overwriteFanart) {
        this.overwriteFanart = overwriteFanart;
    }
    public boolean isOverwriteMetadata() {
        return overwriteMetadata;
    }
    public void setOverwriteMetadata(boolean overwriteMetadata) {
        this.overwriteMetadata = overwriteMetadata;
    }
}
