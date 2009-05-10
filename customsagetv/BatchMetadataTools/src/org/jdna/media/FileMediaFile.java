package org.jdna.media;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class FileMediaFile extends AbstractMediaFile implements IMediaFile {
    private File file = null;
    
    public FileMediaFile(File file) {
        super(file.toURI());
        this.file=file;
    }
    
    public FileMediaFile(URI uri) {
        super(uri);
        this.file = new File(uri);
    }

    public void delete() {
        file.delete();
    }

    public boolean exists() {
        return file.exists();
    }

    public IMediaResource getParent() {
        return new FileMediaFile(file.getParentFile());
    }

    public Type getType() {
        return Type.File;
    }

    public boolean isReadOnly() {
        return !file.canWrite();
    }

    public long lastModified() {
        return file.lastModified();
    }

    public void touch() {
        if (file.exists()) {
            file.setLastModified(System.currentTimeMillis());
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
            }
        }
    }
    
    protected File getFile() {
        return file;
    }
}
