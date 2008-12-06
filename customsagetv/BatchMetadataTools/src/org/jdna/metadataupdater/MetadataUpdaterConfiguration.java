package org.jdna.metadataupdater;

import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(name="metadataUpdater", requiresKey=false, description="Configuration for Main Metadata Updater")
public class MetadataUpdaterConfiguration {
	@Field(description="Screen class name that is used for drawing and responding to events")
	private String screenClass = org.jdna.metadataupdater.ConsoleScreen.class.getName();
	
	@Field(description="Set to true, if you want to overwrite existing thumbnails")
	private boolean overwriteThumbnails =false;
	
	public MetadataUpdaterConfiguration() {
	}

	public String getScreenClass() {
		return screenClass;
	}

	public void setScreenClass(String screenClass) {
		this.screenClass = screenClass;
	}

	public void setOverwriteThumbnails(boolean b) {
		this.overwriteThumbnails = b;
	}

	public boolean isOverwriteThumbnails() {
		return overwriteThumbnails;
	}
	
	
}
