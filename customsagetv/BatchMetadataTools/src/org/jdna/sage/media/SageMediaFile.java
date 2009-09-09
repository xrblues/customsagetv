package org.jdna.sage.media;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaResource;
import org.jdna.media.IPath;
import org.jdna.media.VirtualMediaFile;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;
import org.jdna.media.util.PathUtils;

import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;
import sagex.phoenix.fanart.SageFanartUtil;
import sagex.phoenix.fanart.SimpleMediaFile;

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
        super(PathUtils.createPath(file));
        log.debug("Creating SageMediaFile from File: " + file.getAbsolutePath());
        init(MediaFileAPI.GetMediaFileForFilePath(file));
    }
    
    public SageMediaFile(int mediaFileId) {
        super(PathUtils.createPath(sageURI(mediaFileId)));

        log.debug("Creating SageMediaFile from ID: " + mediaFileId);
        init(MediaFileAPI.GetMediaFileForID(mediaFileId));
    }
    
    public SageMediaFile(Object mediaFile) {
        super(PathUtils.createPath(sageURI(0)));
        init(mediaFile);
    }
    
    protected void init(Object mediaFile) {
        if (AiringAPI.IsAiringObject(mediaFile)) {
            Object o = AiringAPI.GetMediaFileForAiring(mediaFile);
            if (o==null) {
                this.airing=mediaFile;
                setContentType(ContentType.TV);

                // Now check the alternate category, since some sage recordings are Movies
                String altCat = ShowAPI.GetShowCategory(mediaFile);
                if (altCat != null) {
                    if (altCat.equals("Movie") || altCat.equals(phoenix.api.GetProperty("alternate_movie_category"))) {
                        setContentType(ContentType.MOVIE);
                    }
                }
            } else {
                init(o);
            }
        } else if (MediaFileAPI.IsMediaFileObject(mediaFile)) {
            this.mediaFile = mediaFile;
            Object mf = MediaFileAPI.GetFileForSegment(mediaFile, 0);
            if (mf==null) {
                try {
                    log.warn("Media File does not have a File associated with it: " + mediaFile);
                    setLocationUri(PathUtils.createPath(new URI("sage:/id/" + MediaFileAPI.GetMediaFileID(mediaFile))));
                } catch (URISyntaxException e) {
                    log.error("Failed to set ID Location URI for mediafile: " + mediaFile);
                }
            } else {
                setLocationUri(PathUtils.createPath((File)mf));
            }

            if (MediaFileAPI.IsDVD(mediaFile) || MediaFileAPI.IsBluRay(mediaFile)) {
                setContentType(ContentType.HDFOLDER);
            } else if (MediaFileAPI.IsTVFile(mediaFile)) {
                setContentType(ContentType.TV);
                
                // Now check the alternate category, since some sage recordings are Movies
                String altCat = ShowAPI.GetShowCategory(mediaFile);
                if (altCat != null) {
                    if (altCat.equals("Movie") || altCat.equals(phoenix.api.GetProperty("alternate_movie_category"))) {
                        setContentType(ContentType.MOVIE);
                    }
                }
            } else if (MediaFileAPI.IsVideoFile(mediaFile)) {
                // check the filename using the FileName utils...
                SearchQuery q = SearchQueryFactory.createQuery(this.getLocation().toString());
                if (q.getType() == SearchQuery.Type.TV) {
                    setContentType(ContentType.TV);
                } else {
                    setContentType(ContentType.MOVIE);
                }
            }
        }
    }

    @Override
    public void delete() {
        if (mediaFile!=null) {
            MediaFileAPI.DeleteFileWithoutPrejudice(mediaFile);
        }
    }

    @Override
    public boolean exists() {
        if (airing!=null) return false;
        return true;
    }

    @Override
    public boolean isReadOnly() {
        if (airing!=null) return true;
        return false;
    }

    @Override
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
    
    @Override
    public void touch() {
        // TODO: Can we simply tell sage to "refersh this file"
        File f = getFile();
        if (f!=null) {
            f.setLastModified(System.currentTimeMillis());
        }
    }
    
    protected Object getInternalSageMediaObject() {
        return (mediaFile!=null)?mediaFile:airing;
    }
    
    public static Object getSageMediaFileObject(IMediaResource res) {
        if (res instanceof SageMediaFile) {
            return ((SageMediaFile)res).getInternalSageMediaObject();
        } else {
            return null;
        }
    }

    @Override
    public String getName() {
        File f = getFile();
        if (f!=null) {
            return f.getName();
        } else {
            return AiringAPI.GetAiringTitle(getSageMediaFileObject(this));
        }
    }

    @Override
    public IPath getLocation() {
        File f = getFile();
        if (f!=null) {
            return PathUtils.createPath(f);
        } else {
            return super.getLocation();
        }
    }

    @Override
    public String getTitle() {
        // Sage Handles DVD Tiles a little differently, so let the FanartUtils figure out the title for us
        if (mediaFile!=null) {
            SimpleMediaFile smf = SageFanartUtil.GetSimpleMediaFile(mediaFile);
            if (smf!=null) {
                return smf.getTitle();
            }
        }
        
        return super.getTitle();
    }
}
