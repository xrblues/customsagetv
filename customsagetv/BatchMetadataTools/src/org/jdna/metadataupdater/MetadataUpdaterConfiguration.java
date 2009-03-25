package org.jdna.metadataupdater;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(label="Application Configuration", name = "metadataUpdater", requiresKey = false, description = "Configuration for Main Metadata Updater")
public class MetadataUpdaterConfiguration {
    @Field(label="Overwrite", description = "If true, then it will overwrite properties and images")
    private boolean overwrite = false;
    
    @Field(label="Media Folder", description = "Default Folder to Scan (GUI Only)")
    private String guiFolderToScan = null;
    
    @Field(label="Display Results Size", description = "Number of results to display in the search results")
    private int searchResultDisplaySize = 10;

    @Field(label="Refresh SageTV", description="Once Scanning is Complete, notify SageTV of the changes.")
    private boolean refreshSageTV = false;
    
    @Field(label="Automatic Update", description="Automatically update by auto selecting 'best' search result.")
    private boolean automaticUpdate = true;
    
    @Field(label="Process Sub-Folders", description="Recursively process sub folders")
    private boolean recurseFolders = false;
    
    @Field(label="Process Missing Metadata Only", description="Only process files that is missing metadata")
    private boolean processMissingMetadataOnly = true;
    
    @Field(label="Poster Image Width", description="Rescale Posters to this width")
    private int posterImageWidth=500;
    
    @Field(label="Fanart Enabled", description="Enable Fanart downloading")
    private boolean fanartEnabled = true;
    
    @Field(label="Central Fanart Folder", description="Location of the central fanart folder")
    private String fanartCentralFolder = null;
    
    @Field(label="Remember Selected Searches", description="Remember a search result when you select it form a list, for use in later searches.")
    private boolean rememberSelectedSearches = true;
    
    public String getFanartCentralFolder() {
        return fanartCentralFolder;
    }

    public void setCentralFanartFolder(String centralFanartFolder) {
        this.fanartCentralFolder = centralFanartFolder;
    }

    public int getSearchResultDisplaySize() {
        return searchResultDisplaySize;
    }

    public void setSearchResultDisplaySize(int searchResultDisplaySize) {
        this.searchResultDisplaySize = searchResultDisplaySize;
    }

    public boolean isRefreshSageTV() {
        return refreshSageTV;
    }

    public void setRefreshSageTV(boolean refreshSageTV) {
        this.refreshSageTV = refreshSageTV;
    }

    public boolean isAutomaticUpdate() {
        return automaticUpdate;
    }

    public void setAutomaticUpdate(boolean automaticUpdate) {
        this.automaticUpdate = automaticUpdate;
    }

    public String getGuiFolderToScan() {
        return guiFolderToScan;
    }

    public void setGuiFolderToScan(String guiFolderToScan) {
        this.guiFolderToScan = guiFolderToScan;
    }

    public MetadataUpdaterConfiguration() {
    }

    public void setOverwrite(boolean b) {
        this.overwrite = b;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public boolean isRecurseFolders() {
        return recurseFolders;
    }

    public void setRecurseFolders(boolean recurseFolders) {
        this.recurseFolders = recurseFolders;
    }

    public boolean isFanartEnabled() {
        return fanartEnabled;
    }

    public void setFanartEnabled(boolean fanartEnabled) {
        this.fanartEnabled = fanartEnabled;
    }

    public boolean isProcessMissingMetadataOnly() {
        return processMissingMetadataOnly;
    }

    public void setProcessMissingMetadataOnly(boolean b) {
        this.processMissingMetadataOnly = b;
    }

    public int getPosterImageWidth() {
        return posterImageWidth;
    }

    public void setPosterImageWidth(int posterImageWidth) {
        this.posterImageWidth = posterImageWidth;
    }

    public boolean isRememberSelectedSearches() {
        return rememberSelectedSearches;
    }

    public void setRememberSelectedSearches(boolean rememberSelectedSearches) {
        this.rememberSelectedSearches = rememberSelectedSearches;
    }
}
