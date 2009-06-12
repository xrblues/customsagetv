package org.jdna.bmt.web.client.ui.browser;

import java.io.Serializable;

import org.jdna.bmt.web.client.util.Property;

public class ScanOptions implements Serializable {
    private Property<Boolean> scanAll = new Property<Boolean>(Boolean.FALSE);
    private Property<Boolean> scanDVD = new Property<Boolean>(Boolean.TRUE);
    private Property<Boolean> scanVideo = new Property<Boolean>(Boolean.TRUE);
    private Property<Boolean> scanTV = new Property<Boolean>(Boolean.TRUE);

    private Property<Boolean> scanMissingMetadata = new Property<Boolean>(Boolean.TRUE);
    private Property<Boolean> scanMissingPoster = new Property<Boolean>(Boolean.TRUE);
    private Property<Boolean> scanMissingBackground = new Property<Boolean>(Boolean.TRUE);
    private Property<Boolean> scanMissingBanner = new Property<Boolean>(Boolean.TRUE);
    
    private Property<Boolean> updateMetadata = new Property<Boolean>(Boolean.TRUE);
    private Property<Boolean> importTV = new Property<Boolean>(Boolean.FALSE);
    private Property<Boolean> updateFanart = new Property<Boolean>(Boolean.TRUE);
    private Property<Boolean> dontUpdate = new Property<Boolean>(Boolean.FALSE);
    private Property<Boolean> runInBackground = new Property<Boolean>(Boolean.FALSE);

    private Property<Boolean> overwriteMetadata = new Property<Boolean>(Boolean.FALSE);
    private Property<Boolean> overwriteFanart = new Property<Boolean>(Boolean.FALSE);
    
    public Property<Boolean> getScanAll() {
        return scanAll;
    }
    public Property<Boolean> getScanDVD() {
        return scanDVD;
    }
    public Property<Boolean> getScanVideo() {
        return scanVideo;
    }
    public Property<Boolean> getScanTV() {
        return scanTV;
    }
    public Property<Boolean> getScanMissingMetadata() {
        return scanMissingMetadata;
    }
    public Property<Boolean> getScanMissingPoster() {
        return scanMissingPoster;
    }
    public Property<Boolean> getScanMissingBackground() {
        return scanMissingBackground;
    }
    public Property<Boolean> getScanMissingBanner() {
        return scanMissingBanner;
    }
    public Property<Boolean> getUpdateMetadata() {
        return updateMetadata;
    }
    public Property<Boolean> getImportTV() {
        return importTV;
    }
    public Property<Boolean> getUpdateFanart() {
        return updateFanart;
    }
    public Property<Boolean> getDontUpdate() {
        return dontUpdate;
    }
    public Property<Boolean> getRunInBackground() {
        return runInBackground;
    }
    public Property<Boolean> getOverwriteMetadata() {
        return overwriteMetadata;
    }
    public Property<Boolean> getOverwriteFanart() {
        return overwriteFanart;
    }
}
