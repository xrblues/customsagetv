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
	
	public int indexOf(GWTMediaResource res) {
		if (children==null || res==null || res.getPath()==null) return -1;
		for (int i=0;i<children.size();i++) {
			if (res.getPath().equals(children.get(i).getPath())) return i;
		}
		return -1;
	}
	
	public GWTMediaResource next(GWTMediaResource res) {
		int i=indexOf(res);
		if (i>=0) {
			if (i+1 < getLoaded()) {
				return children.get(i+1);
			}
		}
		return null;
	}

	public GWTMediaResource previous(GWTMediaResource res) {
		int i=indexOf(res);
		if ((i-1)>=0) {
			return children.get(i-1);
		}
		return null;
	}
}
