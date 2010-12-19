package org.jdna.bmt.web.client.media;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jdna.bmt.web.client.util.Property;

@SuppressWarnings("serial")
public class GWTMediaMetadata implements Serializable {
	private Property<Boolean> preserveRecordingMetadata = new Property<Boolean>(true);
	
	private Property<String> description = new Property<String>();
	private Property<String> epsisodeName  = new Property<String>();
	private Property<String> extendedRatings  = new Property<String>();
	private Property<String> externalId  = new Property<String>();
	private Property<String> misc  = new Property<String>();
	private Property<String> origAirDate  = new Property<String>();
	private Property<String> parentalRating  = new Property<String>();
	private Property<String> rated  = new Property<String>();
	private Property<String> runningTime  = new Property<String>();
	private Property<String> title  = new Property<String>();
	private Property<String> year  = new Property<String>();
	private Property<String> discNumber  = new Property<String>();
	private Property<String> episodeNumber  = new Property<String>();
	private Property<String> imdbid  = new Property<String>();
	private Property<String> mediaProviderDataId  = new Property<String>();
	private Property<String> mediaProviderId  = new Property<String>();
	private Property<String> mediaTitle  = new Property<String>();
	private Property<String> mediaType  = new Property<String>();
	private Property<String> seasonNumber  = new Property<String>();
	private Property<String> userRating = new Property<String>();
	private Property<String> genres = new Property<String>();
	
	private List<GWTCastMember> actors = new ArrayList<GWTCastMember>();
	private List<GWTCastMember> directors = new ArrayList<GWTCastMember>();
	private List<GWTCastMember> guests = new ArrayList<GWTCastMember>();
	private List<GWTCastMember> writers = new ArrayList<GWTCastMember>();
	private List<GWTMediaArt> fanart = new ArrayList<GWTMediaArt>();

	public GWTMediaMetadata() {
    }

	public Property<String> getGenres() {
		return genres;
	}

	public Property<String> getDescription() {
		return description;
	}

	public Property<String> getEpisodeName() {
		return epsisodeName;
	}

	public Property<String> getExtendedRatings() {
		return extendedRatings;
	}

	public Property<String> getExternalID() {
		return externalId;
	}

	public Property<String> getMisc() {
		return misc;
	}

	public Property<String> getOriginalAirDate() {
		return origAirDate;
	}

	public Property<String> getParentalRating() {
		return parentalRating;
	}

	public Property<String> getRated() {
		return rated;
	}

	public Property<String> getRunningTime() {
		return runningTime;
	}

	public Property<String> getTitle() {
		return title;
	}

	public Property<String> getYear() {
		return year;
	}

	public Property<String> getDiscNumber() {
		return discNumber;
	}

	public Property<String> getEpisodeNumber() {
		return episodeNumber;
	}

	public Property<String> getIMDBID() {
		return imdbid;
	}

	public Property<String> getMediaProviderDataID() {
		return mediaProviderDataId;
	}

	public Property<String> getMediaProviderID() {
		return mediaProviderId;
	}

	public Property<String> getMediaTitle() {
		return mediaTitle;
	}

	public Property<String> getMediaType() {
		return mediaType;
	}

	public Property<String> getSeasonNumber() {
		return seasonNumber;
	}

	public Property<String> getUserRating() {
		return userRating;
	}

	public List<GWTCastMember> getActors() {
		return actors;
	}

	public List<GWTCastMember> getDirectors() {
		return directors;
	}

	public List<GWTCastMember> getGuests() {
		return guests;
	}

	public List<GWTCastMember> getWriters() {
		return writers;
	}
	
	public List<GWTMediaArt> getFanart() {
		return fanart;
	}

	public Property<Boolean> getPreserveRecordingMetadata() {
		return preserveRecordingMetadata;
	}
}
