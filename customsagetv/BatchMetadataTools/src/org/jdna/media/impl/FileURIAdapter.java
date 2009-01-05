package org.jdna.media.impl;

import java.io.File;
import java.net.URI;

import org.apache.log4j.Logger;

public class FileURIAdapter extends URIAdapter {
    private static final Logger log = Logger.getLogger(FileURIAdapter.class);

    private File                file;

    public FileURIAdapter(URI uri) {
        super(uri);
        file = new File(uri);
    }

    @Override
    public void touch() {
        try {
            if (file.exists()) {
                file.setLastModified(System.currentTimeMillis());
            } else {
                file.createNewFile();
            }
        } catch (Exception e) {
            log.error("File Touch Failed for: " + getUri().toString());
        }
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public boolean canWrite() {
        return file.canWrite();
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public long lastModified() {
        return file.lastModified();
    }

    @Override
    public URI getParentUri() {
        return file.getParentFile().toURI();
    }

    @Override
    public URIAdapter createUriAdapter(String name) {
        return URIAdapterFactory.getAdapter(new File(new File(this.getUri()), name).toURI());
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public URI[] listMembers() {
        if (isDirectory()) {
            File files[] = file.listFiles();
            if (files != null) {
                URI u[] = new URI[files.length];
                int i = 0;
                for (File f : files) {
                    u[i++] = f.toURI();
                }
                return u;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
