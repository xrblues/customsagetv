package org.jdna.bmt.web.client.media;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import sagex.phoenix.metadata.IMetadataSearchResult;
import sagex.phoenix.metadata.MediaType;

public class GWTMediaSearchResult implements Serializable, IMetadataSearchResult {
    private static final long serialVersionUID = 1L;
    private String url;
    private String providerId;
    private float score;
    private String title;
    private int year;
    private int mediaFileId;
    private MediaType type;
    private String id;
    private Map<String,String> extra=new HashMap<String, String>();
    
    public GWTMediaSearchResult() {
    }

    public String getUrl() {
        return url;
    }

    public void setProviderId(String providerId) {
        this.providerId=providerId;
    }

    public String getProviderId() {
        return providerId;
    }

    public float getScore() {
        return score;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMediaFileId() {
        return mediaFileId;
    }
    
    public void setMediaFileId(int id) {
        this.mediaFileId=id;
    }

    public MediaType getMediaType() {
        return type;
    }
    
    public void setMediaType(MediaType type) {
        this.type=type;
    }

    public Map<String, String> getExtra() {
        return extra;
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id=id;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GWTMediaSearchResult [");
		if (extra != null) {
			builder.append("extra=");
			builder.append(extra);
			builder.append(", ");
		}
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		builder.append("mediaFileId=");
		builder.append(mediaFileId);
		builder.append(", ");
		if (providerId != null) {
			builder.append("providerId=");
			builder.append(providerId);
			builder.append(", ");
		}
		builder.append("score=");
		builder.append(score);
		builder.append(", ");
		if (title != null) {
			builder.append("title=");
			builder.append(title);
			builder.append(", ");
		}
		if (type != null) {
			builder.append("type=");
			builder.append(type);
			builder.append(", ");
		}
		if (url != null) {
			builder.append("url=");
			builder.append(url);
			builder.append(", ");
		}
		builder.append("year=");
		builder.append(year);
		builder.append("]");
		return builder.toString();
	}
}
