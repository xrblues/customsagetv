package org.jdna.bmt.web.client.ui.browser;

public class MediaFolder extends MediaResource {
    private transient MediaResource[] children;
    
    public MediaFolder() {
        super();
    }

    public MediaFolder(MediaFolder parent, String title) {
        super(parent, title);
    }

    public MediaResource[] getChildren() {
        return children;
    }

    public void setChildren(MediaResource[] children) {
        this.children = children;
        if ( children!=null) {
            for (MediaResource r : children) {
                r.setParent(this);
            }
        }
    }
}
