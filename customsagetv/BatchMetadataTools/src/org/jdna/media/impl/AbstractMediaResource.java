package org.jdna.media.impl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.MediaResourceFactory;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.MediaMetadataFactory;

public abstract class AbstractMediaResource implements IMediaResource {
	private static final Logger log = Logger.getLogger(AbstractMediaResource.class);
	
	private String title;
	private URIAdapter uriAdapter;
	private int type = 0;
	private int contentType;
	
	public AbstractMediaResource(String locationUri, int type, int contentType) throws URISyntaxException {
		this(new URI(locationUri), type, contentType);
	}
	
	public AbstractMediaResource(URI uri, int type, int contentType) {
		this(URIAdapterFactory.getAdapter(uri), type, contentType);
	}
	
	public AbstractMediaResource(URIAdapter uriAdapter, int type, int contentType) {
		this.type = type;
		this.uriAdapter = uriAdapter;
		this.contentType=contentType;
	}
	
	public String getLocationUri() {
		return uriAdapter.getUri().toString();
	}
	
	public URIAdapter getURIAdapter() {
		return uriAdapter;
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

	public IMediaFolder getParent() {
		URI u = uriAdapter.getParentUri();
		if (u==null) {
			return null;
		} else {
			// better they returned a parent uri
			try {
				return (IMediaFolder) MediaResourceFactory.getInstance().createResource(u);
			} catch (Exception e) {
				log.error("Failed to get parent uri for: " + getLocationUri() + "; Parent Uri was: " + u.toString());
				return null;
			}
		}
	}
	
	public boolean exists() {
		return uriAdapter.exists();
	}
	
	public String getName() {
		return uriAdapter.getName();
	}

	public String getPath() {
		return uriAdapter.getUri().getPath();
	}

	public boolean isReadOnly() {
		return !uriAdapter.canWrite();
	}

	public long lastModified() {
		return uriAdapter.lastModified();
	}

	public void touch() {
		uriAdapter.touch();
	}

	public void accept(IMediaResourceVisitor visitor) {
		visitor.visit(this);
	}

	public void copy() {
		throw new UnsupportedOperationException("copy");
	}

	public void delete() {
		throw new UnsupportedOperationException("delete");
	}

	public String getRelativePath(IMediaResource res) {
		throw new UnsupportedOperationException("relative path");
	}

	public int getType() {
		return type;
	}

	public String getLocalMetadataUri() {
		return null;
	}

	public String getLocalSubtitlesUri() {
		return null;
	}

	public String getLocalThumbnailUri() {
		return null;
	}

	public IMediaMetadata getMetadata() {
		return null;
	}

	public void updateMetadata(IMediaMetadata metadata, boolean overwriteThumbnail) throws IOException {
		MediaMetadataFactory.getInstance().getDefaultPeristence().storeMetaData(metadata, this, overwriteThumbnail);
	}
	
	protected File getResourceAsFile() {
		try {
			return new File(new URI(getLocationUri()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public int getContentType() {
		return contentType;
	}
}
