package org.jdna.media.metadata;


public interface IVideoMetaData {
	public String getTitle();
	public String getYear();
	public String getThumbnailUrl();
	public String getProviderId();
	public String getProviderDataUrl();
	public String getPlot();
	public String[] getGenres();
	public ICastMember[] getActors();
	public ICastMember[] getWriters();
	public ICastMember[] getDirectors();
	public String getUserRating();
	public String getReleaseDate();
	public String getRuntime();
	public String getAspectRatio();
	public String getCompany();
	public String getMPAARating();
	
	public boolean isUpdated();
}
