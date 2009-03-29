package org.jdna.sage.media;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.metadata.IMediaMetadata;

import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;

/**
 * A SageMediaFile is meant to allow a native Sage MediaFile object to represent itself as a BMT media file object.
 * This would allow for a more transparent operation when running in plugin mode.
 * 
 * @author seans
 *
 */
public class SageMediaFile implements IMediaFile {
    private static final Logger log = Logger.getLogger(SageMediaFile.class);
    
    private Object mediaFile;
    private int mediaType;

    public SageMediaFile(File file) {
        init(MediaFileAPI.GetMediaFileForFilePath(file));
    }
    
    public SageMediaFile(String mediaFileOrId) {
        try {
            int id = Integer.parseInt(mediaFileOrId);
            init(MediaFileAPI.GetMediaFileForID(id));
        } catch (Exception e) {
            init(MediaFileAPI.GetMediaFileForFilePath(new File(mediaFileOrId)));
        }
    }
    
    public SageMediaFile(Object mediaFile) {
        init(mediaFile);
    }
    
    protected void init(Object mediaFile) {
        this.mediaFile = mediaFile;
        if (MediaFileAPI.IsDVD(mediaFile)) {
            mediaType = CONTENT_TYPE_DVD;
        } else if (MediaFileAPI.IsTVFile(mediaFile)) {
            mediaType = CONTENT_TYPE_TV;
        } else if (MediaFileAPI.IsVideoFile(mediaFile)) {
            mediaType = CONTENT_TYPE_MOVIE;
        } else {
            mediaType = CONTENT_TYPE_UNKNOWN;
        }
    }
    
    public List<IMediaResource> getParts() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isStacked() {
        return false;
    }

    public boolean isWatched() {
        return AiringAPI.IsWatched(mediaFile);
    }

    public void setWatched(boolean watched) {
        AiringAPI.SetWatched(mediaFile);
    }

    public void accept(IMediaResourceVisitor visitor) {
        visitor.visit(this);
    }

    public void copy() {
        throw new UnsupportedOperationException("copy() not supported.");
    }

    public void delete() {
        MediaFileAPI.DeleteFileWithoutPrejudice(mediaFile);
    }

    public boolean exists() {
        return true;
    }

    public int compareTo(IMediaResource o) {
        return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
    }

    public String getExtension() {
        String name = getName();
        if (name == null) return null;
        int p = name.lastIndexOf('.');
        if (p != -1) {
            return name.substring(p + 1);
        } else {
            return null;
        }
    }

    public String getBasename() {
        String name = getName();
        if (name == null) return null;
        int p = name.lastIndexOf('.');
        if (p != -1) {
            return name.substring(0, p);
        } else {
            return name;
        }
    }

    public int getContentType() {
        return mediaType;
    }

    public String getLocalBackdropUri() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLocalMetadataUri() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLocalPosterUri() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLocalSubtitlesUri() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLocationUri() {
        File f = getFile();
        if (f!=null) {
            return f.toURI().toString();
        } else {
            return null;
        }
    }

    public IMediaMetadata getMetadata() {
        // TODO We need to create a Metadata from a SageAiring/Show/MediaFile object
        return null;
    }

    public String getName() {
        File f = getFile();
        if (f!=null) {
            return f.getName();
        } else {
            return null;
        }
    }
    
    private File getFile() {
        return MediaFileAPI.GetFileForSegment(mediaFile, 0);
    }

    public IMediaResource getParent() {
        // TODO: If parent is not set, then create a SageParentFolder from the file itself.
        return null;
    }

    public String getPath() {
        File f = getFile();
        if (f!=null) {
            return f.getAbsolutePath();
        } else {
            return null;
        }
    }

    public String getRelativePath(IMediaResource res) {
        throw new UnsupportedOperationException("GetRelativePath() not supported.");
    }

    public String getTitle() {
        return MediaFileAPI.GetMediaTitle(mediaFile);
    }

    public int getType() {
        return TYPE_FILE;
    }

    public boolean isReadOnly() {
        return false;
    }

    public long lastModified() {
        File f = getFile();
        if (f!=null) {
            return f.lastModified();
        } else {
            return 0;
        }
    }

    public void touch() {
        // TODO: Can we simply tell sage to "refersh this file"
        File f = getFile();
        if (f!=null) {
            f.setLastModified(System.currentTimeMillis());
        }
    }

    /**
     * UpdateMetadata will only download fanart based on the fanart rules.  It will not actually update the 
     * Sage Metadata, since it can't do it anyways.
     */
    public void updateMetadata(IMediaMetadata metadata, boolean overwrite) throws IOException {
        log.warn("updateMetadata() not implemented yet.");
    }
}
