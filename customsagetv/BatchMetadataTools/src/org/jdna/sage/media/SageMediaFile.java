package org.jdna.sage.media;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.jdna.media.VirtualMediaFile;

import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;

public class SageMediaFile extends VirtualMediaFile {
    private static final Logger log = Logger.getLogger(SageMediaFile.class);
    
    private Object mediaFile=null;
    private Object airing=null;

    private static URI sageURI(int id) {
        try {
            return new URI("sage://id/" + id);
        } catch (URISyntaxException e) {
            log.error("Failed to create sage uri for: " + id, e);
        }
        return null;
    }
    
    public SageMediaFile(File file) {
        this(MediaFileAPI.GetMediaFileForFilePath(file));
    }
    
    public SageMediaFile(int mediaFileId) {
        super(sageURI(mediaFileId));

        log.debug("Creating SageMediaFile from ID: " + mediaFileId);
        init(MediaFileAPI.GetMediaFileForID(mediaFileId));
    }
    
    public SageMediaFile(Object mediaFile) {
        this(MediaFileAPI.GetMediaFileID(mediaFile));
    }
    
    protected void init(Object mediaFile) {
        if (AiringAPI.IsAiringObject(mediaFile)) {
            Object o = AiringAPI.GetMediaFileForAiring(mediaFile);
            if (o==null) {
                this.airing=mediaFile;
                setContentType(ContentType.TV);
            } else {
                init(o);
            }
        } else if (MediaFileAPI.IsMediaFileObject(mediaFile)) {
            this.mediaFile = mediaFile;
            if (MediaFileAPI.IsDVD(mediaFile) || MediaFileAPI.IsBluRay(mediaFile)) {
                setContentType(ContentType.HDFOLDER);
            } else if (MediaFileAPI.IsTVFile(mediaFile)) {
                setContentType(ContentType.TV);
            } else if (MediaFileAPI.IsVideoFile(mediaFile)) {
                setContentType(ContentType.MOVIE);
            } else {
                setContentType(ContentType.UNKNOWN);
            }
            setLocationUri(MediaFileAPI.GetFileForSegment(mediaFile, 0).toURI());
        }
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

    public boolean isReadOnly() {
        if (airing!=null) return true;
        return false;
    }

    public long lastModified() {
        if (mediaFile!=null) {
            File f = getFile();
            if (f!=null) {
                return f.lastModified();
            }
        }
            
        return 0l;
    }

    private File getFile() {
        if (mediaFile!=null) {
            return MediaFileAPI.GetFileForSegment(mediaFile, 0);
        } else {
            // airings have no file
            return null;
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

    public String getName() {
        File f = getFile();
        if (f!=null) {
            return f.getName();
        } else {
            return AiringAPI.GetAiringTitle(getSageMediaObject());
        }
    }
}
