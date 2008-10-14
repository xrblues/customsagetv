package org.jdna.media.impl;

import org.jdna.media.IMediaSource;

public class MediaSource implements IMediaSource {
	private String name;
	private String locationUri;
	
	public MediaSource(String name, String locationUri) {
		super();
		this.name = name;
		this.locationUri = locationUri;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocationUri() {
		return locationUri;
	}
	public void setLocationUri(String locationUri) {
		this.locationUri = locationUri;
	}
	
}
