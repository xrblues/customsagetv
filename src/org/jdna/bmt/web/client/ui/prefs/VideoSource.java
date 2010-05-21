package org.jdna.bmt.web.client.ui.prefs;

import java.io.Serializable;

public class VideoSource implements Serializable {
    public static enum SourceType {RECORDING, VIDEO};
    private boolean isNew = false;
    private boolean isDeleted = false;
    private String path = null;
    private SourceType type = null;
    private boolean isDeletable=false;
    
    public VideoSource() {
    }

    public VideoSource(String path, SourceType type) {
        this.path=path;
        this.type=type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public SourceType getType() {
        return type;
    }

    public void setType(SourceType type) {
        this.type = type;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public boolean isDeletable() {
        return isDeletable;
    }

    public void setDeletable(boolean isDeletable) {
        this.isDeletable = isDeletable;
    }
}
