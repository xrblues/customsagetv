package org.jdna.media.metadata.impl.imdb;

import java.util.ArrayList;
import java.util.List;

import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoSearchResult;
import org.jdna.media.metadata.MetaDataException;

public class IMDBMovieMetaData implements IVideoMetaData, IVideoSearchResult {

	private List<ICastMember> actors;
	private String aspectRatio;
	private String company;
	private List<ICastMember> directors;
	private List<String> genres;
	private String MPAARating;
	private String plot;
	private String providerDataUrl;
	private String releaseDate;
	private String runtime;
	private String thumbnailUrl;
	private String title;
	private String userRating;
	private List<ICastMember> writers;
	private String year;

	public IMDBMovieMetaData() {
	}
	
	public List<ICastMember> getActors() {
		if (actors==null) actors = new ArrayList<ICastMember>();
		return actors;
	}

	public String getAspectRatio() {
		return aspectRatio;
	}

	public String getCompany() {
		return company;
	}

	public List<ICastMember> getDirectors() {
		if (directors==null) directors = new ArrayList<ICastMember>();
		return directors;
	}

	public List<String> getGenres() {
		if (genres == null) genres = new ArrayList<String>();
		return genres;
	}

	public String getMPAARating() {
		return MPAARating;
	}

	public String getPlot() {
		return plot;
	}

	public String getProviderDataUrl() {
		return providerDataUrl;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public String getRuntime() {
		return runtime;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public String getTitle() {
		return title;
	}

	public String getUserRating() {
		return userRating;
	}

	public List<ICastMember> getWriters() {
		if (writers==null) writers = new ArrayList<ICastMember>();
		return writers;
	}

	public String getYear() {
		return year;
	}

	public void setAspectRatio(String aspectRatio) {
		this.aspectRatio = aspectRatio;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setMPAARating(String rating) {
		MPAARating = rating;
	}

	public void setPlot(String plot) {
		this.plot = plot;
	}

	public void setProviderDataUrl(String providerDataUrl) {
		this.providerDataUrl = providerDataUrl;
	}

	public void setReleaseDate(String releaseDate) {
		this.releaseDate = releaseDate;
	}

	public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUserRating(String userRating) {
		this.userRating = userRating;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public boolean isUpdated() {
		// IMDB metadata is never considerd updated/modified
		return false;
	}

	public String getProviderId() {
		return IMDBMetaDataProvider.PROVIDER_ID;
	}

	public IVideoMetaData getMetaData() throws MetaDataException {
		// just return a reference to ourselves....
		return this;
	}

	public int getResultType() {
		// we are an exact match
		return IVideoSearchResult.RESULT_TYPE_EXACT_MATCH;
	}
}
