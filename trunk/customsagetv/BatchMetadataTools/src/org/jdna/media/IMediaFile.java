package org.jdna.media;

import java.util.List;

public interface IMediaFile extends IMediaResource {
	public boolean isStacked();
	public List<IMediaResource> getParts();
}
