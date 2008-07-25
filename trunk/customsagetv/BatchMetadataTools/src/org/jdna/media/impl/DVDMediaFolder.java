package org.jdna.media.impl;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;

/**
 * DVD Media Folder is a special Folder that behaves like a folder and it also behaves as file.
 * 
 * @author seans
 *
 */
public class DVDMediaFolder extends MediaFolder implements IMediaFile {
	// TODO: Move this out to a setting...
	private static boolean deepDVDScanning = true;
	private static Pattern dvdFileExtPattern = Pattern.compile("\\.vob$|\\.ifo$|\\.bup$",Pattern.CASE_INSENSITIVE);

	public DVDMediaFolder(IMediaFolder parent, File folder) {
		super(parent, folder);
	}

	public List<IMediaResource> getParts() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isStacked() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static boolean isDVD(File f) {
		if (f.isDirectory()) {
			File dvd = new File(f, "VIDEO_TS");
			if (dvd.exists()) return true;
			
			if (deepDVDScanning) {
				File files[] = f.listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						String name = pathname.getName();
						Matcher m = dvdFileExtPattern.matcher(name);
						return m.find();
					}
				});
				
				if (files!=null && files.length>0) return true;
			}
			
			return false;
		} else {
			return false;
		}
	}

}
