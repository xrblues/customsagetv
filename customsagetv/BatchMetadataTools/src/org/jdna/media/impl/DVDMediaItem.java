package org.jdna.media.impl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;

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

    public static boolean isDVD(URIAdapter uri) {
        if (uri.isDirectory()) {
            URIAdapter ua = uri.createUriAdapter("VIDEO_TS");
            if (ua.exists() && ua.isDirectory()) {
                return true;
            }
            
            // check for bluray
            ua = uri.createUriAdapter("BDMV");
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

    // need to override the dvd for basename to simply return the name, since a dvd folder does not
    // have an extension.
    public String getBasename() {
        return getName();
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
}
