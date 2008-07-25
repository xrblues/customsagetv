package org.jdna.media;

import java.net.URI;

public interface IMediaResource extends Comparable<IMediaResource> {
	// public void accept(IResourceVisitor visitor);
	// public void copy()
	// public void delete();
	public String getName();
	public URI getLocationUri();
	public String getPath();
	public String getBasename();
	public String getExtension();
	public IMediaResource getParent();
	public boolean isReadOnly();
	public boolean exists();
	public long lastModified();
	public String getRelativePath(IMediaResource res);
	public String getTitle();
	public void touch();
}
