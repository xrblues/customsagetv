package org.jdna.media.impl;

import java.io.File;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFolder;

public class FileMediaResource extends AbstractMediaResource {
	public static final Logger log = Logger.getLogger(FileMediaResource.class);
	
	protected File file;

	public FileMediaResource(IMediaFolder parent, File file) {
		super(parent);
		if (file==null) throw new RuntimeException("File Cannot be null for FileMediaResource");
		this.file = file;
	}
	
	protected File getFile() {
		return file;
	}

	public boolean exists() {
		return file.exists();
	}
	
	public String getLocationUri() {
		return file.toURI().toString();
	}

	public String getName() {
		return file.getName();
	}

	public String getPath() {
		return file.getAbsolutePath();
	}

	public boolean isReadOnly() {
		return !file.canWrite();
	}

	public long lastModified() {
		return file.lastModified();
	}

	public void touch() {
		try {
			if (file.exists()) {
				file.setLastModified(System.currentTimeMillis());
			} else {
				file.createNewFile();
			}
		} catch (Exception e) {
			log.error("File Touch Failed for: " + file.getAbsolutePath());
		}
	}
	
}
