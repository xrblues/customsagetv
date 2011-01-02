package org.jdna.bmt.web.client.media;

import java.io.Serializable;

public class GWTPersistenceOptions implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean importAsTV = false;
    private boolean useTitleMasks = true;
    
    public GWTPersistenceOptions() {
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
