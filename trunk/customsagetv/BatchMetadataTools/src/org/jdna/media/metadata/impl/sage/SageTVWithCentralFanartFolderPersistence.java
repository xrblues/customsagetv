package org.jdna.media.metadata.impl.sage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.impl.imdb.IMDBUtils;
import org.jdna.util.SortedProperties;

import sagex.phoenix.fanart.FanartUtil;
import sagex.phoenix.fanart.FanartUtil.MediaArtifactType;
import sagex.phoenix.fanart.FanartUtil.MediaType;

public class SageTVWithCentralFanartFolderPersistence implements IMediaMetadataPersistence {
    private static final Logger                                   log              = Logger.getLogger(SageTVWithCentralFanartFolderPersistence.class);
    private static final SageTVWithCentralFanartFolderPersistence instance         = new SageTVWithCentralFanartFolderPersistence();
    private static final String                                   MOVIE_MEDIA_TYPE = "Movie";
    private static final String                                   TV_MEDIA_TYPE    = "TV";

    public SageTVWithCentralFanartFolderPersistence() {
        if (!SageProperty.isPropertySetValid()) {
            throw new RuntimeException("Programmer Error: SageProperty is missing some MetadataKey values!");
        }
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
    private Map<String, String> loadProperties(IMediaResource mf) throws Exception {
        File propFile = getPropertyFile(mf);
        Properties props = new Properties();
        if (propFile != null && propFile.exists() && propFile.canRead()) {
            log.debug("Loading Sage Video Metadata properties: " + propFile.getAbsolutePath());
            props.load(new FileInputStream(propFile));
        }
        
        Map<String,String> m = new HashMap<String,String>();
        for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
            String n = (String)e.nextElement();
            m.put(n, props.getProperty(n));
            log.debug("xx Keeping Property: " + n);
        }
        return m;
    }

