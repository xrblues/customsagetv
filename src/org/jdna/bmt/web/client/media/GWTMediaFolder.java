package org.jdna.bmt.web.client.media;

import java.io.Serializable;

public class GWTMediaFolder extends GWTMediaResource implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient GWTMediaResource[] children;
    private boolean allowActions = true;
    
    public GWTMediaFolder() {
        super();
    }

    public GWTMediaFolder(GWTMediaFolder parent, String title) {
        super(parent, title);
    }

    public GWTMediaResource[] getChildren() {
        return children;
    }

    public void setChildren(GWTMediaResource[] children) {
        this.children = children;
        if ( children!=null) {
            for (GWTMediaResource r : children) {
                r.setParent(this);
            }
        }
    }

    /**
     * @return the allowActions
     */
    public boolean isAllowActions() {
        return allowActions;
    }

    /**
     * @param allowActions the allowActions to set
     */
    public void setAllowActions(boolean allowActions) {
        this.allowActions = allowActions;
    }
}
