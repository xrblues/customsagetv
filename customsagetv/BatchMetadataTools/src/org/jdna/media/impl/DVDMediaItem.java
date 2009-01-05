package org.jdna.media.impl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.MediaMetadataFactory;

import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;

/**
 * DVD Media Item is a special item that makes a DVD appear as a single item.
 * 
 * @author seans
 * 
 */
public class DVDMediaItem extends AbstractMediaResource implements IMediaFile {
    private static boolean deepDVDScanning   = true;
    private static Pattern dvdFileExtPattern = Pattern.compile("\\.vob$|\\.ifo$|\\.bup$", Pattern.CASE_INSENSITIVE);
    private Object         sageMediaFile;
    private Object         sageAiring;
    private IMediaMetadata metadata;

    public static boolean isDVD(URIAdapter uri) {
        if (uri.isDirectory()) {
            URIAdapter ua = uri.createUriAdapter("VIDEO_TS");
            if (ua.exists() && ua.isDirectory()) {
                return true;
            }

            if (deepDVDScanning) {
                URI files[] = uri.listMembers();
                if (files == null) {
                    return false;
                }

                for (URI u : files) {
                    Matcher m = dvdFileExtPattern.matcher(u.toASCIIString());
                    if (m.find()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public DVDMediaItem(URIAdapter uri) {
        super(uri, IMediaFile.TYPE_FILE, IMediaResource.CONTENT_TYPE_DVD);
    }

    public DVDMediaItem(URI uri) {
        super(uri, IMediaFile.TYPE_FILE, IMediaResource.CONTENT_TYPE_DVD);
    }

    public DVDMediaItem(String uri) throws URISyntaxException {
        super(uri, IMediaFile.TYPE_FILE, IMediaResource.CONTENT_TYPE_DVD);
    }

    public List<IMediaResource> getParts() {
        return null;
    }

    public boolean isStacked() {
        return false;
    }

    @Override
    public void copy() {
        // TODO Auto-generated method stub
        super.copy();
    }

    @Override
    public void delete() {
        // TODO Auto-generated method stub
        super.delete();
    }

    @Override
    public String getLocalMetadataUri() {
        URIAdapter ua = URIAdapterFactory.getAdapter(getURIAdapter().getParentUri());
        return ua.createUriAdapter(getName() + ".properties").getUri().toString();
    }

    @Override
    public String getLocalSubtitlesUri() {
        // TODO Auto-generated method stub
        return super.getLocalSubtitlesUri();
    }

    /**
     * For regular files, returns basefilename + .jpg ext. For DVD Folders, it
     * will return VIDEO_TS/folder.jpg if VIDEO_TS dir exists, or just
     * basedirname + .jpg
     * 
     * @param mediaFile
     * @return
     */
    @Override
    public String getLocalPosterUri() {
        boolean useTSVIDEO = ConfigurationManager.getInstance().getMediaConfiguration().isUseTSFolderForThumbnail();
        URIAdapter tsdir = getURIAdapter().createUriAdapter("VIDEO_TS");
        if (tsdir.exists() && useTSVIDEO) {
            return tsdir.createUriAdapter("folder.jpg").toURIString();
        } else {
            URIAdapter ua = URIAdapterFactory.getAdapter(getURIAdapter().getParentUri());
            return ua.createUriAdapter(getName() + ".jpg").toURIString();
        }
    }
    
    

    @Override
    public String getLocalBackdropUri() {
        boolean useTSVIDEO = ConfigurationManager.getInstance().getMediaConfiguration().isUseTSFolderForThumbnail();
        URIAdapter tsdir = getURIAdapter().createUriAdapter("VIDEO_TS");
        if (tsdir.exists() && useTSVIDEO) {
            return tsdir.createUriAdapter("background.jpg").toURIString();
        } else {
            return getURIAdapter().createUriAdapter("background.jpg").toURIString();
        }
    }

    /**
     * No way to tell if a dvd is watched
     */
    public boolean isWatched() {
        if (!isStacked()) {
            return AiringAPI.IsWatched(getSageAiring());
        } else {
            boolean watched = false;
            for (IMediaResource r : getParts()) {
                if (r instanceof MediaFile) {
                    watched = watched && AiringAPI.IsWatched(((MediaFile) r).getSageAiring());
                }
            }
            return watched;
        }
    }

    /**
     * SetWatched not supported on DVD (sage doesn't track this)
     */
    public void setWatched(boolean watched) {
        if (!isStacked()) {
            System.out.println("Setting Watched for non stacked item: " + getLocationUri() + ";  " + watched);
            if (watched) {
                AiringAPI.SetWatched(getSageAiring());
            } else {
                AiringAPI.ClearWatched(getSageAiring());
            }
        } else {
            for (IMediaResource r : getParts()) {
                if (r instanceof MediaFile) {
                    if (watched) {
                        AiringAPI.SetWatched(((MediaFile) r).getSageAiring());
                    } else {
                        AiringAPI.ClearWatched(((MediaFile) r).getSageAiring());
                    }
                }
            }
        }
    }

    protected Object getSageMediaFile() {
        if (sageMediaFile == null) {
            File f = getResourceAsFile();
            File tsFile = new File(f, "VIDEO_TS");
            if (tsFile.exists() && tsFile.isDirectory()) {
                f = tsFile;
            }
            sageMediaFile = MediaFileAPI.GetMediaFileForFilePath(f);
            if (sageMediaFile == null) {
                System.out.println("Unabled to get Sage MediaFile for paths; " + getLocationUri() + "; " + f.getAbsolutePath());
            } else {
                System.out.println("Is MediaFile: " + MediaFileAPI.IsLibraryFile(sageMediaFile) + "; for " + f.getAbsolutePath());
            }
        }
        return sageMediaFile;
    }

    protected Object getSageAiring() {
        if (sageAiring == null) {
            sageAiring = MediaFileAPI.GetMediaFileAiring(getSageMediaFile());
            if (sageAiring == null) {
                System.out.println("Unable to find a Sage Airing for: " + getLocationUri());
            } else {
                System.out.println("Found Sage Airing for: " + getLocationUri());
            }
        }

        return sageAiring;
    }

    @Override
    public IMediaMetadata getMetadata() {
        if (metadata == null) {
            metadata = MediaMetadataFactory.getInstance().getDefaultPeristence().loadMetaData(this);
        }
        return metadata;
    }

}
