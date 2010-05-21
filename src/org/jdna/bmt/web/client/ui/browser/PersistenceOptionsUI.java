package org.jdna.bmt.web.client.ui.browser;

import java.io.Serializable;

import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.util.Property;

public class PersistenceOptionsUI implements Serializable {
    private Property<GWTMediaFolder> scanPath = new Property<GWTMediaFolder>();
    private Property<Boolean> includeSubDirs = new Property<Boolean>(Boolean.TRUE);
    
    private Property<Boolean> updateMetadata = new Property<Boolean>(Boolean.TRUE);
    private Property<Boolean> updateFanart = new Property<Boolean>(Boolean.TRUE);

    private Property<Boolean> overwriteMetadata = new Property<Boolean>(Boolean.FALSE);
    private Property<Boolean> overwriteFanart = new Property<Boolean>(Boolean.FALSE);

    private Property<Boolean> importTV = new Property<Boolean>(Boolean.FALSE);
    private Property<Boolean> updateWizBin = new Property<Boolean>(Boolean.TRUE);
    
    private Property<Boolean> createDefaultSTVThumbnail = new Property<Boolean>(Boolean.FALSE);

    private Property<Boolean> createPropertyFiles = new Property<Boolean>(Boolean.FALSE);
    
    
    public Property<Boolean> getUpdateMetadata() {
        return updateMetadata;
    }
    
    public Property<Boolean> getImportTV() {
        return importTV;
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

    public Property<GWTMediaFolder> getScanPath() {
        return scanPath;
    }

    public Property<Boolean> getIncludeSubDirs() {
        return includeSubDirs;
    }

    public Property<Boolean> getUpdateWizBin() {
        return updateWizBin;
    }

    /**
     * @return the createDefaultSTVThumbnail
     */
    public Property<Boolean> getCreateDefaultSTVThumbnail() {
        return createDefaultSTVThumbnail;
    }

    /**
     * @return the createPropertyFiles
     */
    public Property<Boolean> getCreatePropertyFiles() {
        return createPropertyFiles;
    }
}
