package org.jdna.media;

import java.io.File;
import java.net.URI;


public class FileMediaFolder extends AbstractMediaFolder {
    private static final IMediaResourceFilter mediaFilter = MovieResourceFilter.INSTANCE;
    private File file = null;
    

    protected FileMediaFolder(URI uri) {
        super(uri);
        this.file=new File(uri);
    }

    protected FileMediaFolder(File file) {
        super(file.toURI());
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

    protected void loadMembers() {
       File fs[] = file.listFiles();
       for (File f : fs) {
           IMediaResource mr = createResource(f);
           if (mediaFilter.accept(mr)) {
               addMember(mr);
           }
        }
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
            if("VIDEO_TS".equalsIgnoreCase(f.getName()) || "BDMV".equalsIgnoreCase(f.getName())) {
                // NOTE: this is added because Sage will pass the DVD as VIDEO_TS
                return new FileHDFolderMediaFile(f.getParentFile());
            } else if (FileHDFolderMediaFile.isDVD(f)) {
                return new FileHDFolderMediaFile(f);
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
