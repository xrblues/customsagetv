package org.jdna.media;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.impl.DVDMediaItem;
import org.jdna.media.impl.MediaFile;
import org.jdna.media.impl.MediaFolder;
import org.jdna.media.impl.URIAdapter;
import org.jdna.media.impl.URIAdapterFactory;
import org.jdna.media.impl.VirtualMediaFolder;

public class MediaResourceFactory {
	private static final Logger log = Logger.getLogger(MediaResourceFactory.class);
	public static MediaResourceFactory instance;
	
	public static MediaResourceFactory getInstance() {
		if (instance == null) instance = new MediaResourceFactory();
		return instance;
	}
	
	public MediaResourceFactory() {
	}
	
	public IMediaResource createResource(URI uri) throws IOException {
		return createResource(URIAdapterFactory.getAdapter(uri));
	}

	public IMediaResource createResource(String uri) throws IOException {
		return createResource(URIAdapterFactory.getAdapter(uri));
	}
	
	public IMediaResource createResource(URIAdapter uriAdapter) throws IOException {
		if (uriAdapter.isDirectory()) {
			if (DVDMediaItem.isDVD(uriAdapter)) {
				return new DVDMediaItem(uriAdapter);
			} else {
				return new MediaFolder(uriAdapter);
			}
		} else {
			return new MediaFile(uriAdapter);
		}
	}
	
	public IMediaFolder createVirtualFolder(String folderName, List<IMediaResource> items) {
		try {
			return new VirtualMediaFolder(folderName, items);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			log.error("Failed to create virtual folder",e);
			return null;
		}
	}
	
	/**
	 * Checks the following resource has stacked items.  If so, then it will stack them, otherwise
	 * it returns the original resource.
	 * 
	 * This is an expensive operation
	 * 
	 * @param mediaFile
	 * @return
	 */
	public IMediaFile getStackedResource(IMediaFile item) {
		try {
			IMediaFolder parent = (IMediaFolder) item.getParent();
			IMediaFile newItem = (IMediaFile) parent.getResource(item.getName());
			if (newItem!=null) {
				return newItem;
			} else {
				return item;
			}
		} catch (Exception e) {
			log.error("Failed to find resourse in it's parent.  Not sure why.", e);
			return item;
		}
	}
}
