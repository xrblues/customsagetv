package org.jdna.media.impl;

import java.net.URISyntaxException;
import java.util.List;

import org.jdna.media.IMediaResource;

public class VirtualMediaFolder extends MediaFolder {
	private List<IMediaResource> items = null;
	
	public VirtualMediaFolder(String folderName, List<IMediaResource> items) throws URISyntaxException {
		super("tmp://"+folderName);
		this.items=items;
	}
	
	@Override
	public List<IMediaResource> members() {
		return items;
	}
}