    public Map<String, String> getMetadataProperties(IMediaResource mediaFile, IMediaMetadata md) throws IOException {
        ConfigurationManager cm = ConfigurationManager.getInstance();

        boolean overwrite = cm.getMetadataUpdaterConfiguration().isOverwrite();

        if (md.getMediaTitle() == null) throw new IOException("MetaData doesn't contain title.  Will not Save/Update.");

        if (mediaFile.getType() != IMediaFile.TYPE_FILE) {
            throw new IOException("Can only store metadata for IMedaiFile.TYPE_FILE objects.  Not a valid file: " + mediaFile.getLocationUri());
        }

        // now copy this metadata...
        Map<String, String> props;
        try {
            props = loadProperties(mediaFile);
        } catch (Exception e) {
            log.error("There was an error trying reload the existing properties.  We may lose some data.", e);
            props = new HashMap<String,String>();
        }

        // add the filename and file uri
        props.put(SageProperty.FILENAME.sageKey, mediaFile.getName());
        props.put(SageProperty.FILEURI.sageKey, mediaFile.getLocationUri());

        // set the media type, if it's not set
        if (StringUtils.isEmpty((String) md.get(MetadataKey.MEDIA_TYPE))) {
            if (StringUtils.isEmpty((String) md.get(MetadataKey.SEASON))) {
                md.set(MetadataKey.MEDIA_TYPE, MOVIE_MEDIA_TYPE);
            } else {
                md.set(MetadataKey.MEDIA_TYPE, TV_MEDIA_TYPE);
            }
        }

        // store properties...
        for (SageProperty p : SageProperty.values()) {
            if (p.metadataKey==null) continue;
            if (StringUtils.isEmpty(p.sageKey)) continue;

            // handle special cases
            if (p == SageProperty.ACTORS) {
                props.put(p.sageKey, encodeString(encodeActors(md.getCastMembers(ICastMember.ACTOR), cm.getSageMetadataConfiguration().getActorMask())));
            } else if (p == SageProperty.DIRECTORS) {
                props.put(p.sageKey, encodeString(encodeDirectors(md.getCastMembers(ICastMember.DIRECTOR))));
            } else if (p == SageProperty.WRITERS) {
                props.put(p.sageKey, encodeString(encodeWriters(md.getCastMembers(ICastMember.WRITER))));
            } else if (p == SageProperty.GENRES) {
                props.put(p.sageKey, encodeString(encodeGenres(md.getGenres())));
            } else if (p == SageProperty.SERIALIZED_GENRES) {
                props.put(p.sageKey, encodeString(serializeStrings(md.getGenres())));
            } else if (p == SageProperty.SERIALIZED_CAST) {
                props.put(p.sageKey, encodeString(serializeCast(md.getCastMembers(ICastMember.ALL))));
            } else if (p == SageProperty.FANART_BACKGROUND) {
                setFanartImageUrl(overwrite, props, md.getBackground(), p);
            } else if (p == SageProperty.FANART_BANNER) {
                setFanartImageUrl(overwrite, props, md.getBanner(), p);
            } else if (p == SageProperty.FANART_POSTER) {
                setFanartImageUrl(overwrite, props, md.getPoster(), p);
            } else if (p == SageProperty.SEASON_NUMBER) {
                props.put(p.sageKey, zeroPad(encodeString((md.get(MetadataKey.SEASON))), 2));
            } else if (p == SageProperty.EPISODE_NUMBER) {
                props.put(p.sageKey, zeroPad(encodeString((md.get(MetadataKey.EPISODE))), 2));
            } else if (p == SageProperty.DISPLAY_TITLE) {
                props.put(p.sageKey, rewriteTitle(encodeString(md.getMediaTitle())));
            } else if (p == SageProperty.PROVIDER_DATA_ID) {
                if (md.get(p.metadataKey)!=null) {
                    props.put(p.sageKey, md.get(p.metadataKey).toString());
                }
            } else if (p == SageProperty.USER_RATING) {
                props.put(p.sageKey, IMDBUtils.parseUserRating((String)md.get(p.metadataKey)));
            } else {
                // should just be normal string data
                Object o = md.get(p.metadataKey);
                if (o == null) continue;
                if (o instanceof String) {
                    if (!StringUtils.isEmpty((String) o)) {
                        props.put(p.sageKey, String.valueOf(o));
                    }
                } else {
                    log.error("Cannot Perist Metadata Key " + p.metadataKey + "; A special case is needed.");
                }
            }
        }

        // lastly encode the description, to ensure that all other props are
        // set.
        props.put(SageProperty.DESCRIPTION.sageKey, encodeDescription(md, cm.getSageMetadataConfiguration().getDescriptionMask(), props));

        // now copy this metadata...
        Map<String, String> xprops = new HashMap<String, String>();
        for (Map.Entry<String, String> me : props.entrySet()) {
            String v =me.getValue();
            if (!StringUtils.isEmpty(v)) {
                xprops.put(me.getKey(), v);
                log.debug("Adding Property: " + me.getKey() + "; value: " + me.getValue());
            } else {
                log.warn("Removing Property: " + me.getKey() + "; It has an empty value.");
            }
        }
        return props;
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, boolean overwrite) throws IOException {
        // do the actual save
        save((IMediaFile) mediaFile, md, getMetadataProperties(mediaFile, md), overwrite);
    }

    private void setFanartImageUrl(boolean overwrite, Map<String,String> props, IMediaArt fanart, SageProperty key) {
        if (overwrite || ((props.get(key.sageKey) == null || ((String)props.get(key.sageKey)).trim().length() == 0) && fanart != null && !StringUtils.isEmpty(fanart.getDownloadUrl()))) {

            if (fanart != null && !StringUtils.isEmpty(fanart.getDownloadUrl())) {
                props.put(key.sageKey, encodeString(fanart.getDownloadUrl()));
            }
        }
    }

    private String zeroPad(String encodeString, int padding) {
        try {
            int v = Integer.parseInt(encodeString);
            String format = "%0" + padding + "d";
            return String.format(format, v);
        } catch (Exception e) {
            return encodeString;
        }
    }

