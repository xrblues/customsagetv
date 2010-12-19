package org.jdna.bmt.web.client.ui.browser;

import java.io.Serializable;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.util.Property;
import org.jdna.bmt.web.client.util.StringUtils;

import sagex.phoenix.metadata.MediaType;
import sagex.phoenix.metadata.search.SearchQuery;
import sagex.phoenix.metadata.search.SearchQuery.Field;
import sagex.phoenix.util.Hints;

public class SearchQueryOptions implements Serializable {
	private static final long serialVersionUID = 1L;
	private Property<String> provider = new Property<String>();
    private Property<String> searchTitle = new Property<String>();
    private Property<String> episodeTitle = new Property<String>();
    private Property<String> year = new Property<String>();
    private Property<String> season = new Property<String>();
    private Property<String> episode = new Property<String>();
    private Property<String> type = new Property<String>();
    private Property<String> airedDate = new Property<String>();
    
    public SearchQueryOptions() {
    }

    public SearchQueryOptions(GWTMediaFile mf) {
        GWTMediaMetadata md = mf.getMetadata();
        searchTitle.set(md.getEpisodeName().get());
        year.set(String.valueOf(md.getYear().get()));
        if ("TV".equals(md.getMediaType().get())) {
            type.set("TV");
            searchTitle.set(md.getTitle().get());
            episodeTitle.set(md.getEpisodeName().get());
            episode.set(String.valueOf(md.getEpisodeNumber().get()));
            season.set(String.valueOf(md.getSeasonNumber().get()));
            airedDate.set(md.getOriginalAirDate().get());
        } else {
            type.set("Movie");
        }
        
        if (StringUtils.isEmpty(searchTitle.get())) {
            searchTitle.set(mf.getTitle());
        }
    }
    
    public SearchQuery getSearchQuery() {
        SearchQuery q = new SearchQuery(new Hints());
        q.set(Field.QUERY, searchTitle.get());
        q.set(Field.YEAR, year.get());
        if ("TV".equals(type.get())) {
            q.setMediaType(MediaType.TV);
            q.set(Field.EPISODE_TITLE, episodeTitle.get());
            q.set(Field.EPISODE, episode.get());
            q.set(Field.SEASON, season.get());
            q.set(Field.EPISODE_DATE, airedDate.get());
        } else {
            q.setMediaType(MediaType.MOVIE);
        }
        
        return q;
    }

    public Property<String> getSearchTitle() {
        return searchTitle;
    }

    public void setSearchTitle(Property<String> searchTitle) {
        this.searchTitle = searchTitle;
    }

    public Property<String> getEpisodeTitle() {
        return episodeTitle;
    }

    public void setEpisodeTitle(Property<String> episodeTitle) {
        this.episodeTitle = episodeTitle;
    }

    public Property<String> getYear() {
        return year;
    }

    public Property<String> getSeason() {
        return season;
    }

    public Property<String> getEpisode() {
        return episode;
    }

    public Property<String> getType() {
        return type;
    }

    public Property<String> getProvider() {
        return provider;
    }

	public Property<String> getAiredDate() {
		return airedDate;
	}
}
