package org.jdna.media.metadata.impl.sage;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.StackedMediaFile;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.MediaMetadataUtils;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.metadataupdater.MetadataUpdaterConfiguration;
import org.jdna.util.StoredStringSet;

import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.fanart.FanartUtil;
import sagex.phoenix.fanart.MediaArtifactType;
import sagex.phoenix.fanart.MediaType;

public class FanartStorage {
    private static final FanartStorage instance = new FanartStorage();
    private static final Logger        log      = Logger.getLogger(FanartStorage.class);
    
    private MetadataUpdaterConfiguration updaterConfig = GroupProxy.get(MetadataUpdaterConfiguration.class);
    private MetadataConfiguration metadataConfig = GroupProxy.get(MetadataConfiguration.class);

    public void saveFanart(IMediaFile mediaFileParent, String title, IMediaMetadata md, PersistenceOptions options) {
        MediaArtifactType[] localArtTypes = null;
        boolean localFanart = false;
        if (updaterConfig.isFanartEnabled() && !StringUtils.isEmpty(updaterConfig.getFanartCentralFolder())) {
            log.info("Using Central Fanart: " + updaterConfig.getFanartCentralFolder());
            for (MediaArtifactType mt : MediaArtifactType.values()) {
                saveCentralFanart(title, md, mt, options);
            }
            localArtTypes = new MediaArtifactType[] { MediaArtifactType.POSTER };
            localFanart = metadataConfig.isEnableDefaultSTVPosterCompatibility();
        } else if (updaterConfig.isFanartEnabled()) {
            log.info("Using Local Fanart; The central fanart folder is not set.");
            localArtTypes = MediaArtifactType.values();
            localFanart = true;
        } else {
            log.info("Fanart is not enabled.  Saving local thumbnails only.");
            localArtTypes = new MediaArtifactType[] { MediaArtifactType.POSTER };
            localFanart = true;
        }

        if (mediaFileParent != null && localFanart) {
            // save any local artifacts
            saveLocalFanartForTypes(mediaFileParent, md, options, localArtTypes);
        }
    }

    private void saveLocalFanartForTypes(IMediaFile mediaFileParent, IMediaMetadata md, PersistenceOptions options, MediaArtifactType[] artTypes) {
        if (mediaFileParent instanceof StackedMediaFile) {
            for (IMediaResource mf : ((StackedMediaFile) mediaFileParent).getStackedFiles()) {
                for (MediaArtifactType mt : artTypes) {
                    try {
                        saveLocalFanart((IMediaFile) mf, md, mt, options);
                    } catch (Exception e) {
                        log.error("Failed to save fanart: " + mt + " for file: " + mediaFileParent.getLocationUri(), e);
                    }
                }
            }
        } else {
            for (MediaArtifactType mt : artTypes) {
                try {
                    saveLocalFanart(mediaFileParent, md, mt, options);
                } catch (Exception e) {
                    log.error("Failed to save fanart: " + mt + " for file: " + mediaFileParent.getLocationUri(), e);
                }
            }
        }
    }

    private void saveLocalFanart(IMediaFile mediaFileParent, IMediaMetadata md, MediaArtifactType mt, PersistenceOptions options) throws IOException {
        File mediaFile = new File(mediaFileParent.getLocationUri());

        // media type is not important for local fanart
        File imageFile = FanartUtil.getLocalFanartForFile(mediaFile, MediaType.MOVIE, mt);
        IMediaArt ma[] = MetadataAPI.getMediaArt(md,mt);
        if (ma != null && ma.length > 0) {
            downloadAndSaveFanart(mt, ma[0], imageFile, options, false);
        }
    }

    private void downloadAndSaveFanart(MediaArtifactType mt, IMediaArt mediaArt, File imageFile, PersistenceOptions options, boolean useOriginalName) throws IOException {
        if (mediaArt == null || mediaArt.getDownloadUrl() == null || imageFile == null) return;

        String url = mediaArt.getDownloadUrl();
        if (useOriginalName) {
            imageFile = new File(imageFile.getParentFile(), new File(url).getName());
            log.debug("Using orginal image filename from url: " + imageFile.getAbsolutePath());
        }

        if (!shouldSkipFile(imageFile) || (options != null && options.isOverwriteFanart())) {
            if (!imageFile.exists() || (options != null && options.isOverwriteFanart())) {
                int scale = getScale(mt);
                if (scale > 0) {
                    MediaMetadataUtils.writeImageFromUrl(mediaArt.getDownloadUrl(), imageFile, scale);
                } else {
                    MediaMetadataUtils.writeImageFromUrl(mediaArt.getDownloadUrl(), imageFile);
                }
            } else {
                log.debug("Skipping writing of image file: " + imageFile.getAbsolutePath() + " consider using --overwrite or --overwriteFanart.");
            }
        } else {
            log.debug("Skipping writing of image file: " + imageFile.getAbsolutePath() + " consider using --overwrite or --overwriteFanart or remove the image name from the image skip file");
        }
    }

