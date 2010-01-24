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
    private String fileRenamePattern=null;
    private boolean importAsTV = false;
    private boolean useTitleMasks = false;
    private boolean createProperties=true;
    private boolean updateWizBin=false;
    private boolean touchingFiles=true;
    
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
	public String getFileRenamePattern() {
		return fileRenamePattern;
	}
	public void setFileRenamePattern(String fileRenamePattern) {
		this.fileRenamePattern = fileRenamePattern;
	}

    public boolean isImportAsTV() {
        return importAsTV;
    }

    public void setImportAsTV(boolean importAsTV) {
        this.importAsTV = importAsTV;
    }

    public boolean isUseTitleMasks() {
        return useTitleMasks;
    }

    public void setUseTitleMasks(boolean useTitleMasks) {
        this.useTitleMasks = useTitleMasks;
    }

    public boolean isCreateProperties() {
        return createProperties;
    }

    /**
     * @return the updateWizBin
     */
    public boolean isUpdateWizBin() {
        return updateWizBin;
    }

    /**
     * @param updateWizBin the updateWizBin to set
     */
    public void setUpdateWizBin(boolean updateWizBin) {
        this.updateWizBin = updateWizBin;
    }

    /**
     * @param createProperties the createProperties to set
     */
    public void setCreateProperties(boolean createProperties) {
        this.createProperties = createProperties;
    }

    /**
     * @return the touchingFiles
     */
    public boolean isTouchingFiles() {
        return touchingFiles;
    }

    /**
     * @param touchingFiles the touchingFiles to set
     */
    public void setTouchingFiles(boolean touchingFiles) {
        this.touchingFiles = touchingFiles;
    }
}
