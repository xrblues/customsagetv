package org.jdna.media.metadata.impl.sage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MediaArt;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MetadataKey;

import sagex.phoenix.fanart.FanartUtil.MediaArtifactType;

public class SageVideoMetaDataPersistence implements IMediaMetadataPersistence {
    private static final Logger log                = Logger.getLogger(SageVideoMetaDataPersistence.class);

    private static final String  _SER_GENRE         = "_serializedGenres";
    private static final String  _SER_CAST          = "_serializedCast";
    private static final String  _SER_TITLE         = "_serializedTitle";
    private static final String  _SER_DESCRIPTION   = "_serializedDescription";


    private static final String  _ASPECT_RATIO      = "_aspectRatio";
    private static final String  _RELEASE_DATE      = "_releaseDate";
    private static final String  _POSTER_URL        = "_thumbnailUrl";
    private static final String  _USER_RATING       = "_userRating";
    private static final String  _PROVIDER_ID       = "_providerId";
    private static final String  _PROVIDER_DATA_URL = "_providerDataUrl";
    private static final String  _COMPANY           = "_company";
    private static final String  _BACKDROP_URL      = "_backdropUrl";
    
    private static final String  _SEASON            = "_season";
    private static final String  _EPISODE           = "_episode";
    private static final String  _SHOW_TITLE        = "_showTitle";
    
    private static final String  DESCRIPTION        = "Description";
    private static final String  RUNNING_TIME       = "RunningTime";
    private static final String  TITLE              = "Title";
    private static final String  YEAR               = "Year";
    private static final String  GENRE              = "Genre";
    private static final String  RATED              = "Rated";
    
    public SageVideoMetaDataPersistence() {
    }

    public String getDescription() {
        return "Export/Import in SageTV properties format";
    }

    public String getId() {
        return "sage";
    }

    private File getPropertyFile(IMediaResource mediaFile) {
        try {
            return new File(new URI(mediaFile.getLocalMetadataUri()));
        } catch (URISyntaxException e) {
            log.error("Failed to create File Uri!", e);
            return null;
        }
    }

    /**
     * We only load the metadata for the first part of a MediaFile.
     */
    private Properties loadProperties(IMediaResource mf) throws Exception {
        File propFile = getPropertyFile(mf);
        Properties props = new Properties();
        if (propFile != null && propFile.exists() && propFile.canRead()) {
            log.debug("Loading Sage Video Metadata properties: " + propFile.getAbsolutePath());
            props.load(new FileInputStream(propFile));
        }
        return props;
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, long options) throws IOException {
        throw new IOException("This Peristence provider does not save!");
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        File propFile = getPropertyFile(mediaFile);

        if (propFile != null && propFile.exists()) {
            try {
                MediaMetadata md = new MediaMetadata();
                log.debug("Loading Sage Video Metadata properties: " + propFile.getAbsolutePath());
                Properties props = loadProperties(mediaFile);
                md.setCastMembers(deserializeCast(props.getProperty(_SER_CAST)));
                md.setAspectRatio(props.getProperty(_ASPECT_RATIO));
                md.setCompany(props.getProperty(_COMPANY));
                md.setGenres(deserializeStrings(props.getProperty(_SER_GENRE, props.getProperty(GENRE))));
                md.setMPAARating(props.getProperty(RATED));
                md.setDescription(props.getProperty(_SER_DESCRIPTION, props.getProperty(DESCRIPTION)));
                md.setProviderDataUrl(props.getProperty(_PROVIDER_DATA_URL));
                md.setProviderId(props.getProperty(_PROVIDER_ID));
                md.setReleaseDate(props.getProperty(_RELEASE_DATE));
                md.setRuntime(props.getProperty(RUNNING_TIME));

                // some tv stuff
                md.set(MetadataKey.SEASON, props.getProperty(_SEASON));
                md.set(MetadataKey.EPISODE, props.getProperty(_EPISODE));
                md.set(MetadataKey.MEDIA_TITLE, props.getProperty(_SHOW_TITLE));
                
                String img = props.getProperty(_POSTER_URL);
                if (img!=null && img.length()>0) {
                    MediaArt poster = new MediaArt();
                    poster.setType(MediaArtifactType.POSTER);
                    poster.setDownloadUrl(img);
                    md.addMediaArt(poster);
                }
                
                img = props.getProperty(_BACKDROP_URL);
                if (img!=null && img.length()>0) {
                    MediaArt poster = new MediaArt();
                    poster.setType(MediaArtifactType.BACKGROUND);
                    poster.setDownloadUrl(img);
                    md.addMediaArt(poster);
                }
                
                md.setMediaTitle(props.getProperty(_SER_TITLE, props.getProperty(TITLE)));
                md.setUserRating(props.getProperty(_USER_RATING));
                md.setYear(props.getProperty(YEAR));
                return md;
            } catch (Exception e) {
                log.error("Failed to load sage properties: " + propFile.getAbsolutePath(), e);
                return null;
            }
        } else {
            log.info("No Metadata for file: " + propFile.getAbsolutePath());
            return null;
        }
    }

    private String[] deserializeStrings(String s) {
        if (s == null) return null;
        return s.split(";");
    }

    private CastMember[] deserializeCast(String s) {
        if (s == null) return null;
        try {
            String m[] = deserializeStrings(s);
            CastMember all[] = new CastMember[m.length];
            for (int i = 0; i < m.length; i++) {
                all[i] = deserializeCastMember(m[i]);
            }

            return all;
        } catch (Exception e) {
            log.error("Failed to deserialize the string: " + s, e);
            return null;
        }
    }

    private CastMember deserializeCastMember(String cmStr) {
        CastMember cm = new CastMember();

        String flds[] = cmStr.split("\\|");
        try {
            // type|name|part|url
            cm.setType(Integer.parseInt(flds[0]));
            cm.setName(flds[1]);
            cm.setPart(flds[2]);
            cm.setProviderDataUrl(flds[3]);

        } catch (Throwable e) {
            log.warn("Failed to parse cast member from string: " + cmStr);
        }

        return cm;
    }

    public File getThumbnailFile(IMediaFile mediaFile) {
        try {
            return new File(new URI(mediaFile.getLocalPosterUri()));
        } catch (URISyntaxException e) {
            log.error("Failed to get local thumbnail file to media file!", e);
            return null;
        }
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, boolean overwrite) throws IOException {
    }
}
