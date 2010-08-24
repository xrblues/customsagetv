package org.jdna.bmt.web.client.media;

import java.io.Serializable;
import java.util.ArrayList;

public class GWTMediaFolder extends GWTMediaResource implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient ArrayList<GWTMediaResource> children=null;
    private boolean allowActions = true;
    
    private int loaded=0;
    private int size=0;
    private int pageSize=0;
    
    public GWTMediaFolder() {
        super();
    }

    public GWTMediaFolder(GWTMediaFolder parent, String title, int size) {
        super(parent, title);
        this.size=size;
    }

    public ArrayList<GWTMediaResource> getChildren() {
        return children;
    }

    public void addChildren(GWTMediaResource[] children) {
    	if (this.children==null) this.children=new ArrayList<GWTMediaResource>();
        if ( children!=null) {
            for (GWTMediaResource r : children) {
                r.setParent(this);
                this.children.add(r);
                loaded++;
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

	public int getSize() {
		return size;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setLoaded(int loaded) {
		this.loaded = loaded;
	}

	public int getLoaded() {
		return loaded;
	}
	
	public boolean isFullyLoaded() {
		return loaded>=size;
	}
	
	public boolean isLoaded(int start, int size) {
		if (isFullyLoaded()) return true;
		return loaded>=(start+size);
	}

	public void setPageSize(int pageSize) {
		this.pageSize=pageSize;
	}
}
