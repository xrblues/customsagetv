package org.jdna.sage.media;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;

import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;

/**
 * A SageMediaFile is meant to allow a native Sage MediaFile or Airing object to represent itself as a BMT media file object.
 * This would allow for a more transparent operation when running in plugin mode.
 * 
 * @author seans
 *
 */
public class SageMediaFile implements IMediaFile {
    private static final Logger log = Logger.getLogger(SageMediaFile.class);
    
    private Object mediaFile=null;
    private Object airing=null;
    private int mediaType;

    public SageMediaFile(File file) {
        log.debug("Creating SageMediaFile from File: " + file.getAbsolutePath());
        init(MediaFileAPI.GetMediaFileForFilePath(file));
    }
    
    public SageMediaFile(String mediaFileOrId) {
        log.debug("Creating SageMediaFile from ID: " + mediaFileOrId);
        try {
            int id = Integer.parseInt(mediaFileOrId);
            init(MediaFileAPI.GetMediaFileForID(id));
        } catch (Exception e) {
            init(MediaFileAPI.GetMediaFileForFilePath(new File(mediaFileOrId)));
        }
    }
    
    public SageMediaFile(Object mediaFile) {
        log.debug("Creating SageMediaFile from Object");
        init(mediaFile);
    }
    
    protected void init(Object mediaFile) {
        if (AiringAPI.IsAiringObject(mediaFile)) {
            Object o = AiringAPI.GetMediaFileForAiring(mediaFile);
            if (o==null) {
                this.airing=mediaFile;
                mediaType = CONTENT_TYPE_TV;
            } else {
                init(o);
            }
        } else if (MediaFileAPI.IsMediaFileObject(mediaFile)) {
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
    }
    
    public List<IMediaResource> getParts() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isStacked() {
        return false;
    }

    public boolean isWatched() {
        return AiringAPI.IsWatched(getSageMediaObject());
    }

    public void setWatched(boolean watched) {
        AiringAPI.SetWatched(getSageMediaObject());
    }

    public void accept(IMediaResourceVisitor visitor) {
        visitor.visit(this);
    }

    public void copy() {
        throw new UnsupportedOperationException("copy() not supported.");
    }

    public void delete() {
        if (mediaFile!=null) {
            MediaFileAPI.DeleteFileWithoutPrejudice(mediaFile);
        }
    }

    public boolean exists() {
        if (airing!=null) return false;
        
        return true;
    }

    public int compareTo(IMediaResource o) {
        return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
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

    public String getLocationUri() {
        if (mediaFile!=null) {
            File f = getFile();
            if (f!=null) {
                return f.toURI().toString();
            } else {
                return null;
            }
        } else {
            return "airing://" + AiringAPI.GetAiringID(airing);
        }
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
        if (mediaFile!=null) {
            return MediaFileAPI.GetFileForSegment(mediaFile, 0);
        } else {
            // airings have no file
            return null;
        }
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
        if (mediaFile!=null) {
            return MediaFileAPI.GetMediaTitle(mediaFile);
        }
        if (airing!=null) {
            return AiringAPI.GetAiringTitle(airing);
        }
        return null;
    }

    public int getType() {
        return TYPE_FILE;
    }

    public boolean isReadOnly() {
        if (airing!=null) return true;
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
    
    public Object getSageMediaObject() {
        return (mediaFile!=null)?mediaFile:airing;
    }
}
