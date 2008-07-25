package org.jdna.util;

import java.io.File;

/**
 * Listener to be used with the DirectoryScanner.
 * 
 * @author seans
 *
 */
public interface IFileListener {

	/**
	 * Accepts a file for processing.  If this method returns false, then that file chain (ie, dir) is not be processed any futher.
	 * @param file File or Directory to accept
	 * @return return true to continue processing the file chain.  False aborts processing the file chain.
	 */
	public boolean accept(File file);

}
