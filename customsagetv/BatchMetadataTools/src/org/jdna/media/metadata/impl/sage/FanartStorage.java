package org.jdna.media.metadata.impl.sage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.MediaMetadataUtils;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.util.StoredStringSet;

import sagex.phoenix.fanart.FanartUtil;
import sagex.phoenix.fanart.FanartUtil.MediaArtifactType;
import sagex.phoenix.fanart.FanartUtil.MediaType;

public class FanartStorage {
    private static final FanartStorage instance = new FanartStorage();
    private static final Logger log = Logger.getLogger(FanartStorage.class);
    
    public void saveFanart(IMediaFile mediaFileParent, String title, IMediaMetadata md, PersistenceOptions options) {
        MediaArtifactType[] localArtTypes = null;
        if (ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().isFanartEnabled() && !StringUtils.isEmpty(ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().getFanartCentralFolder())) {
            log.info("Using Central Fanart: " + ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().getFanartCentralFolder());
            for (MediaArtifactType mt : MediaArtifactType.values()) {
                saveCentralFanart(title, md, mt, options);
            }
            localArtTypes = new MediaArtifactType[] {MediaArtifactType.POSTER};
        } else if (ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().isFanartEnabled()) {
            log.info("Using Local Fanart; The central fanart folder is not set.");
            localArtTypes = MediaArtifactType.values();
        } else {
            log.info("Fanart is not enabled.  Saving local thumbnails only.");
            localArtTypes = new MediaArtifactType[] {MediaArtifactType.POSTER};
        }
        
        if (mediaFileParent!=null && ConfigurationManager.getInstance().getMetadataConfiguration().isEnableDefaultSTVPosterCompatibility()) {
            // save any local artifacts
            saveLocalFanartForTypes(mediaFileParent, md, options, localArtTypes);
        }
    }
    
    private void saveLocalFanartForTypes(IMediaFile mediaFileParent, IMediaMetadata md, PersistenceOptions options, MediaArtifactType[] artTypes) {
        if (mediaFileParent.isStacked()) {
            for (IMediaResource mf : mediaFileParent.getParts()) {
                for (MediaArtifactType mt : artTypes) {
                    try {
                        saveLocalFanart((IMediaFile)mf, md, mt, options);
                    } catch (Exception e) {
                        log.error("Failed to save fanart: " + mt + " for file: " + mediaFileParent.getLocationUri(),e);
                    }
                }
            }
        } else {
            for (MediaArtifactType mt : artTypes) {
                try {
                    saveLocalFanart(mediaFileParent, md, mt, options);
                } catch (Exception e) {
                    log.error("Failed to save fanart: " + mt + " for file: " + mediaFileParent.getLocationUri(),e);
                }
            }
        }
    }

    private void saveLocalFanart(IMediaFile mediaFileParent, IMediaMetadata md, MediaArtifactType mt, PersistenceOptions options) throws IOException {
        try {
            File mediaFile = new File(new URI(mediaFileParent.getLocationUri()));

            // media type is not important for local fanart
            File imageFile = FanartUtil.getLocalFanartForFile(mediaFile, MediaType.MOVIE, mt);
            IMediaArt ma[] = md.getMediaArt(mt);
            if (ma != null && ma.length > 0) {
                downloadAndSaveFanart(mt, ma[0], imageFile, options, false);
            }
        } catch (URISyntaxException e) {
            log.error("Failed to save media art: " + mt + " for file: " + mediaFileParent.getLocationUri(),e);
        }
    }

    private void downloadAndSaveFanart(MediaArtifactType mt, IMediaArt mediaArt, File imageFile, PersistenceOptions options, boolean useOriginalName) throws IOException {
        if (mediaArt == null || mediaArt.getDownloadUrl() == null || imageFile == null) return;
        
        String url = mediaArt.getDownloadUrl();
        if (useOriginalName) {
            imageFile = new File(imageFile.getParentFile(), new File(url).getName());
            log.debug("Using orginal image filename from url: " + imageFile.getAbsolutePath());
        }
        
        if (!shouldSkipFile(imageFile) || (options!=null && options.isOverwriteFanart())) {
            if (!imageFile.exists() || (options!=null && options.isOverwriteFanart())) {
                int scale = getScale(mt);
                if (scale>0) {
                    MediaMetadataUtils.writeImageFromUrl(mediaArt.getDownloadUrl(), imageFile, scale);
                } else {
                    MediaMetadataUtils.writeImageFromUrl(mediaArt.getDownloadUrl(), imageFile);
                }
            } else {
                log.debug("Skipping writing of image file: " + imageFile.getAbsolutePath() +" consider using --overwrite or --overwriteFanart.");
            }
        } else {
            log.debug("Skipping writing of image file: " + imageFile.getAbsolutePath() +" consider using --overwrite or --overwriteFanart or remove the image name from the image skip file");
        }
    }
    
