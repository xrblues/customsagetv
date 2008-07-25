package org.jdna.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple Scanner that will notify a listener for each File it finds.
 * 
 * @author seans
 *
 */
public class DirectoryScanner {
	private List<IFileListener> listeners = new ArrayList<IFileListener>();
	private FileFilter filter;
	private boolean recurse=false;
	
	public DirectoryScanner(FileFilter filter, IFileListener listener) {
		this.filter = filter;
		this.listeners.add(listener);
	}
	
	public void addFileListener(IFileListener fl) {
		listeners.add(fl);
	}
	
	public void setFileFilter(FileFilter filter) {
		this.filter = filter;
	}
	
	public void scan(String start) throws IOException {
		scan(new File(start));
	}
	
	public void scan(File start) throws IOException {
		if (start.isDirectory()) {
			if (notifyListeners(start)==true) {
				// abort
				return;
			} else {
				File[] files;
				if (filter!=null) {
					files = start.listFiles(filter);
				} else {
					files = start.listFiles();
				}
				
				for (File f :  files) {
					if (f.isDirectory() && !recurse) continue;
					scan(f);
				}
			}
		} else {
			if (filter!=null && filter.accept(start)) {
				notifyListeners(start);
			}
		}
	}

	private boolean notifyListeners(File start) {
		boolean abort = false;
		for (IFileListener fl : listeners) {
			if (fl.accept(start)==false) abort=true;
		}
		return abort;
	}

	public void setRecurse(boolean recurse) {
		this.recurse = recurse;
	}
}
