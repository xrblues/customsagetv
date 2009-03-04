package org.jdna.media.metadata.impl.sage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.htmlparser.filters.IsEqualFilter;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MediaArt;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MediaMetadataUtils;
import org.jdna.media.metadata.MetadataKey;

public class SageVideoMetaDataPersistence implements IMediaMetadataPersistence {
    private static final Logger log                = Logger.getLogger(SageVideoMetaDataPersistence.class);

    public static final String  _SER_GENRE         = "_serializedGenres";
    public static final String  _SER_CAST          = "_serializedCast";
    public static final String  _SER_TITLE         = "_serializedTitle";
    public static final String  _SER_DESCRIPTION   = "_serializedDescription";

    public static final String  _OTHER_PARTS       = "_otherParts";

    public static final String  _ASPECT_RATIO      = "_aspectRatio";
    public static final String  _RELEASE_DATE      = "_releaseDate";
    public static final String  _POSTER_URL        = "_thumbnailUrl";
    public static final String  _USER_RATING       = "_userRating";
    public static final String  _PROVIDER_ID       = "_providerId";
    public static final String  _PROVIDER_DATA_URL = "_providerDataUrl";
    public static final String  _COMPANY           = "_company";
    public static final String  _DISC              = "_disc";
    public static final String  _RATED_DESCRIPTION = "_ratedDescription";
    public static final String  _BACKDROP_URL      = "_backdropUrl";
    
    public static final String  _SEASON            = "_season";
    public static final String  _EPISODE           = "_episode";
    public static final String  _SHOW_TITLE        = "_showTitle";
    
    public static final String  WRITER             = "Writer";
    public static final String  DIRECTOR           = "Director";
    public static final String  ACTOR              = "Actor";
    public static final String  DESCRIPTION        = "Description";
    public static final String  RUNNING_TIME       = "RunningTime";
    public static final String  TITLE              = "Title";
    public static final String  YEAR               = "Year";
    public static final String  GENRE              = "Genre";
    public static final String  RATED              = "Rated";
    
    /* These properties are added and used only the for the El processing, they are removed before a save if done */
    private static final String _FILENAME = "_fileName";
    private static final String _FILEURI = "_fileUri";
    
    /* List of properties that we do not want to store */
    private static final String IGNORE_PROPS[] = new String[] {
        _FILENAME, _FILEURI
    };
    
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
        boolean overwriteThumbnail = (options & OPTION_OVERWRITE_POSTER) == OPTION_OVERWRITE_POSTER;
        boolean overwriteBackground = (options & OPTION_OVERWRITE_BACKGROUND) == OPTION_OVERWRITE_BACKGROUND;
        
        if (md.getTitle() == null) throw new IOException("MetaData doesn't contain title.  Will not Save/Update.");

        if (mediaFile.getType() != IMediaFile.TYPE_FILE) {
            throw new IOException("Can only store metadata for IMedaiFile.TYPE_FILE objects.  Not a valid file: " + mediaFile.getLocationUri());
        }

        ConfigurationManager cm = ConfigurationManager.getInstance();

        // now copy this metadata...
        Properties props;
        try {
            props = loadProperties(mediaFile);
        } catch (Exception e) {
            log.error("There was an error trying reload the existing properties.  We may lose some data.", e);
            props = new Properties();
        }

        // add the filename and file uri
        props.put(_FILENAME, mediaFile.getName());
        props.put(_FILEURI, mediaFile.getLocationUri());
        
        // Store other props and serializedProps
        props.put(_COMPANY, encodeString(md.get(MetadataKey.COMPANY)));
        props.put(_PROVIDER_DATA_URL, encodeString(md.getProviderDataUrl()));
        props.put(_PROVIDER_ID, encodeString(md.getProviderId()));
        props.put(_USER_RATING, encodeString(md.getUserRating()));

