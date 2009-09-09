package org.jdna.media;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import sagex.phoenix.fanart.FanartUtil;


public class FileMediaFolder extends AbstractMediaFolder {
    private IMediaResourceFilter mediaFilter = null;
    private File file = null;
    

    protected FileMediaFolder(IPath uri) {
        super(uri);
        try {
            this.file=new File(new URI(uri.toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    protected FileMediaFolder(File file) {
        super(new Path(file.toURI().toString()));
        this.file=file;
    }
    
    @Override
    protected void deleteFolder() {
        file.delete();
    }

    @Override
    protected void touchFolder() {
        file.setLastModified(System.currentTimeMillis());
    }

    public IMediaResource getResource(String path) {
        return new FileMediaFolder(new File(file, path));
    }
    
    protected IMediaResource newResource(File f) {
        return createResource(f);
    }

    @Override
    protected void loadMembers() {
       File fs[] = file.listFiles();
       for (File f : fs) {
           IMediaResource mr = createResource(f);
           if (getMediaFilter().accept(mr)) {
               addMember(mr);
           }
        }
    }
    
    private IMediaResourceFilter getMediaFilter() {
        if (mediaFilter==null) {
            mediaFilter = new MovieResourceFilter();
        }
        return mediaFilter;
    }

    public boolean exists() {
        return file.exists();
    }

    public IMediaResource getParent() {
        return new FileMediaFolder(file.getParentFile());
    }

    public boolean isReadOnly() {
        return !file.canWrite();
    }

    public long lastModified() {
        return file.lastModified();
    }

    public static IMediaResource createResource(File f) {
        if (f.isDirectory()) {
            if (FanartUtil.isDVDFolder(f)) {
                // NOTE: this is added because Sage will pass the DVD as VIDEO_TS
                return new FileHDFolderMediaFile(FanartUtil.resolveMediaFile(f));
            } else if (FileHDFolderMediaFile.isDVD(f)) {
                return new FileHDFolderMediaFile(FanartUtil.resolveMediaFile(f));
            } else {
                return new FileMediaFolder(f);
            }
        } else {
            return new FileMediaFile(f);
        }
    }
    
    public static IMediaResource createResource(URI uri) {
        return createResource(new File(uri));
    }
    
    protected File getFile() {
        return file;
    }
}
