package org.jdna.media.impl;

import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;

public abstract class AbstractMediaResource implements IMediaResource {
	protected IMediaFolder parent;
	
	private String title = null;
	
	public AbstractMediaResource(IMediaFolder parent) {
		this.parent = parent;
		
	}
	
	public String getTitle() {
		if (title == null) {
			String name = getBasename();
			if (name!=null)	title = name.replaceAll("[^A-Za-z0-9']", " ");
		}
		return title;
	}
	
	protected void setTitle(String name) {
		this.title = name;
	}

	public int compareTo(IMediaResource o) {
		return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
	}

	public String getExtension() {
		String name = getName();
		if (name==null) return null;
		int p = name.lastIndexOf('.');
		if (p!=-1) {
			return name.substring(p+1);
		} else {
			return null;
		}
	}
	
	public String getBasename() {
		String name = getName();
		if (name==null) return null;
		int p = name.lastIndexOf('.');
		if (p!=-1) {
			return name.substring(0, p);
		} else {
			return name;
		}
	}

	public String getRelativePath(IMediaResource res) {
		// TODO Auto-generated method stub
		return null;
	}

	public IMediaFolder getParent() {
		return parent;
	}
}
