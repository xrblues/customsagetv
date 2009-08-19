package org.jdna.media.metadata.impl.sage;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.StackedMediaFile;
import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.MediaArt;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MediaMetadataUtils;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.impl.imdb.IMDBUtils;
import org.jdna.media.util.PathUtils;
import org.jdna.util.PropertiesUtils;
import org.jdna.util.Singleton;
import org.jdna.util.SortedProperties;

import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.fanart.FanartUtil;
import sagex.phoenix.fanart.MediaArtifactType;

public class SageTVPropertiesPersistence implements IMediaMetadataPersistence {
    private static final Logger                                   log              = Logger.getLogger(SageTVPropertiesPersistence.class);

    private SageMetadataConfiguration cfg = new SageMetadataConfiguration();
    private MetadataConfiguration metadataCfg = new MetadataConfiguration();
    
    public SageTVPropertiesPersistence() {
        if (!SageProperty.isPropertySetValid()) {
            throw new RuntimeException("Programmer Error: SageProperty is missing some MetadataKey values!");
        }
    }

    public String getDescription() {
        return "Export/Import in SageTV properties format";
    }

    public String getId() {
        return "sageProperties";
    }

    private File getPropertyFile(IMediaResource mediaFile) {
        File f = PathUtils.toFile(mediaFile.getLocation());
        return FanartUtil.resolvePropertiesFile(f);
    }

