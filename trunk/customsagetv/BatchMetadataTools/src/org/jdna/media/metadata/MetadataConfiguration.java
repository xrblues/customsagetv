package org.jdna.media.metadata;

import org.jdna.media.metadata.impl.dvdproflocal.LocalDVDProfMetaDataProvider;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;
import org.jdna.media.metadata.impl.nielm.NielmIMDBMetaDataProvider;
import org.jdna.media.metadata.impl.themoviedb.TheMovieDBMetadataProvider;
import org.jdna.persistence.annotations.Field;
import org.jdna.persistence.annotations.Table;

@Table(label="Metadata Configuration", name = "metadata", requiresKey = false, description = "Configuration for Metadata")
public class MetadataConfiguration {
    @Field(label="Persistence Classname", description = "Default class name for storing metadata")
    private String persistenceClass       = org.jdna.media.metadata.impl.sage.SageTVWithCentralFanartFolderPersistence.class.getName();

    @Field(label="Registered Providers", description = "Comma separated list of known metadata providers (ie, can be used for searching for metadata)")
    private String videoMetadataProviders = IMDBMetaDataProvider.class.getName() + "," + NielmIMDBMetaDataProvider.class.getName() + "," + "," + LocalDVDProfMetaDataProvider.class.getName() + "," + TheMovieDBMetadataProvider.class.getName();

    @Field(label="Ignore Words in Title", description = "Comma separated list of words that will be removed from a title when doing a search")
    private String wordsToClean           = "dvd,dvdrip,cam,ts,tc,scr,screener,dvdscr,xvid,divx,avi,vrs,repack,mallat,proper,dmt,dmd,stv";

    @Field(label="Metadata Provider(s)", description = "Default provider id to use (comma separate, if more than 1)")
    private String defaultProviderId      = "tvdb.xml,themoviedb.org,themoviedb.org-2,imdb.xml,imdb";
    
    @Field(label="Good Score Threshold", description = "Score which must be exceeded to consider a result a good match")
    private float goodScoreThreshold = 0.9f;

    @Field(label="Score Alternate Titles", description = "If true, then providers will check alternate titles for matches.")
    private boolean scoreAlternateTitles = true;

    public MetadataConfiguration() {
    }

    public String getPersistenceClass() {
        return persistenceClass;
    }

    public void setPersistenceClass(String persistenceClass) {
        this.persistenceClass = persistenceClass;
    }

    public String getMediaMetadataProviders() {
        return videoMetadataProviders;
    }

    public void setVideoMetadataProviders(String videoMetadataProviders) {
        this.videoMetadataProviders = videoMetadataProviders;
    }

    public String getWordsToClean() {
        return wordsToClean;
    }

    public void setWordsToClean(String wordsToClean) {
        this.wordsToClean = wordsToClean;
    }

    public String getDefaultProviderId() {
        return defaultProviderId;
    }

    public void setDefaultProviderId(String defaultProviderId) {
        this.defaultProviderId = defaultProviderId;
    }
    
    public float getGoodScoreThreshold(){
    	return goodScoreThreshold;
    }
    
    public void setGoodScoreThreshold(float goodScoreThreshold){
    	this.goodScoreThreshold = goodScoreThreshold;
    }

    public boolean isScoreAlternateTitles() {
        return scoreAlternateTitles;
    }

    public void setScoreAlternateTitles(boolean scoreAlternateTitles) {
        this.scoreAlternateTitles = scoreAlternateTitles;
    }
}
