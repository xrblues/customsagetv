package org.jdna.media.metadata;


public interface IVideoMetaData {
	public String getTitle();
	public void setTitle(String title);
	
	public String getYear();
	public void setYear(String year);
	
	public String getThumbnailUrl();
	public void setThumbnailUrl(String url);
	
	public String getProviderId();
	public void setProviderId(String id);
	
	public String getProviderDataUrl();
	public void setProviderDataUrl(String url);
	
	public String getPlot();
	public void setPlot(String plot);
	
	public String[] getGenres();
	public void setGenres(String[] genres);
	
	public ICastMember[] getActors();
	public void setActors(ICastMember[] memebers);
	
	public ICastMember[] getWriters();
	public void setWriters(ICastMember[] writers);
	
	public ICastMember[] getDirectors();
	public void setDirectors(ICastMember[] directors);
	
	public String getUserRating();
	public void setUserRating(String rating);
	
	public String getReleaseDate();
	public void setReleaseDate(String date);
	
	public String getRuntime();
	public void setRuntime(String runtime);
	
	public String getAspectRatio();
	public void setAspectRatio(String ratio);
	
	public String getCompany();
	public void setCompany(String company);
	
	public String getMPAARating();
	public void setMPAARating(String rating);
	
	public boolean isUpdated();
	public void setUpdated(boolean updated);
	
	public boolean isThumbnailUpdated();
	public void setThumbnailUpdated(boolean b);
}
