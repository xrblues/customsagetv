package org.jdna.media.metadata;

import java.io.Serializable;


public class CoverResult implements ICoverResult, Serializable{
	private static final long serialVersionUID = 1L;
	
	private String iconUrl, imageInfo, imageUrl;
	private float score;

	public CoverResult() {
	}

	public CoverResult(String iconUrl, String imageUrl, String imageInfo, float score) {
		super();
		this.iconUrl = iconUrl;
		this.imageUrl = imageUrl;
		this.imageInfo = imageInfo;
		this.score = score;
	}

	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getImageInfo() {
		return imageInfo;
	}
	public void setImageInfo(String imageInfo) {
		this.imageInfo = imageInfo;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
}
