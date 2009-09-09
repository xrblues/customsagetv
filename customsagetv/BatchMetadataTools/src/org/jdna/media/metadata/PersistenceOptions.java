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
}