        // we only copy/update the thumbnail url IF the update thumbnail has
        // been set.
        // this prevents us from frivously updating a custom set thumbnail url
        if (overwriteThumbnail || 
                ((props.getProperty(_POSTER_URL) == null || props.getProperty(_POSTER_URL).trim().length()==0)  
                && md.getPoster() != null 
                && !StringUtils.isEmpty(md.getPoster().getDownloadUrl()))) {

            if (md.getPoster()!=null && !StringUtils.isEmpty(md.getPoster().getDownloadUrl())) {
                props.put(_POSTER_URL, encodeString(md.getPoster().getDownloadUrl()));
            }
        }
        
        // check for backdrop 
        if (overwriteBackground || 
                ((props.getProperty(_BACKDROP_URL) == null && 
                md.getBackground() != null && 
                !StringUtils.isEmpty(md.getBackground().getDownloadUrl())))) {
            
            if (md.getBackground()!=null && !StringUtils.isEmpty(md.getBackground().getDownloadUrl())) {
                props.put(_BACKDROP_URL, encodeString(md.getBackground().getDownloadUrl()));
            }
        }
        
        props.put(_RELEASE_DATE, encodeString(md.getReleaseDate()));
        props.put(_ASPECT_RATIO, encodeString(md.get(MetadataKey.ASPECT_RATIO)));

        // serialze some fields
        props.put(_SER_GENRE, encodeString(serializeStrings(md.getGenres())));
        props.put(_SER_CAST, encodeString(serializeCast(md.getCastMembers(ICastMember.ALL))));
        props.put(_SER_TITLE, encodeString(md.getTitle()));
        props.put(_SER_DESCRIPTION, encodeString(md.getDescription()));

        // Sage recognized properties
        props.put(TITLE, rewriteTitle(encodeString(md.getTitle())));
        props.put(YEAR, encodeString(md.getYear()));
        props.put(GENRE, encodeString(encodeGenres(md.getGenres())));
        props.put(RATED, encodeString(md.get(MetadataKey.MPAA_RATING)));
        props.put(_RATED_DESCRIPTION, encodeString(md.get(MetadataKey.MPAA_RATING_DESCRIPTION)));
        props.put(RUNNING_TIME, encodeString(md.getRuntime()));
        props.put(ACTOR, encodeString(encodeActors(md.getCastMembers(ICastMember.ACTOR), cm.getSageMetadataConfiguration().getActorMask())));
        props.put(WRITER, encodeString(encodeWriters(md.getCastMembers(ICastMember.WRITER))));
        props.put(DIRECTOR, encodeString(encodeDirectors(md.getCastMembers(ICastMember.DIRECTOR))));
        props.put(DESCRIPTION, encodeString(md.getDescription()));

        // tv stuff
        if (!StringUtils.isEmpty(encodeString((md.get(MetadataKey.TV_SEASON))))) {
            log.debug("Writing TV Properties ");
            props.put(_SEASON, zeroPad(encodeString((md.get(MetadataKey.TV_SEASON))),2));
            props.put(_EPISODE, zeroPad(encodeString((md.get(MetadataKey.TV_EPISODE))),2));
            props.put(_SHOW_TITLE, encodeString((md.get(MetadataKey.TV_SHOW_TITLE))));
            props.put(_DISC, encodeString((md.get(MetadataKey.DVD_DISC))));
        }
        
        // lastly encode the description, to ensure that all other props are
        // set.
        props.put(DESCRIPTION, encodeDescription(md, cm.getSageMetadataConfiguration().getDescriptionMask(), props));

