package org.jdna.metadataupdater;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(label="Application Configuration", name = "metadataUpdater", requiresKey = false, description = "Configuration for Main Metadata Updater")
public class MetadataUpdaterConfiguration {
    @Field(label="Overwrite Metadata", description = "Set to true, if you want to overwrite existing property files")
    private boolean overwriteProperties = false;
    
    @Field(label="Overwrite Posters", description = "Set to true, if you want to overwrite existing thumbnails")
    private boolean overwriteThumbnails = false;

    @Field(label="Overwrite Backdrops", description = "Set to true, if you want to overwrite existing backdrop images")
    private boolean overwriteBackdrops = false;
    
    @Field(label="Media Folder", description = "Default Folder to Scan (GUI Only)")
    private String guiFolderToScan = null;
    
    @Field(label="Display Results Size", description = "Number of results to display in the search results")
    private int searchResultDisplaySize = 10;

    @Field(label="Agressive Searching", description="Will attempt multiple searches if necessary")
    private boolean aggressiveSearches = true;
    
    @Field(label="Refresh SageTV", description="Once Scanning is Complete, notify SageTV of the changes.")
    private boolean refreshSageTV = false;
    
    @Field(label="Automatic Update", description="Automatically update by auto selecting 'best' search result.")
    private boolean automaticUpdate = true;
    
    @Field(label="Backdrops Only", description="Only Find/Process Backdrops.  (ie, don't search and update metadata/posters)")
    private boolean onlyProcessBackdrops = false;

    @Field(label="Ignore Backdrops", description="Ignore processing for backdrops")
    private boolean ignoreBackdrops = false;
    
    @Field(label="Process Sub-Folders", description="Recursively process sub folders")
    private boolean recurseFolders = false;
    
    @Field(label="Process Missing Metadata Only", description="Only process files that is missing metadata")
    private boolean processMissingMetadataOnly = true;
    
    @Field(label="Poster Image Width", description="Rescale Posters to this width")
    private int posterImageWidth=500;
    
    @Field(label="Refresh Indexes", description="Pass hint to Providers that maintain their own indexes, to reindex their content")
    private boolean refreshIndexes = false;
    
    public boolean isOverwriteProperties() {
        return overwriteProperties;
    }

    public void setOverwriteProperties(boolean overwriteProperties) {
        this.overwriteProperties = overwriteProperties;
    }

    public int getSearchResultDisplaySize() {
        return searchResultDisplaySize;
    }

    public void setSearchResultDisplaySize(int searchResultDisplaySize) {
        this.searchResultDisplaySize = searchResultDisplaySize;
    }

    public boolean isAggressiveSearches() {
        return aggressiveSearches;
    }

    public void setAggressiveSearches(boolean aggressiveSearches) {
        this.aggressiveSearches = aggressiveSearches;
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

    public boolean isOnlyProcessBackdrops() {
        return onlyProcessBackdrops;
    }

    public void setOnlyProcessBackdrops(boolean onlyProcessBackdrops) {
        this.onlyProcessBackdrops = onlyProcessBackdrops;
    }

    public String getGuiFolderToScan() {
        return guiFolderToScan;
    }

    public void setGuiFolderToScan(String guiFolderToScan) {
        this.guiFolderToScan = guiFolderToScan;
    }

    public MetadataUpdaterConfiguration() {
    }

    public void setOverwriteThumbnails(boolean b) {
        this.overwriteThumbnails = b;
    }

    public boolean isOverwriteThumbnails() {
        return overwriteThumbnails;
    }

    public boolean isOverwriteBackdrops() {
        return overwriteBackdrops;
    }

    public void setOverwriteBackdrops(boolean overwriteBackdrops) {
        this.overwriteBackdrops = overwriteBackdrops;
    }

    public boolean isRecurseFolders() {
        return recurseFolders;
    }

    public void setRecurseFolders(boolean recurseFolders) {
        this.recurseFolders = recurseFolders;
    }

    public boolean isIgnoreBackdrops() {
        return ignoreBackdrops;
    }

    public void setIgnoreBackdrops(boolean ignoreBackdrops) {
        this.ignoreBackdrops = ignoreBackdrops;
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

    public boolean isRefreshIndexes() {
        return refreshIndexes;
    }

    public void setRefreshIndexes(boolean refreshIndexes) {
        this.refreshIndexes = refreshIndexes;
    }

}