    private int getScale(MediaArtifactType mt) {
        MetadataConfiguration mc = ConfigurationManager.getInstance().getMetadataConfiguration();
        int scale = -1;
        if (mc==null) return scale;
        if (mt == MediaArtifactType.BACKGROUND) {
            scale = mc.getBackgroundImageWidth();
        } else if (mt == MediaArtifactType.BANNER) {
            scale = mc.getBannerImageWidth();
        } else if (mt == MediaArtifactType.POSTER) {
            scale = mc.getPosterImageWidth();
        } else {
            scale = -1;
        }
        return scale;
    }

    private boolean shouldSkipFile(File imageFile) {
        boolean skip = false;
        try {
            File storedFile = new File(imageFile.getParentFile(), "images");
            StoredStringSet set = new StoredStringSet();
            String name = imageFile.getName();
            if (storedFile.exists()) {
                set.load(new FileReader(storedFile));
                if (set.contains(name)) {
                    log.debug("Skipping Image file: " + imageFile.getPath() + " because it's in the image skip file.");
                    skip=true;
                }
            }
            if (!skip) {
                log.debug("Adding Image file: " + imageFile.getPath() + " to the image skip file.");
                set.add(name);
                // create the file's parent dir if it's not exist
                if (!storedFile.getParentFile().exists()) {
                    storedFile.getParentFile().mkdirs();
                }
                FileWriter fw = new FileWriter(storedFile);
                set.store(fw, "Ignoring these image files");
                fw.flush();
                fw.close();
            }
        } catch (Exception e) {
            log.error("Failed to load/save the skip file for image: " + imageFile.getAbsolutePath(), e);
        }
        return skip;
    }

    private void saveCentralFanart(String title, IMediaMetadata md, MediaArtifactType mt, PersistenceOptions options) {
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
            if (!StringUtils.isEmpty((String) md.get(MetadataKey.SEASON))) {
                extraMD.put(SageProperty.SEASON_NUMBER.sageKey, (String) md.get(MetadataKey.SEASON));
            }
        } else if (isMusic(md)) {
            mediaType = MediaType.MUSIC;
        } else {
            log.error("Unsupported MediaFile Type: " + md.get(MetadataKey.MEDIA_TYPE));
            return;
        }

        int downloaded=0;
        int max = ConfigurationManager.getInstance().getMetadataConfiguration().getMaxDownloadableImages();
        if (max==-1) max=99;
        File fanartFile = FanartUtil.getCentralFanartArtifact(mediaType, mt, title, centralFolder, extraMD);
        IMediaArt artwork[] = md.getMediaArt(mt);
        if (artwork != null && artwork.length > 0) {
            max = Math.min(max, artwork.length);
            for (IMediaArt ma : artwork) {
                try {
                    downloadAndSaveFanart(mt, ma, fanartFile, options, true);
                } catch (IOException e) {
                    log.error("Failed to download Fanart: " + ma.getDownloadUrl() + " for : " + title, e);
                }
                downloaded++;
                if (downloaded>=max) break;
            }
        } else {
            log.warn("No " + mt + " for " + title + " in the metadata.");
        }
    }

    private boolean isMusic(IMediaMetadata md) {
        // TODO Current Music is not supported.
        return false;
    }

    private boolean isTV(IMediaMetadata md) {
        return MetadataUtil.TV_MEDIA_TYPE.equals(md.get(MetadataKey.MEDIA_TYPE));
    }

    private boolean isMovie(IMediaMetadata md) {
        return md == null || MetadataUtil.MOVIE_MEDIA_TYPE.equals(md.get(MetadataKey.MEDIA_TYPE));
    }
    
    public static void  downloadFanart(String title, IMediaMetadata md, PersistenceOptions options) {
        instance.saveFanart(null, title, md, options);
    }

    public static void  downloadFanart(IMediaMetadata md, PersistenceOptions options) {
        instance.saveFanart(null, md.getMediaTitle(), md, options);
    }

    public static void  downloadFanart(IMediaFile mediaFile, IMediaMetadata md, PersistenceOptions options) {
        instance.saveFanart(mediaFile, md.getMediaTitle(), md, options);
    }
}