        // do the actual save
        save((IMediaFile) mediaFile, md, props, options);
    }

    private Object zeroPad(String encodeString, int padding) {
        try {
            int v = Integer.parseInt(encodeString);
            String format="%0" + padding + "d";
            return String.format(format, v);
        } catch (Exception e) {
            return encodeString;
        }
    }

    private String rewriteTitle(String title) {
        if (title==null) title="";
        if (ConfigurationManager.getInstance().getSageMetadataConfiguration().isRewriteTitle()) {
            Pattern p = Pattern.compile(ConfigurationManager.getInstance().getSageMetadataConfiguration().getRewriteTitleRegex(), Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(title);
            return m.replaceFirst("$2, $1");
        } else {
            return title;
        }
    }

    private String serializeCast(ICastMember[] members) {
        if (members != null && members.length > 0) {
            String cast[] = new String[members.length];
            for (int i = 0; i < members.length; i++) {
                cast[i] = serializeCastMember(members[i]);
            }
            return serializeStrings(cast);
        } else {
            return null;
        }
    }

    private String serializeCastMember(ICastMember cm) {
        StringBuffer sb = new StringBuffer();
        // type|name|part|url
        sb.append(cm.getType()).append("|").append(cm.getName()).append("|").append(cm.getPart()).append("|").append(cm.getProviderDataUrl());
        return sb.toString();
    }

    private String serializeStrings(String[] strings) {
        if (strings == null) return null;

        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (String s : strings) {
            if (!first) sb.append(";");
            sb.append(s);
            first = false;
        }
        return sb.toString();
    }

    private void save(IMediaFile mediaFileParent, IMediaMetadata md, Properties props, long options) { 
        boolean overwriteThumbnail = (options & OPTION_OVERWRITE_POSTER) == OPTION_OVERWRITE_POSTER;
        boolean overwriteBackground = (options & OPTION_OVERWRITE_BACKGROUND) == OPTION_OVERWRITE_BACKGROUND;

        // in the event that this is a grouped/stacked MediaFile, we need to
        // write the metadata for each part.
        if (mediaFileParent.isStacked()) {
            int i = 1;
            String localThumb = null;
            for (IMediaResource mf : mediaFileParent.getParts()) {
                if (mf instanceof IMediaFile) {
                    // Update the title with the title mask before saving multi
                    // part movies
                    // store the title
                    props.setProperty(TITLE, rewriteTitle(props.getProperty(_SER_TITLE)));
                    // set the disc # in the props
                    props.setProperty(_DISC, String.valueOf(i++));
                    // update it using the mask
                    props.setProperty(TITLE, MediaMetadataUtils.format(ConfigurationManager.getInstance().getSageMetadataConfiguration().getMultiCDTitleMask(), props));
                    // for multiple parts, we try tro re-use the thumbnail to
                    // reduce the amount of downloading...
                    localThumb = save(props, (IMediaFile) mf, md, localThumb, overwriteThumbnail);
                } else {
                    log.error("Unknown Media File type for: " + mf.getLocationUri() + "; " + mf.getClass().getName());
                }
            }
        } else {
            // store the title
            props.setProperty(TITLE, rewriteTitle(props.getProperty(_SER_TITLE)));
            // update it using the mask
            if (!StringUtils.isEmpty(props.getProperty(_SEASON))) {
                // assume TV
            	if (!StringUtils.isEmpty(props.getProperty(_EPISODE))) {
            		props.setProperty(TITLE, MediaMetadataUtils.format(ConfigurationManager.getInstance().getSageMetadataConfiguration().getTvTitleMask(), props));
            	} else {
            		props.setProperty(TITLE, MediaMetadataUtils.format(ConfigurationManager.getInstance().getSageMetadataConfiguration().getTvDvdTitleMask(), props));
            	}
            } else {
                props.setProperty(TITLE, MediaMetadataUtils.format(ConfigurationManager.getInstance().getSageMetadataConfiguration().getTitleMask(), props));
            }
            save(props, mediaFileParent, md, null, overwriteThumbnail);
        }
        
        if (!ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().isIgnoreBackdrops()) {
            // now lets deal with the backdrop file
            IMediaArt backdrop = md.getBackground();
            if (backdrop!=null) {
                File backdropFile = getBackdropFile(mediaFileParent, props);
                if (overwriteBackground || !backdropFile.exists()) {
                    log.debug("Downloading backdrop for: " + mediaFileParent.getLocationUri());
                    try {
                        MediaMetadataUtils.writeImageFromUrl(backdrop.getDownloadUrl(), backdropFile);
                    } catch (IOException e) {
                        log.error("Failed to download backdrop: "+ backdrop.getDownloadUrl() +" for resource: " + mediaFileParent.getLocationUri(),e);
                    }
                }
            }
        }
    }

    private String save(Properties props, IMediaFile mf, IMediaMetadata md, String localThumbFile, boolean overwriteThumbnail) {
        File partFile = getPropertyFile(mf);
        try {
            log.debug("Saving Sage video metadata properties: " + partFile.getAbsolutePath() + "; Overwrite Thumbnail: " + overwriteThumbnail);

            // update local values for this instance
            File thumbFile = getThumbnailFile(mf);
            
            // remove props that we don't want to store
            for (String k : IGNORE_PROPS) {
                props.remove(k);
            }
            props.store(new FileOutputStream(partFile), "Sage Video Metadata for " + mf.getLocationUri());

            // now download and save the thumbnail, if it does not exist
            thumbFile = getThumbnailFile(mf);
            if (!thumbFile.exists() || overwriteThumbnail) {
                try {
                    if (localThumbFile == null) {
                        IMediaArt ma = md.getPoster();
                        if (ma != null) {
                            localThumbFile = ma.getDownloadUrl();
                        }
                    }
                    int scale = ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().getPosterImageWidth();
                    if (scale==-1) { 
                        MediaMetadataUtils.writeImageFromUrl(localThumbFile, thumbFile);
                    } else {
                        MediaMetadataUtils.writeImageFromUrl(localThumbFile, thumbFile, scale);
                    }
                    log.debug("Stored Thumbanil: " + thumbFile.getAbsolutePath() + " from " + localThumbFile);

                    // next time, use the local file for the thumbnail url,
                    // saves
                    // excessive downloading for multipart cds
                    localThumbFile = thumbFile.toURI().toURL().toExternalForm();
                } catch (Exception e) {
                    log.error("Failed to save/download thumbnail: " + localThumbFile + " to: " + thumbFile.getAbsolutePath() + "; But the rest of the property data has been saved.", e);
                }
            }

            // update the file data/time on the mediafile
            mf.touch();
            log.debug("Touched Media File: " + mf.getLocationUri() + " so that sage to reload the metadata.");
        } catch (IOException e) {
            log.error("Failed to save properties: " + partFile.getAbsolutePath(), e);
        }

        return localThumbFile;
    }

    private static String encodeString(Object s) {
        if (s == null) return "";
        if (s instanceof String) {
            return ((String) s).trim();
        } else {
            return s.toString();
        }
    }

    private static String encodeWriters(ICastMember[] writers) {
        if (writers == null) return "";
        StringBuffer sb = new StringBuffer();
        for (ICastMember c : writers) {
            sb.append(c.getName()).append(";");
        }
        return sb.toString();
    }

    private static String encodeDirectors(ICastMember[] directors) {
        if (directors == null) return "";

        StringBuffer sb = new StringBuffer();
        for (ICastMember c : directors) {
            sb.append(c.getName()).append(";");
        }
        return sb.toString();
    }

    private static String encodeActors(ICastMember[] actors, String mask) {
        if (actors == null) return "";

        if (mask == null) mask = "{0} -- {1};";

        StringBuffer sb = new StringBuffer();
        for (ICastMember c : actors) {
            String part = c.getPart();
            if (StringUtils.isEmpty(part)) {
                part="";
            }
            sb.append(MessageFormat.format(mask, c.getName(), part));
        }
        return sb.toString();
    }

    private static String encodeDescription(IMediaMetadata md, String mask, Properties props) {
        if (mask == null) mask = "${Description}";
        return MediaMetadataUtils.format(mask, props);
    }

    private static String encodeGenres(String[] genres) {
        if (genres == null) return "";

        int genreLevels = ConfigurationManager.getInstance().getSageMetadataConfiguration().getGenreLevels();
        if (genreLevels == -1) genreLevels = genres.length;
        int max = Math.min(genres.length, genreLevels);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < max; i++) {
            if (i > 0) sb.append("/");
            sb.append(genres[i]);
        }

        return sb.toString();
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
                md.set(MetadataKey.TV_SEASON, props.getProperty(_SEASON));
                md.set(MetadataKey.TV_EPISODE, props.getProperty(_EPISODE));
                md.set(MetadataKey.TV_SHOW_TITLE, props.getProperty(_SHOW_TITLE));
                
                String img = props.getProperty(_POSTER_URL);
                if (img!=null && img.length()>0) {
                    MediaArt poster = new MediaArt();
                    poster.setType(IMediaArt.POSTER);
                    poster.setDownloadUrl(img);
                    md.addMediaArt(poster);
                }
                
                img = props.getProperty(_BACKDROP_URL);
                if (img!=null && img.length()>0) {
                    MediaArt poster = new MediaArt();
                    poster.setType(IMediaArt.BACKGROUND);
                    poster.setDownloadUrl(img);
                    md.addMediaArt(poster);
                }
                
                md.setTitle(props.getProperty(_SER_TITLE, props.getProperty(TITLE)));
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

    public File getBackdropFile(IMediaFile mediaFile, Properties props) {
        try {
            String backdropLocation =ConfigurationManager.getInstance().getSageMetadataConfiguration().getAlternateBackdropLocation();
            String safeRegex =ConfigurationManager.getInstance().getSageMetadataConfiguration().getSafeTitleReplaceRegex();
           
            if (backdropLocation != null && backdropLocation != "") {
                //get the title that Sage will use and remove any characters that are invalid in a filename
                String safeTitle = props.getProperty(TITLE).replaceAll(safeRegex, "");

                //check that the root backdrop folder exists, if it doesn't create it (this shouldn't ever happen, but let's be safe)
                File rootBackdrops = new File(backdropLocation);
                if(!rootBackdrops.exists())
                    rootBackdrops.mkdirs();
               
                //since we're only dealing with movies (for now), check that the movies folder exists, and create it if it doesn't
                File movieBackdrops = new File(rootBackdrops.getAbsolutePath() + File.separator + "Movies");
                if(!movieBackdrops.exists())
                    movieBackdrops.mkdirs();
               
                return new File(movieBackdrops.getAbsolutePath() + File.separator + safeTitle + ".jpg");
            } else {
                return new File(new URI(mediaFile.getLocalBackdropUri()));
            }
        } catch (URISyntaxException e) {
            log.error("Failed to get local backdrop file to media file!", e);
            return null;
        }
    }

    public static Map<String, String> metadataToSageTVMap(IMediaMetadata md) {
        Map<String, String> props = new HashMap<String, String>();
        // Sage recognized properties
        props.put(TITLE, encodeString(md.getTitle()));
        props.put(YEAR, encodeString(md.getYear()));
        props.put(GENRE, encodeString(encodeGenres(md.getGenres())));
        props.put(RATED, encodeString(md.get(MetadataKey.MPAA_RATING)));
        props.put(RUNNING_TIME, encodeString(md.getRuntime()));
        props.put(ACTOR, encodeString(encodeActors(md.getCastMembers(ICastMember.ACTOR), ConfigurationManager.getInstance().getSageMetadataConfiguration().getActorMask())));
        props.put(WRITER, encodeString(encodeWriters(md.getCastMembers(ICastMember.WRITER))));
        props.put(DIRECTOR, encodeString(encodeDirectors(md.getCastMembers(ICastMember.DIRECTOR))));
        props.put(DESCRIPTION, encodeString(md.getDescription()));
        return props;
    }
}
