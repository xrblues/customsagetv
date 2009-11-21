package org.jdna.bmt.web.client.ui.filechooser;

import java.io.Serializable;

public class JSFile implements Serializable {
    private boolean isDir = false;
    private String path = null;
    private String name = null;
    private boolean isRoot = false;
    
    public JSFile() {
    }
    
    public JSFile(String path, String name, boolean isDirectory) {
        this.path=path;
        this.name =name;
        this.isDir=isDirectory;
        if (name==null || name.trim().length()==0) {
            this.name=path;
        }
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

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }
}
