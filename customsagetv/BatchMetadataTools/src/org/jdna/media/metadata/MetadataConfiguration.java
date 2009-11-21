package org.jdna.media.metadata;

import org.jdna.media.metadata.impl.dvdproflocal.LocalDVDProfMetaDataProvider;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;
import org.jdna.media.metadata.impl.mymovies.MyMoviesMetadataProvider;
import org.jdna.media.metadata.impl.nielm.NielmIMDBMetaDataProvider;
import org.jdna.media.metadata.impl.themoviedb.TheMovieDBMetadataProvider;
import org.jdna.media.metadata.impl.tvdb.TVDBMetadataProvider;

import sagex.phoenix.configuration.proxy.AField;
import sagex.phoenix.configuration.proxy.AGroup;
import sagex.phoenix.configuration.proxy.FieldProxy;
import sagex.phoenix.configuration.proxy.GroupProxy;

@AGroup(label="Metadata Configuration", path = "bmt/metadata", description = "Configuration for Metadata")
public class MetadataConfiguration extends GroupProxy {
    @AField(label="Persistence Classname", description = "Default class name for storing metadata")
    private FieldProxy<String> persistenceClass       = new FieldProxy<String>(org.jdna.media.metadata.impl.sage.SageTVPropertiesWithCentralFanartPersistence.class.getName());

    @AField(label="Registered Providers", description = "Comma separated list of known metadata providers (ie, can be used for searching for metadata)", editor="array")
    private FieldProxy<String> videoMetadataProviders = new FieldProxy<String>(TVDBMetadataProvider.class.getName()+","+IMDBMetaDataProvider.class.getName() + "," + NielmIMDBMetaDataProvider.class.getName() + "," + "," + LocalDVDProfMetaDataProvider.class.getName() + "," + TheMovieDBMetadataProvider.class.getName() + "," + MyMoviesMetadataProvider.class.getName());

    @AField(label="Ignore Words in Title", description = "Comma separated list of words that will be removed from a title when doing a search", editor="array")
    private FieldProxy<String> wordsToClean           = new FieldProxy<String>("1080p,720p,480p,1080i,720i,480i,dvd,dvdrip,cam,ts,tc,scr,screener,dvdscr,xvid,divx,avi,vrs,repack,mallat,proper,dmt,dmd,stv,HDTV,x264");

    @AField(label="Metadata Provider(s)", description = "Default provider id to use (comma separate, if more than 1)", editor="array")
    private FieldProxy<String> defaultProviderId      = new FieldProxy<String>("tvdb,imdb-2,themoviedb.org,imdb.xml");
    
    @AField(label="Good Score Threshold", description = "Score which must be exceeded to consider a result a good match")
    private FieldProxy<Float> goodScoreThreshold = new FieldProxy<Float>(0.9f);

    @AField(label="Score Alternate Titles", description = "If true, then providers will check alternate titles for matches.")
    private FieldProxy<Boolean> scoreAlternateTitles = new FieldProxy<Boolean>(true);

    @AField(label="Poster width", description="Resize poster to scale using the specified max width")
    private FieldProxy<Integer> posterImageWidth = new FieldProxy<Integer>(200);

    @AField(label="Banner width", description="Resize banner to scale using the specified max width")
    private FieldProxy<Integer> bannerImageWidth = new FieldProxy<Integer>(-1);

    @AField(label="Background width", description="Resize backgrond to scale using the specified max width")
    private FieldProxy<Integer> backgroundImageWidth = new FieldProxy<Integer>(-1);
    
    @AField(label="Max Images to Download", description="Maximum # of images within each fanart type to download.")
    private FieldProxy<Integer> maxDownloadableImages = new FieldProxy<Integer>(5);
    
    @AField(label="Default STV Poster Compatibility", description="When writing fanart, if this is enabled, an additional poster file will be written that is compatible with the default stv.")
    private FieldProxy<Boolean> enableDefaultSTVPosterCompatibility = new FieldProxy<Boolean>(false);
    
    @AField(label="Import TV as Sage Recordings", description="When importing TV Shows, try to add them into the Sage Recordings.")
    private FieldProxy<Boolean> importTVAsRecordedShows = new FieldProxy<Boolean>(false);

    @AField(label="Backup Sage Recordings on write", description="When writing/overwriting SageTV recordings, first make a backup of the existing metadata.")
    private FieldProxy<Boolean> backupSageRecordingMetadata = new FieldProxy<Boolean>(true);
    
    public MetadataConfiguration() {
        super();
        init();
    }

    public String getPersistenceClass() {
        return persistenceClass.getString();
    }

    public void setPersistenceClass(String persistenceClass) {
        this.persistenceClass.set(persistenceClass);
    }

    public String getMediaMetadataProviders() {
        return videoMetadataProviders.getString();
    }

    public void setVideoMetadataProviders(String videoMetadataProviders) {
        this.videoMetadataProviders.set(videoMetadataProviders);
    }

    public String getWordsToClean() {
        return wordsToClean.getString();
    }

    public void setWordsToClean(String wordsToClean) {
        this.wordsToClean.set(wordsToClean);
    }

    public String getDefaultProviderId() {
        return defaultProviderId.getString();
    }

    public void setDefaultProviderId(String defaultProviderId) {
        this.defaultProviderId.set(defaultProviderId);
    }
    
    public float getGoodScoreThreshold(){
    	return goodScoreThreshold.getFloat();
    }
    
    public void setGoodScoreThreshold(float goodScoreThreshold){
    	this.goodScoreThreshold.set(goodScoreThreshold);
    }

    public boolean isScoreAlternateTitles() {
        return scoreAlternateTitles.getBoolean();
    }

    public void setScoreAlternateTitles(boolean scoreAlternateTitles) {
        this.scoreAlternateTitles.set(scoreAlternateTitles);
    }

    public int getPosterImageWidth() {
        return posterImageWidth.getInt();
    }

    public void setPosterImageWidth(int posterImageWidth) {
        this.posterImageWidth.set(posterImageWidth);
    }

    public int getBannerImageWidth() {
        return bannerImageWidth.getInt();
    }

    public void setBannerImageWidth(int bannerImageWidth) {
        this.bannerImageWidth.set(bannerImageWidth);
    }

    public int getBackgroundImageWidth() {
        return backgroundImageWidth.getInt();
    }

    public void setBackgroundImageWidth(int backgroundImageWidth) {
        this.backgroundImageWidth.set(backgroundImageWidth);
    }

    public int getMaxDownloadableImages() {
        return maxDownloadableImages.getInt();
    }

    public void setMaxDownloadableImages(int maxDownloadableImages) {
        this.maxDownloadableImages.set(maxDownloadableImages);
    }

    public boolean isEnableDefaultSTVPosterCompatibility() {
        return enableDefaultSTVPosterCompatibility.getBoolean();
    }

    public void setEnableDefaultSTVPosterCompatibility(boolean enableDefaultSTVPosterCompatibility) {
        this.enableDefaultSTVPosterCompatibility.set(enableDefaultSTVPosterCompatibility);
    }

    public boolean isImportTVAsRecordedShows() {
        return importTVAsRecordedShows.getBoolean();
    }

    public void setImportTVAsRecordedShows(boolean importTVAsRecordedShows) {
        this.importTVAsRecordedShows.set(importTVAsRecordedShows);
    }

    public boolean isBackupSageRecordingMetadata() {
        return backupSageRecordingMetadata.getBoolean();
    }

    public void setBackupSageRecordingMetadata(boolean backup) {
        this.backupSageRecordingMetadata.set(backup);
    }
}
