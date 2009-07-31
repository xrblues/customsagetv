package org.jdna.media;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class FileMediaFile extends AbstractMediaFile implements IMediaFile {
    private File file = null;
    
    public FileMediaFile(File file) {
        super(new Path(file.toURI().toString()));
        this.file=file;
    }
    
    public FileMediaFile(IPath uri) {
        super(uri);
        try {
            this.file = new File(new URI(uri.toURI()));
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    @Override
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
