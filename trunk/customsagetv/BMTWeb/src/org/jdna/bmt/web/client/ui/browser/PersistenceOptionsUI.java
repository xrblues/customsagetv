package org.jdna.bmt.web.client.ui.browser;

import java.io.Serializable;

import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.util.Property;

public class PersistenceOptionsUI implements Serializable {
    private Property<Boolean> includeSubDirs = new Property<Boolean>(Boolean.TRUE);
    private Property<Boolean> scanOnlyMissingMetadata = new Property<Boolean>(Boolean.TRUE);
    private Property<Boolean> importTVAsRecordings = new Property<Boolean>(Boolean.FALSE);
    
	private Property<GWTMediaFolder> folder = new Property<GWTMediaFolder>();
    
    public PersistenceOptionsUI() {
    }
    
    public Property<Boolean> getScanOnlyMissingMetadata() {
        return scanOnlyMissingMetadata;
    }
    
    public Property<Boolean> getIncludeSubDirs() {
        return includeSubDirs;
    }

	public Property<GWTMediaFolder> getScanPath() {
		return folder;
	}

	public Property<Boolean> getImportTVAsRecordings() {
		return importTVAsRecordings;
	}
}
