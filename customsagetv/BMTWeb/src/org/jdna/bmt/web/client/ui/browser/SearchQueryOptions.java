package org.jdna.bmt.web.client.ui.browser;

import java.io.Serializable;

import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.util.Property;
import org.jdna.bmt.web.client.util.StringUtils;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Field;

import sagex.phoenix.fanart.MediaType;

public class SearchQueryOptions implements Serializable {
    private Property<String> provider = new Property<String>();
    private Property<String> searchTitle = new Property<String>();
    private Property<String> episodeTitle = new Property<String>();
    private Property<String> year = new Property<String>();
    private Property<String> season = new Property<String>();
    private Property<String> episode = new Property<String>();
    private Property<String> type = new Property<String>();
    
    public SearchQueryOptions() {
    }

    public SearchQueryOptions(GWTMediaFile mf) {
        GWTMediaMetadata md = mf.getMetadata();
        searchTitle.set(md.getString(MetadataKey.MEDIA_TITLE));
        year.set(md.getString(MetadataKey.YEAR));
        if ("TV".equals(md.getString(MetadataKey.MEDIA_TYPE))) {
            type.set("TV");
            episodeTitle.set(md.getString(MetadataKey.EPISODE_TITLE));
            episode.set(md.getString(MetadataKey.EPISODE));
            season.set(md.getString(MetadataKey.SEASON));
        } else {
            type.set("Movie");
        }
        
        if (StringUtils.isEmpty(searchTitle.get())) {
            searchTitle.set(mf.getTitle());
        }
    }
    
    public SearchQuery getSearchQuery() {
        SearchQuery q = new SearchQuery();
        q.set(Field.QUERY, searchTitle.get());
        q.set(Field.YEAR, year.get());
        if ("TV".equals(type.get())) {
            q.setMediaType(MediaType.TV);
            q.set(Field.EPISODE_TITLE, episodeTitle.get());
            q.set(Field.EPISODE, episode.get());
            q.set(Field.SEASON, season.get());
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

    public void setYear(Property<String> year) {
        this.year = year;
    }

    public Property<String> getSeason() {
        return season;
    }

    public void setSeason(Property<String> season) {
        this.season = season;
    }

    public Property<String> getEpisode() {
        return episode;
    }

    public void setEpisode(Property<String> episode) {
        this.episode = episode;
    }

    public Property<String> getType() {
        return type;
    }

    public void setType(Property<String> type) {
        this.type = type;
    }

    public Property<String> getProvider() {
        return provider;
    }

    public void setProvider(Property<String> provider) {
        this.provider = provider;
    }
}
