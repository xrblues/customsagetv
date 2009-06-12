package org.jdna.bmt.web.client.ui.filechooser;

import java.io.Serializable;

public class JSFile implements Serializable {
    private boolean isDir = false;
    private String path = null;
    private String name = null;
    
    public JSFile() {
    }
    
    public JSFile(String path, String name, boolean isDirectory) {
        this.path=path;
        this.name =name;
        this.isDir=isDirectory;
    }
    
    public boolean isDirectory() {
        return isDir;
    }
    
    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }
}
