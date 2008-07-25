package org.jdna.media.metadata;

import java.util.List;

public interface IVideoMetaData {
	public String getTitle();
	public String getYear();
	public String getThumbnailUrl();
	public String getProviderId();
	public String getProviderDataUrl();
	public String getPlot();
	public List<String> getGenres();
	public List<ICastMember> getActors();
	public List<ICastMember> getWriters();
	public List<ICastMember> getDirectors();
	public String getUserRating();
	public String getReleaseDate();
	public String getRuntime();
	public String getAspectRatio();
	public String getCompany();
	public String getMPAARating();
	
	public boolean isUpdated();
}
