package org.jdna.media.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceFilter;
import org.jdna.media.IMediaStackModel;

public class MediaFolder extends FileMediaResource implements IMediaFolder {
	private static final Logger log = Logger.getLogger(MediaFolder.class);
	
	private static long updateDelay = 600 * 1000;
	private IMediaResourceFilter filter;
	private IMediaStackModel stackingModel;
	
	private List<IMediaResource> members = new ArrayList<IMediaResource>();
	private long lastUpdate = 0;

	public MediaFolder(IMediaFolder parent, File folder) {
		super(parent, folder);
	}

	public List<IMediaResource> members() {
		long now = System.currentTimeMillis();
		if (now - lastUpdate > updateDelay) {
			lastUpdate = now;

			if (getFile()==null || !getFile().exists()) {
				log.warn("Cannot get members for non existent Folder: " + getLocationUri());
				return members;
			}
			
			// data needs to be refreshed...
			// iterate through the resources and build up a list media resources for this folder
			List<IMediaResource> l = new ArrayList<IMediaResource>();
			File files[] = getFile().listFiles();
			int s = files.length;
			for (int i=0;i<s;i++) {
				File f = files[i];
				IMediaResource r = null;
				if (f.isDirectory()) {
					if (DVDMediaFolder.isDVD(f)) {
						r = new DVDMediaFolder(this, f);
					} else {
						r = new MediaFolder(this, f);
					}
				} else {
					r = new MediaFile(this, f);
				}

				if (filter==null || filter.accept(r)) {
					l.add(r);
				}
			}
			
			Collections.sort(l);
			
			if (stackingModel!=null) {
				List <IMediaResource> stackedList = new ArrayList<IMediaResource>(l.size());
				String lastTitle = null;
				String curTitle = null;
				MediaFile mf = null;
				// apply stacking against the list using the stacking model
				for (IMediaResource res : l) {
					if (res instanceof IMediaFolder) {
						stackedList.add(res);
					} else {
						curTitle = stackingModel.getStackedTitle(res);
						if (curTitle!=null && curTitle.equals(lastTitle)) {
							mf = (MediaFile) stackedList.get(stackedList.size()-1);
							mf.addStackedTitle(res);
						} else {
							lastTitle = curTitle;
							stackedList.add(res);
						}
					}
				}
				
				l.clear();
				l = stackedList;
			}
			
			// replace the old list with the new list
			try {
				List<IMediaResource> oldL = members;
				members = l;
				if (oldL!=null) oldL.clear();
			} catch (Exception e) {
				// probably a list state exception... ignore... gc should clean up.
			}
		}
		
		return members;
	}

	@Override
	public int compareTo(IMediaResource o) {
		if (o instanceof IMediaFolder) {
			return super.compareTo(o);
		} else {
			return -1;
		}
	}

	public IMediaResourceFilter getFilter() {
		return filter;
	}

	public IMediaStackModel getStackingModel() {
		return stackingModel;
	}

	public void setFilter(IMediaResourceFilter filter) {
		this.filter = filter;
		
		// refresh data
		this.lastUpdate = 0;
	}

	public void setStackingModel(IMediaStackModel stackingModel) {
		this.stackingModel = stackingModel;
		
		// refresh data
		this.lastUpdate = 0;
	}

	public boolean contains(String path) {
		File f = new File(getFile(), path);
		if (f==null) return false;
		return f.exists();
	}

	@Override
	public void touch() {
		if (file.exists()) {
			List<IMediaResource> m = members();
			for (IMediaResource r : m) {
				r.touch();
			}
		} else {
			file.mkdir();
		}
	}
}
