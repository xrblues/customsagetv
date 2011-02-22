package org.jdna.bmt.web.client.ui.prefs;

import java.io.Serializable;

public class PluginDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String description;
	private String version;
	private String id;
	private String author;
	public long getInstallDate() {
		return installDate;
	}

	public void setInstallDate(long installDate) {
		this.installDate = installDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getId() {
		return id;
	}

	public String getAuthor() {
		return author;
	}

	public long getCreatedDate() {
		return createdDate;
	}

	public String[] getDemoVideos() {
		return demoVideos;
	}

	public String[] getPluginDependencies() {
		return pluginDependencies;
	}

	public long getLastModified() {
		return lastModified;
	}

	public String getReleaseNotes() {
		return releaseNotes;
	}

	public String[] getScreenShots() {
		return screenShots;
	}

	public String[] getPluginWebsites() {
		return pluginWebsites;
	}

	private long createdDate;
	private String[] demoVideos;
	private String[] pluginDependencies;
	private long installDate;
	private long lastModified;
	private String releaseNotes;
	private String[] screenShots;
	private String[] pluginWebsites;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getVersion() {
		return version;
	}

	public PluginDetail() {
	}

	public void setName(String getPluginName) {
		this.name=getPluginName;
	}

	public void setDescription(String getPluginDescription) {
		this.description = getPluginDescription;
	}

	public void setVersion(String getPluginVersion) {
		this.version=getPluginVersion;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setAuthor(String author) {
		this.author=author;
	}

	public void setCreatedDate(long date) {
		this.createdDate=date;
	}

	public void setDemoVideos(String[] videos) {
		this.demoVideos = videos;
	}

	public void setPluginDependencies(String[] deps) {
		this.pluginDependencies =  deps;
	}

	public void setInstalledDate(long date) {
		this.installDate =date; 
	}

	public void setLastModified(long date) {
		this.lastModified=date;
	}

	public void setReleaseNotes(String notes) {
		this.releaseNotes = notes;
	}

	public void setScreenShots(String[] screens) {
		this.screenShots=screens;
	}

	public void setPluginWebsites(String[] sites) {
		this.pluginWebsites = sites;
	}
}
