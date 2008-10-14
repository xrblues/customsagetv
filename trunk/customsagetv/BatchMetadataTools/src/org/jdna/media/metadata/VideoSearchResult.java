package org.jdna.media.metadata;

import java.io.Serializable;

public class VideoSearchResult implements IVideoSearchResult, Serializable {
	private static final long serialVersionUID = 1L;
	
	private String providerId, id, title, year;
	private int resultType;
	private transient Object data;
	
	
	public VideoSearchResult() {
	}
	
	public VideoSearchResult(String providerId, int resultType) {
		this.providerId=providerId;
		this.resultType=resultType;
	}

	public VideoSearchResult(String providerId, String id, String title, String year, int resultType) {
		super();
		this.providerId = providerId;
		this.id = id;
		this.title = title;
		this.year = year;
		this.resultType = resultType;
	}

	public String getProviderId() {
		return providerId;
	}
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public int getResultType() {
		return resultType;
	}
	public void setResultType(int resultType) {
		this.resultType = resultType;
	}
	
	/**
	 * An arbitrary piece of data that will have meaning to the provider that created this result.
	 * @return
	 */
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}


}
