package org.jdna.media.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;

public class MediaFile extends FileMediaResource implements IMediaFile {
	private boolean stacked;
	private List<IMediaResource> parts;

	public MediaFile(IMediaFolder parent, File f) {
		super(parent, f);
	}

	@Override
	public int compareTo(IMediaResource o) {
		if (o instanceof IMediaFile) {
			return super.compareTo(o);
		} else {
			return 1;
		}
	}

	public List<IMediaResource> getParts() {
		return parts;
	}
	
	

	public boolean isStacked() {
		return stacked;
	}

	public void setStacked(boolean b) {
		stacked = b;
		
		if (b==true) {
			if (parts==null) {
				// setup the stacking list... with ourself as the first element
				parts = new ArrayList<IMediaResource>();
				parts.add(this);
				
				// setup the media file title
				if (parent!=null) {
					setTitle(parent.getStackingModel().getStackedTitle(this));
				}
			}
		} else {
			if (parts!=null) {
				parts.clear();
				parts = null;
			}
		}
	}

	public void addStackedTitle(IMediaResource res) {
		if (!isStacked()) setStacked(true);
		parts.add(res);
	}
}