    private String rewriteTitle(String title) {
        log.debug("rewriting title: " + title);
        if (title == null) title = "";
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

    private void save(IMediaFile mediaFileParent, IMediaMetadata md, Map<String,String> props, boolean overwrite) {
        // in the event that this is a grouped/stacked MediaFile, we need to
        // write the metadata for each part.
        if (mediaFileParent.isStacked()) {
            int i = 1;
            for (IMediaResource mf : mediaFileParent.getParts()) {
                if (mf instanceof IMediaFile) {
                    // Update the title with the title mask before saving multi
                    // part movies
                    // store the title
                    props.put(SageProperty.DISPLAY_TITLE.sageKey, rewriteTitle(md.getMediaTitle()));
                    // set the disc # in the props
                    props.put(SageProperty.DISC.sageKey, String.valueOf(i++));
                    // update it using the mask
                    props.put(SageProperty.DISPLAY_TITLE.sageKey, MediaMetadataUtils.format(ConfigurationManager.getInstance().getSageMetadataConfiguration().getMultiCDTitleMask(), props));
                    // for multiple parts, we try tro re-use the thumbnail to
                    // reduce the amount of downloading...
                    saveSingle(props, (IMediaFile) mf, md, overwrite);
                } else {
                    log.error("Unknown Media File type for: " + mf.getLocationUri() + "; " + mf.getClass().getName());
                }
            }
        } else {
            // store the title
            props.put(SageProperty.DISPLAY_TITLE.sageKey, rewriteTitle(md.getMediaTitle()));
            // update it using the mask
            if (!StringUtils.isEmpty(props.get(SageProperty.SEASON_NUMBER.sageKey))) {
                // assume TV
                if (!StringUtils.isEmpty(props.get(SageProperty.EPISODE_NUMBER.sageKey))) {
                    props.put(SageProperty.DISPLAY_TITLE.sageKey, MediaMetadataUtils.format(ConfigurationManager.getInstance().getSageMetadataConfiguration().getTvTitleMask(), props));
                } else {
                    props.put(SageProperty.DISPLAY_TITLE.sageKey, MediaMetadataUtils.format(ConfigurationManager.getInstance().getSageMetadataConfiguration().getTvDvdTitleMask(), props));
                }
            } else {
                props.put(SageProperty.DISPLAY_TITLE.sageKey, MediaMetadataUtils.format(ConfigurationManager.getInstance().getSageMetadataConfiguration().getTitleMask(), props));
            }
            saveSingle(props, mediaFileParent, md, overwrite);
        }

        saveFanart(mediaFileParent, md, overwrite);

        mediaFileParent.touch();
    }

    private void saveFanart(IMediaFile mediaFileParent, IMediaMetadata md, boolean overwrite) {
        MediaArtifactType[] localArtTypes = null;
        if (ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().isFanartEnabled() && !StringUtils.isEmpty(ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().getFanartCentralFolder())) {
            log.info("Using Central Fanart: " + ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().getFanartCentralFolder());
            for (MediaArtifactType mt : MediaArtifactType.values()) {
                saveCentralFanart(mediaFileParent, md, mt, overwrite);
            }
            localArtTypes = new MediaArtifactType[] {MediaArtifactType.POSTER};
        } else if (ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().isFanartEnabled()) {
            log.info("Using Local Fanart; The central fanart folder is not set.");
            localArtTypes = MediaArtifactType.values();
        } else {
            log.info("Fanart is not enabled.  Saving local thumbnails only.");
            localArtTypes = new MediaArtifactType[] {MediaArtifactType.POSTER};
        }
        
        // save any local artifacts
        saveLocalFanartForTypes(mediaFileParent, md, overwrite, localArtTypes);
    }
    
    private void saveLocalFanartForTypes(IMediaFile mediaFileParent, IMediaMetadata md, boolean overwrite, MediaArtifactType[] artTypes) {
        if (mediaFileParent.isStacked()) {
            for (IMediaResource mf : mediaFileParent.getParts()) {
                for (MediaArtifactType mt : artTypes) {
                    try {
                        saveLocalFanart((IMediaFile)mf, md, mt, overwrite);
                    } catch (Exception e) {
                        log.error("Failed to save fanart: " + mt + " for file: " + mediaFileParent.getLocationUri(),e);
                    }
                }
            }
        } else {
            for (MediaArtifactType mt : artTypes) {
                try {
                    saveLocalFanart(mediaFileParent, md, mt, overwrite);
                } catch (Exception e) {
                    log.error("Failed to save fanart: " + mt + " for file: " + mediaFileParent.getLocationUri(),e);
                }
            }
        }
    }

    private void saveLocalFanart(IMediaFile mediaFileParent, IMediaMetadata md, MediaArtifactType mt, boolean overwrite) throws IOException {
        try {
            File mediaFile = new File(new URI(mediaFileParent.getLocationUri()));

            // media type is not important for local fanart
            File imageFile = FanartUtil.getLocalFanartForFile(mediaFile, MediaType.MOVIE, mt);
            IMediaArt ma[] = md.getMediaArt(mt);
            if (ma != null && ma.length > 0) {
                downloadAndSaveFanart(ma[0], imageFile, overwrite, false);
            }
        } catch (URISyntaxException e) {
            log.error("Failed to save media art: " + mt + " for file: " + mediaFileParent.getLocationUri(),e);
        }
    }

    private void downloadAndSaveFanart(IMediaArt mediaArt, File imageFile, boolean overwrite, boolean useOriginalName) throws IOException {
        if (mediaArt == null || mediaArt.getDownloadUrl() == null || imageFile == null) return;
        
        String url = mediaArt.getDownloadUrl();
        if (useOriginalName) {
            imageFile = new File(imageFile.getParentFile(), new File(url).getName());
            log.debug("Using orginal image filename from url: " + imageFile.getAbsolutePath());
        }
        
        if (!overwrite && imageFile.exists()) {
            log.debug("Skipping writing of image file: " + imageFile.getAbsolutePath() + " since overwrite is disabled.");
        } else {
            MediaMetadataUtils.writeImageFromUrl(mediaArt.getDownloadUrl(), imageFile);
        }
    }

    private void saveCentralFanart(IMediaFile mediaFileParent, IMediaMetadata md, MediaArtifactType mt, boolean overwrite) {
        String centralFolder = ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().getFanartCentralFolder();
        if (centralFolder == null) {
            throw new RuntimeException("Central Fanart Support is enabled, but no central folder location is set!");
        }
        MediaType mediaType = null;
        Map<String, String> extraMD = new HashMap<String, String>();
        if (isMovie(md)) {
            mediaType = MediaType.MOVIE;
        } else if (isTV(md)) {
            mediaType = MediaType.TV;
            extraMD.put(SageProperty.SEASON_NUMBER.sageKey, (String) md.get(MetadataKey.SEASON));
        } else if (isMusic(md)) {
            mediaType = MediaType.MUSIC;
        } else {
            log.error("Unsupported MediaFile Type: " + mediaFileParent.getLocationUri());
            return;
        }

        File fanartFile = FanartUtil.getCentralFanartArtifact(mediaType, mt, md.getMediaTitle(), centralFolder, extraMD);
        IMediaArt artwork[] = md.getMediaArt(mt);
        if (artwork != null && artwork.length > 0) {
            for (IMediaArt ma : artwork) {
                try {
                    downloadAndSaveFanart(ma, fanartFile, overwrite, true);
                } catch (IOException e) {
                    log.error("Failed to download Fanart: " + ma.getDownloadUrl() + " for file: " + mediaFileParent.getLocationUri(), e);
                }
            }
        }
    }

    private boolean isMusic(IMediaMetadata md) {
        // TODO Current Music is not supported.
        return false;
    }

    private boolean isTV(IMediaMetadata md) {
        return TV_MEDIA_TYPE.equals(md.get(MetadataKey.MEDIA_TYPE));
    }

    private boolean isMovie(IMediaMetadata md) {
        return md == null || MOVIE_MEDIA_TYPE.equals(md.get(MetadataKey.MEDIA_TYPE));
    }

    private void saveSingle(Map<String,String> allprops, IMediaFile mf, IMediaMetadata md, boolean overwrite) {
        File partFile = getPropertyFile(mf);
        if (!overwrite && partFile.exists()) {
            log.debug("Skipping overwrite of property file: " + partFile.getAbsolutePath() + " since overwrite is disabled.");
        }
        
        try {
            log.debug("Saving Sage video metadata properties: " + partFile.getAbsolutePath());

            // only store the props that we really want...
            Properties props = new SortedProperties();
            for (SageProperty p : SageProperty.values()) {
                if (p.metadataKey==null) continue;
                if (StringUtils.isEmpty(p.sageKey)) continue;
                
                if (!StringUtils.isEmpty(allprops.get(p.sageKey))) {
                    log.debug("Storing: " + p.sageKey);
                    props.setProperty(p.sageKey, allprops.get(p.sageKey));
                } else {
                    log.debug("Rejecting: " + p);
                }
            }

            props.store(new FileOutputStream(partFile), "Sage Video Metadata for " + mf.getLocationUri());

            log.debug("Touched Media File: " + mf.getLocationUri() + " so that sage to reload the metadata.");
        } catch (IOException e) {
            log.error("Failed to save properties: " + partFile.getAbsolutePath(), e);
        }
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
                part = "";
            }
            sb.append(MessageFormat.format(mask, c.getName(), part));
        }
        return sb.toString();
    }

    private static String encodeDescription(IMediaMetadata md, String mask, Map<String,String> props) {
        if (mask == null) mask = "${"+SageProperty.DESCRIPTION.sageKey+"}";
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

        if (propFile == null || propFile.exists()) {
            return null;
        }

        try {
            MediaMetadata md = new MediaMetadata();
            Map<String,String> props = loadProperties(mediaFile);

            for (SageProperty p : SageProperty.values()) {
                if (p.metadataKey == null) continue;
                if (StringUtils.isEmpty(p.sageKey)) continue;

                // handle special cases
                if (p == SageProperty.ACTORS) {
                } else if (p == SageProperty.PROVIDER_DATA_ID) {
                    md.set(p.metadataKey, new MetadataID(props.get(p.sageKey)));
                } else if (p == SageProperty.DIRECTORS) {
                } else if (p == SageProperty.WRITERS) {
                } else if (p == SageProperty.GENRES) {
                } else if (p == SageProperty.SERIALIZED_GENRES) {
                    md.setGenres(deserializeStrings(StringUtils.defaultIfEmpty(props.get(p.sageKey), props.get(SageProperty.GENRES.sageKey))));
                } else if (p == SageProperty.SERIALIZED_CAST) {
                    md.setCastMembers(deserializeCast(props.get(p.sageKey)));
                } else if (p == SageProperty.FANART_BACKGROUND) {
                    String img = props.get(p.sageKey);
                    if (img != null && img.length() > 0) {
                        MediaArt poster = new MediaArt();
                        poster.setType(MediaArtifactType.BACKGROUND);
                        poster.setDownloadUrl(img);
                        md.addMediaArt(poster);
                    }
                } else if (p == SageProperty.FANART_BANNER) {
                    String img = props.get(p.sageKey);
                    if (img != null && img.length() > 0) {
                        MediaArt poster = new MediaArt();
                        poster.setType(MediaArtifactType.BANNER);
                        poster.setDownloadUrl(img);
                        md.addMediaArt(poster);
                    }
                } else if (p == SageProperty.FANART_POSTER) {
                    String img = props.get(p.sageKey);
                    if (img != null && img.length() > 0) {
                        MediaArt poster = new MediaArt();
                        poster.setType(MediaArtifactType.POSTER);
                        poster.setDownloadUrl(img);
                        md.addMediaArt(poster);
                    }
                } else if (p == SageProperty.DISPLAY_TITLE) {
                    md.setMediaTitle(StringUtils.defaultIfEmpty(props.get(p.sageKey), props.get(SageProperty.MEDIA_TITLE.sageKey)));
                } else if (p == SageProperty.DESCRIPTION) {
                    md.setDescription(StringUtils.defaultIfEmpty(props.get(SageProperty.SERIALIZED_DESCRIPTION.sageKey), props.get(p.sageKey)));
                }

                // should just be normal string data
                Object o = md.get(p.metadataKey);
                if (o == null) continue;
                if (o instanceof String) {
                    if (!StringUtils.isEmpty((String) o)) {
                        props.put(p.sageKey, (String)o);
                        md.set(p.metadataKey, props.get(p.sageKey));
                    }
                } else {
                    log.error("Cannot Perist Metadata Key " + p.metadataKey + "; A special case is needed.");
                }
            }

            return md;
        } catch (Exception e) {
            log.error("Failed to load sage properties: " + propFile.getAbsolutePath(), e);
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

    public static Map<String, String> getSageTVMetadataMap(IMediaFile mediaFile, IMediaMetadata md) {
        try {
            return instance.getMetadataProperties(mediaFile, md);
        } catch (IOException e) {
            log.error("Unabled to get Metadata Properties for: " + mediaFile.getLocationUri(), e);
        }
        return null;
    }
}
