package org.jdna.metadataupdater;

import org.jdna.configuration.Field;
import org.jdna.configuration.FieldProxy;
import org.jdna.configuration.Group;
import org.jdna.configuration.GroupProxy;

@Group(label="Application Configuration", path = "bmt/metadataUpdater", description = "Configuration for Main Metadata Updater")
public class MetadataUpdaterConfiguration extends GroupProxy {
    @Field(label="Default Media Folder", description = "Default Folder to Scan when Doing an interactive scan from the Web UI", hidden=true)
    private FieldProxy<String> guiFolderToScan = new FieldProxy<String>(null);
    
    @Field(label="Display Results Size", description = "Number of results to display in the search results", hidden=true)
    private FieldProxy<Integer> searchResultDisplaySize = new FieldProxy<Integer>(10);

    @Field(label="Refresh SageTV", description="Once Scanning is Complete, notify SageTV of the changes. (commandline only)", hidden=true)
    private FieldProxy<Boolean> refreshSageTV = new FieldProxy<Boolean>(false);
    
    @Field(label="Automatic Update", description="Automatically update by auto selecting 'best' search result. (commandline only)", hidden=true)
    private FieldProxy<Boolean> automaticUpdate = new FieldProxy<Boolean>(true);
    
    @Field(label="Process Sub-Folders", description="Recursively process sub folders", hidden=true)
    private FieldProxy<Boolean> recurseFolders = new FieldProxy<Boolean>(false);
    
    @Field(label="Process Missing Metadata Only", description="Only process files that is missing metadata. (commandline only)", hidden=true)
    private FieldProxy<Boolean> processMissingMetadataOnly = new FieldProxy<Boolean>(true);
    
    @Field(label="Fanart Enabled", description="Enable Fanart downloading", fullKey="phoenix/mediametadata/fanartEnabled")
    private FieldProxy<Boolean> fanartEnabled = new FieldProxy<Boolean>(true);
    
    @Field(label="Central Fanart Folder", description="Location of the central fanart folder", fullKey="phoenix/mediametadata/fanartCentralFolder")
    private FieldProxy<String> fanartCentralFolder = new FieldProxy<String>(null);
    
    @Field(label="Remember Selected Searches", description="Remember a search result when you select it form a list, for use in later searches.")
    private FieldProxy<Boolean> rememberSelectedSearches = new FieldProxy<Boolean>(true);

    public MetadataUpdaterConfiguration() {
        super();
        init(this);
    }
    
    public String getFanartCentralFolder() {
        return fanartCentralFolder.getString();
    }

    public void setCentralFanartFolder(String centralFanartFolder) {
        this.fanartCentralFolder.set(centralFanartFolder);
    }

    public int getSearchResultDisplaySize() {
        return searchResultDisplaySize.getInt();
    }

    public void setSearchResultDisplaySize(int searchResultDisplaySize) {
        this.searchResultDisplaySize.set(searchResultDisplaySize);
    }

    public boolean isRefreshSageTV() {
        return refreshSageTV.getBoolean();
    }

    public void setRefreshSageTV(boolean refreshSageTV) {
        this.refreshSageTV.set(refreshSageTV);
    }

    public boolean isAutomaticUpdate() {
        return automaticUpdate.getBoolean();
    }

    public void setAutomaticUpdate(boolean automaticUpdate) {
        this.automaticUpdate.set(automaticUpdate);
    }

    public String getGuiFolderToScan() {
        return guiFolderToScan.getString();
    }

    public void setGuiFolderToScan(String guiFolderToScan) {
        this.guiFolderToScan.set(guiFolderToScan);
    }


    public boolean isRecurseFolders() {
        return recurseFolders.getBoolean();
    }

    public void setRecurseFolders(boolean recurseFolders) {
        this.recurseFolders.set(recurseFolders);
    }

    public boolean isFanartEnabled() {
        return fanartEnabled.getBoolean();
    }

    public void setFanartEnabled(boolean fanartEnabled) {
        this.fanartEnabled.set(fanartEnabled);
    }

    public boolean isProcessMissingMetadataOnly() {
        return processMissingMetadataOnly.getBoolean();
    }

    public void setProcessMissingMetadataOnly(boolean b) {
        this.processMissingMetadataOnly.set(b);
    }

    public boolean isRememberSelectedSearches() {
        return rememberSelectedSearches.getBoolean();
    }

    public void setRememberSelectedSearches(boolean rememberSelectedSearches) {
        this.rememberSelectedSearches.set(rememberSelectedSearches);
    }
}