    /**
     * We only load the metadata for the first part of a MediaFile.
     */
    @SuppressWarnings("unchecked")
    private Map<String, String> loadProperties(IMediaResource mf) throws Exception {
        File propFile = getPropertyFile(mf);
        Properties props = new Properties();
        if (propFile != null && propFile.exists() && propFile.canRead()) {
            log.debug("Loading Sage Video Metadata properties: " + propFile.getAbsolutePath());
            PropertiesUtils.load(props, propFile);
        }
        
        Map<String,String> m = new HashMap<String,String>();
        for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
            String n = (String)e.nextElement();
            m.put(n, props.getProperty(n));
        }
        return m;
    }

    public Map<String, String> getMetadataProperties(IMediaResource mediaFile, IMediaMetadata md, PersistenceOptions options) throws IOException {
        if (MetadataAPI.getMediaTitle(md) == null) throw new IOException("MetaData doesn't contain title.  Will not Save/Update.");

        if (mediaFile.getType() != IMediaResource.Type.File) {
            throw new IOException("Can only store metadata for IMedaiFile.TYPE_FILE objects.  Not a valid file: " + mediaFile.getLocation());
        }
        
        // set the media type, if it's not set
        MetadataUtil.updateMetadataMediaType(md);

        Map<String, String> props;
        try {
            props = loadProperties(mediaFile);
        } catch (Exception e) {
            log.error("There was an error trying reload the existing properties.  We may lose some data.", e);
            props = new HashMap<String,String>();
        }

        // add the filename and file uri
        props.put(SageProperty.FILENAME.sageKey, mediaFile.getName());
        props.put(SageProperty.FILEURI.sageKey, mediaFile.getLocation().toString());
        
        props = getMetadataProperties(md, props, options);
        return props;
    }
    
    
    public Map<String, String> getMetadataProperties(IMediaMetadata md, Map<String, String> props, PersistenceOptions options) throws IOException {
        // set the media type, if it's not set
        MetadataUtil.updateMetadataMediaType(md);

        // store properties...
        for (SageProperty p : SageProperty.values()) {
            if (p.metadataKey==null) continue;
            if (StringUtils.isEmpty(p.sageKey)) continue;

            // handle special cases
            if (p == SageProperty.ACTORS) {
                props.put(p.sageKey, encodeString(encodeActors(MetadataAPI.getCastMembers(md, ICastMember.ACTOR), cfg.getActorMask())));
            } else if (p == SageProperty.DIRECTORS) {
                props.put(p.sageKey, encodeString(encodeDirectors(MetadataAPI.getCastMembers(md,ICastMember.DIRECTOR))));
            } else if (p == SageProperty.WRITERS) {
                props.put(p.sageKey, encodeString(encodeWriters(MetadataAPI.getCastMembers(md,ICastMember.WRITER))));
            } else if (p == SageProperty.GENRES) {
                props.put(p.sageKey, encodeString(encodeGenres(MetadataAPI.getGenres(md))));
            } else if (p == SageProperty.SERIALIZED_GENRES) {
                props.put(p.sageKey, encodeString(serializeStrings(MetadataAPI.getGenres(md))));
            } else if (p == SageProperty.SERIALIZED_CAST) {
                props.put(p.sageKey, encodeString(serializeCast(MetadataAPI.getCastMembers(md,ICastMember.ALL))));
            } else if (p == SageProperty.SERIALIZED_FANART) {
                props.put(p.sageKey, encodeFanart(MetadataAPI.getMediaArt(md)));
            } else if (p == SageProperty.SEASON_NUMBER) {
                props.put(p.sageKey, encodeNumberString(md.getString(MetadataKey.SEASON)));
            } else if (p == SageProperty.EPISODE_NUMBER) {
                props.put(p.sageKey, encodeNumberString(md.getString(MetadataKey.EPISODE)));
            } else if (p == SageProperty.DISPLAY_TITLE) {
                props.put(p.sageKey, rewriteTitle(encodeString(MetadataAPI.getMediaTitle(md))));
            } else if (p == SageProperty.PROVIDER_DATA_ID) {
                if (md.getString(p.metadataKey)!=null) {
                    props.put(p.sageKey, md.getString(p.metadataKey));
                }
            } else if (p == SageProperty.USER_RATING) {
                props.put(p.sageKey, IMDBUtils.parseUserRating(md.getString(p.metadataKey)));
            } else {
                // should just be normal string data
                String o = md.getString(p.metadataKey);
                if (o == null) continue;
                if (!StringUtils.isEmpty((String) o)) {
                    props.put(p.sageKey, String.valueOf(o));
                }
            }
        }

        // lastly encode the description, to ensure that all other props are
        // set.
        props.put(SageProperty.DESCRIPTION.sageKey, MetadataAPI.getDescription(md));
        
        // now copy this metadata...
        Map<String, String> xprops = new HashMap<String, String>();
        for (Map.Entry<String, String> me : props.entrySet()) {
            String v =me.getValue();
            if (!StringUtils.isEmpty(v)) {
                xprops.put(me.getKey(), v);
            }
        }
        return props;
    }


    private String encodeNumberString(Object s) {
        if (s == null) return "";
        if (s instanceof String) {
            String num = ((String) s).trim();
            int n = NumberUtils.toInt(num);
            if (n>0) {
                return String.valueOf(n);
            } else {
                return "";
            }
        } else {
            return s.toString();
        }
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        MetadataAPI.normalizeMetadata((IMediaFile)mediaFile, md);
        // do the actual save
        save((IMediaFile) mediaFile, md, getMetadataProperties(mediaFile, md, options), options);
    }

    private void setFanartImageUrl(PersistenceOptions options, Map<String,String> props, IMediaArt fanart, SageProperty key) {
        if ((options!=null && options.isOverwriteFanart()) || ((props.get(key.sageKey) == null || (props.get(key.sageKey)).trim().length() == 0) && fanart != null && !StringUtils.isEmpty(fanart.getDownloadUrl()))) {

            if (fanart != null && !StringUtils.isEmpty(fanart.getDownloadUrl())) {
                props.put(key.sageKey, encodeString(fanart.getDownloadUrl()));
            }
        }
    }

    private static String zeroPad(String encodeString, int padding) {
        try {
            int v = Integer.parseInt(encodeString);
            String format = "%0" + padding + "d";
            return String.format(format, v);
        } catch (Exception e) {
            return encodeString;
        }
    }

    private static String rewriteTitle(String title) {
        log.debug("rewriting title: " + title);
        if (title == null) title = "";
        if (GroupProxy.get(SageMetadataConfiguration.class).isRewriteTitle()) {
            Pattern p = Pattern.compile(GroupProxy.get(SageMetadataConfiguration.class).getRewriteTitleRegex(), Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(title);
            return m.replaceFirst("$2, $1");
        } else {
            return title;
        }
    }

    private String serializeCast(List<ICastMember> members) {
        if (members != null && members.size() > 0) {
            List<String> cast = new ArrayList();
            for (int i = 0; i < members.size(); i++) {
                cast.add(serializeCastMember(members.get(i)));
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

    private String encodeFanart(List<IMediaArt> mediaArt) {
        log.debug("TODO: Implement Serialized Fanart");
        return null;
    }

    private String serializeStrings(List<String> strings) {
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

    private void save(IMediaFile mediaFileParent, IMediaMetadata md, Map<String,String> props, PersistenceOptions options) {
        // in the event that this is a grouped/stacked MediaFile, we need to
        // write the metadata for each part.
        if (mediaFileParent instanceof StackedMediaFile) {
            int i = 1;
            for (IMediaResource mf : ((StackedMediaFile)mediaFileParent).getStackedFiles()) {
                // Update the title with the title mask before saving multi
                // part movies
                // store the title
                props.put(SageProperty.DISPLAY_TITLE.sageKey, rewriteTitle(MetadataAPI.getMediaTitle(md)));
                // set the disc # in the props
                props.put(SageProperty.DISC.sageKey, String.valueOf(i++));
                // update it using the mask
                props.put(SageProperty.DISPLAY_TITLE.sageKey, MediaMetadataUtils.format(cfg.getMultiCDTitleMask(), props));
                // for multiple parts, we try tro re-use the thumbnail to
                // reduce the amount of downloading...
                saveSingle(props, (IMediaFile) mf, md, options);
            }
        } else {
            // store the title
            props.put(SageProperty.DISPLAY_TITLE.sageKey, rewriteTitle(MetadataAPI.getMediaTitle(md)));
            // update it using the mask
            if (MetadataAPI.isValidSeason(md)) {
                // assume TV
                if (MetadataAPI.isValidEpisode(md)) {
                    // cough hack - need to format the season and episode so that it look liks 01, 02, etc.
                    Map mod = new HashMap(props);
                    mod.put(SageProperty.SEASON_NUMBER.sageKey, zeroPad(props.get(SageProperty.SEASON_NUMBER.sageKey), 2));
                    mod.put(SageProperty.EPISODE_NUMBER.sageKey, zeroPad(props.get(SageProperty.EPISODE_NUMBER.sageKey), 2));
                    if (metadataCfg.isImportTVAsRecordedShows()) {
                        props.put(SageProperty.DISPLAY_TITLE.sageKey, MediaMetadataUtils.format(cfg.getSageTVTitleMask(), mod));
                    } else {
                        props.put(SageProperty.DISPLAY_TITLE.sageKey, MediaMetadataUtils.format(cfg.getTvTitleMask(), mod));
                    }
                } else {
                    Map mod = new HashMap(props);
                    mod.put(SageProperty.SEASON_NUMBER.sageKey, zeroPad(props.get(SageProperty.SEASON_NUMBER.sageKey), 2));
                    mod.put(SageProperty.DISC.sageKey, zeroPad(props.get(SageProperty.DISC.sageKey), 2));
                    props.put(SageProperty.DISPLAY_TITLE.sageKey, MediaMetadataUtils.format(cfg.getTvDvdTitleMask(), mod));
                }
            } else {
                if (MetadataAPI.isValidDisc(md)) {
                    props.put(SageProperty.DISPLAY_TITLE.sageKey, MediaMetadataUtils.format(cfg.getMultiCDTitleMask(), props));
                } else {
                    props.put(SageProperty.DISPLAY_TITLE.sageKey, MediaMetadataUtils.format(cfg.getTitleMask(), props));
                }
            }
            saveSingle(props, mediaFileParent, md, options);
        }
    }


    private void saveSingle(Map<String,String> allprops, IMediaFile mf, IMediaMetadata md, PersistenceOptions options) {
        File partFile = getPropertyFile(mf);
        if (!(options!=null && options.isOverwriteMetadata()) && partFile.exists()) {
            log.debug("Skipping overwrite of property file: " + partFile.getAbsolutePath() + " since overwrite is disabled.");
            return;
        }
        
        try {
            log.debug("Saving Sage video metadata properties: " + partFile.getAbsolutePath());

            // only store the props that we really want...
            Properties props = new SortedProperties();
            for (SageProperty p : SageProperty.values()) {
                if (p.metadataKey==null) continue;
                if (StringUtils.isEmpty(p.sageKey)) continue;
                
                if (!StringUtils.isEmpty(allprops.get(p.sageKey))) {
                    props.setProperty(p.sageKey, allprops.get(p.sageKey));
                }
            }

            PropertiesUtils.store(props, partFile, "Generator: Batch Metadata Tools ("+ bmt.api.GetVersion() + "); MediaFile: " + mf.getLocation());
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

    private static String encodeWriters(List<ICastMember> writers) {
        if (writers == null) return "";
        StringBuffer sb = new StringBuffer();
        for (ICastMember c : writers) {
            sb.append(c.getName()).append(";");
        }
        return sb.toString();
    }

    private static String encodeDirectors(List<ICastMember> directors) {
        if (directors == null) return "";

        StringBuffer sb = new StringBuffer();
        for (ICastMember c : directors) {
            sb.append(c.getName()).append(";");
        }
        return sb.toString();
    }

    private static String encodeActors(List<ICastMember> actors, String mask) {
        if (actors == null) return "";

        if (actors != null) {
            log.debug("Encoding Actors: " + actors.size());
        }
        
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

    private static String encodeGenres(List<String> genres) {
        if (genres == null) return "";

        int genreLevels = GroupProxy.get(SageMetadataConfiguration.class).getGenreLevels();
        if (genreLevels == -1) genreLevels = genres.size();
        int max = Math.min(genres.size(), genreLevels);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < max; i++) {
            if (i > 0) sb.append("/");
            sb.append(genres.get(i));
        }

        return sb.toString();
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        File propFile = getPropertyFile(mediaFile);

        if (propFile == null || !propFile.exists()) {
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
                    md.setString(p.metadataKey, props.get(p.sageKey));
                } else if (p == SageProperty.DIRECTORS) {
                } else if (p == SageProperty.WRITERS) {
                } else if (p == SageProperty.GENRES) {
                } else if (p == SageProperty.SERIALIZED_GENRES) {
                    md.getGenres().addAll(deserializeStrings(StringUtils.defaultIfEmpty(props.get(p.sageKey), props.get(SageProperty.GENRES.sageKey))));
                } else if (p == SageProperty.SERIALIZED_CAST) {
                    md.getCastMembers().addAll(deserializeCast(props.get(p.sageKey)));
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
                    md.setDescription(props.get(p.sageKey));
                } else {
                    md.set(p.metadataKey, props.get(p.sageKey));
                    log.debug("Setting: " + p.metadataKey + "; Value: " + props.get(p.sageKey));
                }
            }

            return md;
        } catch (Exception e) {
            log.error("Failed to load sage properties: " + propFile.getAbsolutePath(), e);
            return null;
        }
    }

    private List<String> deserializeStrings(String s) {
        if (s == null) return Collections.EMPTY_LIST;
        String[] ss=  s.split(";");
        if (ss==null) {
            return Collections.EMPTY_LIST;
        } else {
            return Arrays.asList(ss);
        }
    }

    private List<CastMember> deserializeCast(String s) {
        if (s == null) return Collections.EMPTY_LIST;
        try {
            List<String> m = deserializeStrings(s);
            List<CastMember> all = new ArrayList<CastMember>();
            for (int i = 0; i < m.size(); i++) {
                all.add(deserializeCastMember(m.get(i)));
            }

            return all;
        } catch (Exception e) {
            log.error("Failed to deserialize the string: " + s, e);
            return Collections.EMPTY_LIST;
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

    public static Map<String, String> getSageTVMetadataMap(IMediaMetadata md) {
        Map<String, String> props = new HashMap<String, String>();
        try {
            PersistenceOptions options =new PersistenceOptions();
            options.setOverwriteFanart(true);
            options.setOverwriteMetadata(true);
            return Singleton.get(SageTVPropertiesPersistence.class).getMetadataProperties(md, props, options);
        } catch (IOException e) {
            log.error("Unable to load metadata for: " + MetadataAPI.getMediaTitle(md), e);
        }
        return null;
    }
    
    public static Map<String, String> getSageTVMetadataMap(IMediaFile mediaFile, IMediaMetadata md) {
        try {
            PersistenceOptions options =new PersistenceOptions();
            options.setOverwriteFanart(true);
            options.setOverwriteMetadata(true);
            return Singleton.get(SageTVPropertiesPersistence.class).getMetadataProperties(mediaFile, md, options);
        } catch (IOException e) {
            log.error("Unabled to get Metadata Properties for: " + mediaFile.getLocation(), e);
        }
        return null;
    }
}
