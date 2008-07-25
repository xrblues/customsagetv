package org.jdna.media.impl;

import java.io.File;

import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaSource;

public class DirectoryMediaSource implements IMediaSource {
	private File dir;
	private IMediaFolder folder = null;
	private String name;

	public DirectoryMediaSource() {
	}
	
	public DirectoryMediaSource(String name, File dir) {
		setName(name);
		setDirectory(dir);
	}
	
	public IMediaFolder getMediaFolder() {
		return folder;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		if (dir==null) return null;
		return dir.getAbsolutePath();
	}
	
	public void setName(String name) {
		this.name= name;
	}
	
	public void setDirectory(File dir) {
		this.dir = dir;
		this.folder = new MediaFolder(null, dir);
	}

}