    private int getScale(MediaArtifactType mt) {
        MetadataConfiguration mc = metadataConfig;
        int scale = -1;
        if (mc == null) return scale;
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
        // only do this if we are using the central fanart
        if ((updaterConfig.isFanartEnabled() && !StringUtils.isEmpty(updaterConfig.getFanartCentralFolder()))) {
            try {
                File storedFile = new File(imageFile.getParentFile(), "images");
                StoredStringSet set = new StoredStringSet();
                String name = imageFile.getName();
                if (storedFile.exists()) {
                    StoredStringSet.load(set, storedFile);
                    if (set.contains(name)) {
                        log.debug("Skipping Image file: " + imageFile.getPath() + " because it's in the image skip file.");
                        skip = true;
                    }
                }
                if (!skip) {
                    log.debug("Adding Image file: " + imageFile.getPath() + " to the image skip file.");
                    set.add(name);
                    // create the file's parent dir if it's not exist
                    if (!storedFile.getParentFile().exists()) {
                        storedFile.getParentFile().mkdirs();
                    }
                    StoredStringSet.save(set, storedFile, "Ignoring these image files");
                }
            } catch (Exception e) {
                log.error("Failed to load/save the skip file for image: " + imageFile.getAbsolutePath(), e);
            }
        }
        return skip;
    }

    private void saveCentralFanart(String title, IMediaMetadata md, MediaArtifactType mt, PersistenceOptions options) {
        String centralFolder = updaterConfig.getFanartCentralFolder();
        if (centralFolder == null) {
            throw new RuntimeException("Central Fanart Support is enabled, but no central folder location is set!");
        }

        if (isTV(md)) {
            sageCentralFanartForTV(title, md, mt, options, centralFolder);
        } else {
            MediaType mediaType = null;
            Map<String, String> extraMD = new HashMap<String, String>();
            if (isMovie(md)) {
                mediaType = MediaType.MOVIE;
            } else if (isMusic(md)) {
                mediaType = MediaType.MUSIC;
            } else {
                log.error("Unsupported MediaFile Type: " + md.get(MetadataKey.MEDIA_TYPE));
                return;
            }

            File fanartFile = FanartUtil.getCentralFanartArtifact(mediaType, mt, title, centralFolder, extraMD);
            IMediaArt artwork[] = MetadataAPI.getMediaArt(md, mt);
            downloadAndSageCentralFanart(mt, fanartFile, artwork, options);
        }
    }

    private void downloadAndSageCentralFanart(MediaArtifactType mt, File fanartDir, IMediaArt artwork[], PersistenceOptions options) {
        int downloaded = 0;
        int max = metadataConfig.getMaxDownloadableImages();
        if (max == -1) max = 99;
        if (artwork != null && artwork.length > 0) {
            max = Math.min(max, artwork.length);
            for (IMediaArt ma : artwork) {
                try {
                    downloadAndSaveFanart(mt, ma, fanartDir, options, true);
                } catch (IOException e) {
                    log.error("Failed to download Fanart: " + ma.getDownloadUrl() + " for : " + fanartDir.getAbsolutePath(), e);
                }
                downloaded++;
                if (downloaded >= max) break;
            }
        } else {
            log.warn("No " + mt + " for " + fanartDir.getAbsolutePath() + " in the metadata.");
        }
    }

    private void sageCentralFanartForTV(String title, IMediaMetadata md, MediaArtifactType mt, PersistenceOptions options, String centralFolder) {
        // TODO: Change this so that if we are doing TV Fanart, then we call
        // sageTVCentralFanart();
        // TV Central fanart would download series based fanart, then season
        // based fanart

        // do the series level artwork
        Map<String, String> emptyMap = Collections.emptyMap();
        File fanartFile = FanartUtil.getCentralFanartArtifact(MediaType.TV, mt, title, centralFolder, emptyMap);
        IMediaArt artwork[] = MetadataAPI.getMediaArt(md,mt);
        if (artwork == null || artwork.length == 0) {
            log.debug("No TV " + mt.name() + " MediaArt for " + MetadataAPI.getMediaTitle(md));
            return;
        }
        List<IMediaArt> l = new LinkedList<IMediaArt>();
        for (IMediaArt ma : artwork) {
            if (ma.getSeason() == 0) {
                l.add(ma);
            }
        }
        if (l.size() == 0) {
            log.debug("No Series Level TV " + mt.name() + " MediaArt for " + MetadataAPI.getMediaTitle(md));
        } else {
            downloadAndSageCentralFanart(mt, fanartFile, l.toArray(new IMediaArt[l.size()]), options);
        }

        // do the season level artwork
        Map<String, String> extraMD = new HashMap<String, String>();
        if (!StringUtils.isEmpty((String) md.get(MetadataKey.SEASON))) {
            extraMD.put(SageProperty.SEASON_NUMBER.sageKey, (String) md.get(MetadataKey.SEASON));
            fanartFile = FanartUtil.getCentralFanartArtifact(MediaType.TV, mt, title, centralFolder, extraMD);
            artwork = MetadataAPI.getMediaArt(md, mt);
            if (artwork == null || artwork.length == 0) {
                log.debug("No TV " + mt.name() + " MediaArt for " + MetadataAPI.getMediaTitle(md));
                return;
            }

            l = new LinkedList<IMediaArt>();
            for (IMediaArt ma : artwork) {
                if (ma.getSeason() > 0) {
                    l.add(ma);
                }
            }
            if (l.size() == 0) {
                log.debug("No Season Level TV " + mt.name() + " MediaArt for " + MetadataAPI.getMediaTitle(md));
            } else {
                downloadAndSageCentralFanart(mt, fanartFile, l.toArray(new IMediaArt[l.size()]), options);
            }
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

    public static void downloadFanart(String title, IMediaMetadata md, PersistenceOptions options) {
        instance.saveFanart(null, title, md, options);
    }

    public static void downloadFanart(IMediaMetadata md, PersistenceOptions options) {
        instance.saveFanart(null, MetadataAPI.getMediaTitle(md), md, options);
    }

    public static void downloadFanart(IMediaFile mediaFile, IMediaMetadata md, PersistenceOptions options) {
        instance.saveFanart(mediaFile, MetadataAPI.getMediaTitle(md), md, options);
    }
}