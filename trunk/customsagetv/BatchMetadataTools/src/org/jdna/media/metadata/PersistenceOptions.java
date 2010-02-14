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
    private boolean createDefaultSTVThumbnail=false;
    
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

    /**
     * if true, then create thumbnail for use in the default stv.
     * 
     * @return
     */
    public boolean isCreateDefaultSTVThumbnail() {
        return createDefaultSTVThumbnail;
    }
    
    public void setCreateDefaultSTVThumbnail(boolean b) {
        this.createDefaultSTVThumbnail = b;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PersistenceOptions [createDefaultSTVThumbnail=");
        builder.append(createDefaultSTVThumbnail);
        builder.append(", createProperties=");
        builder.append(createProperties);
        builder.append(", fileRenamePattern=");
        builder.append(fileRenamePattern);
        builder.append(", importAsTV=");
        builder.append(importAsTV);
        builder.append(", overwriteFanart=");
        builder.append(overwriteFanart);
        builder.append(", overwriteMetadata=");
        builder.append(overwriteMetadata);
        builder.append(", touchingFiles=");
        builder.append(touchingFiles);
        builder.append(", updateWizBin=");
        builder.append(updateWizBin);
        builder.append(", useTitleMasks=");
        builder.append(useTitleMasks);
        builder.append("]");
        return builder.toString();
    }
}
