package org.jdna.media.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceFilter;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.IMediaStackModel;
import org.jdna.media.MediaResourceFactory;

public class MediaFolder extends AbstractMediaResource implements IMediaFolder {
	private static final Logger log = Logger.getLogger(MediaFolder.class);
	
	private static long updateDelay = 600 * 1000;
	private IMediaResourceFilter xfilter;
	private IMediaStackModel xstackingModel;
	
	private List<IMediaResource> members = new ArrayList<IMediaResource>();
	private long lastUpdate = 0;

	public MediaFolder(String folderUri) throws URISyntaxException {
		super(folderUri, IMediaFolder.TYPE_FOLDER, IMediaResource.CONTENT_TYPE_UNKNOWN);
		setFilter(MovieResourceFilter.INSTANCE);
		setStackingModel(CDStackingModel.INSTANCE);
	}
	
	public MediaFolder(URI folder) {
		super(folder, IMediaFolder.TYPE_FOLDER, IMediaResource.CONTENT_TYPE_UNKNOWN);
		setFilter(MovieResourceFilter.INSTANCE);
		setStackingModel(CDStackingModel.INSTANCE);
	}

	public MediaFolder(URIAdapter ua) {
		super(ua, IMediaFolder.TYPE_FOLDER, IMediaResource.CONTENT_TYPE_UNKNOWN);
		setFilter(MovieResourceFilter.INSTANCE);
		setStackingModel(CDStackingModel.INSTANCE);
	}

	public List<IMediaResource> members() {
		long now = System.currentTimeMillis();
		if (now - lastUpdate > updateDelay) {
			lastUpdate = now;

			if (!exists()) {
				log.warn("Cannot get members for non existent Folder: " + getLocationUri());
				return members;
			}
			
			// data needs to be refreshed...
			// iterate through the resources and build up a list media resources for this folder
			List<IMediaResource> l = new ArrayList<IMediaResource>();
			URI files[] = getURIAdapter().listMembers();
			int s = files.length;
			for (int i=0;i<s;i++) {
				URI f = files[i];
				IMediaResource r = null;
				URIAdapter ua = URIAdapterFactory.getAdapter(f);
				try {
					r = MediaResourceFactory.getInstance().createResource(ua);
				} catch (IOException e) {
					log.error("Failed to create resource: " + ua.getUri().toASCIIString(), e);
					continue;
				}

				IMediaResourceFilter filter = getFilter();
				if (filter==null || filter.accept(r)) {
					l.add(r);
				}
			}
			
			Collections.sort(l);
			
			IMediaStackModel stackingModel = getStackingModel();
			if (stackingModel!=null) {
				List <IMediaResource> stackedList = new ArrayList<IMediaResource>(l.size());
				String lastTitle = null;
				String curTitle = null;
				
				// TODO: Enable stacking for DVD content types
				
				MediaFile mf = null;
				// apply stacking against the list using the stacking model
				for (IMediaResource res : l) {
					if (res.getType() == IMediaFolder.TYPE_FOLDER || res.getContentType() == IMediaResource.CONTENT_TYPE_DVD) {
						stackedList.add(res);
					} else {
						curTitle = stackingModel.getStackedTitle(res);
						if (curTitle!=null && curTitle.equals(lastTitle)) {
							mf = (MediaFile) stackedList.get(stackedList.size()-1);
							mf.addStackedTitle(res);
						} else {
							lastTitle = curTitle;
							stackedList.add(res);
							// set the title on the stacked resource
							((MediaFile)res).setTitle(curTitle);
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
		if (o.getType() == IMediaFolder.TYPE_FOLDER) {
			return super.compareTo(o);
		} else {
			return -1;
		}
	}

	public IMediaResourceFilter getFilter() {
		if (xfilter!=null) return xfilter;
		return null;
	}

	public IMediaStackModel getStackingModel() {
		if (xstackingModel!=null) return xstackingModel;
		return null;
	}

	public void setFilter(IMediaResourceFilter filter) {
		this.xfilter = filter;
		
		// refresh data
		this.lastUpdate = 0;
	}

	public void setStackingModel(IMediaStackModel stackingModel) {
		this.xstackingModel = stackingModel;
		
		// refresh data
		this.lastUpdate = 0;
	}

	public boolean contains(String path) {
		return getURIAdapter().createUriAdapter(path).exists();
	}

	@Override
	public void accept(IMediaResourceVisitor visitor) {
		accept(visitor, true);
	}

	public void accept(IMediaResourceVisitor visitor, boolean recurse) {
		visitor.visit(this);
		List<IMediaResource> m = members();
		for (IMediaResource r : m) {
			if (recurse || r.getType()==IMediaFile.TYPE_FILE) {
				r.accept(visitor);
			}
		}
	}

	/**
	 * Return s child resource of this folder resource, or null if the doesn't exist.
	 * 
	 * The path must be a url unix style path.  ie, use "/" and not backslash.  Paths
	 * cannot start with "/" or a ".".  If you do pass paths that start with "/" or ".", then
	 * a runtime exception will be thrown.
	 * 
	 * @param path path cannot start with "/" or "."
	 */
	public IMediaResource getResource(String path) {
		if (path==null) return null;
		if (path.startsWith("/") || path.startsWith(".")) throw new RuntimeException("Cannot specify paths starting with / or .");
		int dirPos = path.indexOf("/");
		if (dirPos!=-1) {
			String dir = path.substring(0, dirPos);
			IMediaFolder subDir = findDir(dir);
			return subDir.getResource(path.substring(dirPos+1));
		} else {
			return findFile(path);
		}
	}

	private IMediaFile findFile(String path) {
		for (IMediaResource f : members()) {
			if (f.getType() == IMediaFile.TYPE_FILE && f.getName().equals(path)) {
				return (IMediaFile)f;
			}
		}
		return null;
	}

	private IMediaFolder findDir(String dir) {
		for (IMediaResource f : members()) {
			if (f.getType() == IMediaFolder.TYPE_FOLDER && f.getName().equals(dir)) {
				return (IMediaFolder)f;
			}
		}
		return null;
	}

}
