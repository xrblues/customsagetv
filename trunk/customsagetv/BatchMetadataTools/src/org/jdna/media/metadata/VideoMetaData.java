package org.jdna.media.metadata;

import java.io.Serializable;


public class VideoMetaData implements IVideoMetaData, Serializable {
	private static final long serialVersionUID = 1;
	
	private String[] genres;
	private ICastMember[] actors;
	private ICastMember[] directors;
	private ICastMember[] writers;
	private String aspectRatio;
	private String company;
	private String MPAARating;
	private String plot;
	private String providerDataUrl;
	private String releaseDate;
	private String runtime;
	private String thumbnailUrl;
	private String title;
	private String userRating;
	private String year;
	private String providerId;
	private boolean updated;
	private boolean thumbnailUpdated;

	public VideoMetaData() {
	}
	
	public VideoMetaData(IVideoMetaData md) {
		setGenres(md.getGenres());

		ICastMember[] mdCM = md.getActors();
		if (mdCM!=null) {
			CastMember[] cast = new CastMember[mdCM.length];
			for (int i=0;i<mdCM.length;i++) {
				cast[i] = new CastMember(mdCM[i]);
			}
			setActors(cast);
		}
		
		mdCM = md.getDirectors();
		if (mdCM!=null) {
			CastMember[] cast = new CastMember[mdCM.length];
			for (int i=0;i<mdCM.length;i++) {
				cast[i] = new CastMember(mdCM[i]);
			}
			setDirectors(cast);
		}

		mdCM = md.getWriters();
		if (mdCM!=null) {
			CastMember[] cast = new CastMember[mdCM.length];
			for (int i=0;i<mdCM.length;i++) {
				cast[i] = new CastMember(mdCM[i]);
			}
			setWriters(cast);
		}

		setAspectRatio(md.getAspectRatio());
		setCompany(md.getCompany());
		setMPAARating(md.getMPAARating());
		setPlot(md.getPlot());
		setProviderDataUrl(md.getProviderDataUrl());
		setProviderId(md.getProviderId());
		setReleaseDate(md.getReleaseDate());
		setRuntime(md.getRuntime());
		setThumbnailUrl(md.getThumbnailUrl());
		setTitle(md.getTitle());
		setUpdated(md.isUpdated());
		setUserRating(md.getUserRating());
		setYear(md.getYear());
	}
	
	public ICastMember[] getActors() {
		return actors;
	}
	public void setActors(ICastMember[] actors) {
		this.actors = actors;
	}
	public String getAspectRatio() {
		return aspectRatio;
	}
	public void setAspectRatio(String aspectRatio) {
		this.aspectRatio = aspectRatio;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public ICastMember[] getDirectors() {
		return directors;
	}
	public void setDirectors(ICastMember[] directors) {
		this.directors = directors;
	}
	public String[] getGenres() {
		return genres;
	}
	public void setGenres(String[] genres) {
		this.genres = genres;
	}
	public String getMPAARating() {
		return MPAARating;
	}
	public void setMPAARating(String rating) {
		MPAARating = rating;
	}
	public String getPlot() {
		return plot;
	}
	public void setPlot(String plot) {
		this.plot = plot;
	}
	public String getProviderDataUrl() {
		return providerDataUrl;
	}
	public void setProviderDataUrl(String providerDataUrl) {
		this.providerDataUrl = providerDataUrl;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getRuntime() {
		return runtime;
	}
	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUserRating() {
		return userRating;
	}
	public void setUserRating(String userRating) {
		this.userRating = userRating;
	}
	public ICastMember[] getWriters() {
		return writers;
	}
	
	public void setWriters(ICastMember writers[]) {
		this.writers = writers;
	}
	
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getProviderId() {
		return providerId;
	}
	public boolean isUpdated() {
		return updated;
	}
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	public boolean isThumbnailUpdated() {
		return thumbnailUpdated;
	}

	public void setThumbnailUpdated(boolean b) {
		this.thumbnailUpdated=b;
	}
}
