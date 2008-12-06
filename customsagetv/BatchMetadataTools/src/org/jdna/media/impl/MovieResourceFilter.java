package org.jdna.media.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceFilter;

public class MovieResourceFilter implements IMediaResourceFilter {
	private static final Logger log = Logger.getLogger(MovieResourceFilter.class);
	public static final IMediaResourceFilter INSTANCE = new MovieResourceFilter();
	
	private Pattern filePattern = null;
	private Pattern dirExcludePattern = null;
	
	public MovieResourceFilter() {
		String pat = ConfigurationManager.getInstance().getMediaConfiguration().getVideoExtensionsRegex();
		log.debug("Using Movie Filter Regex: " + pat);
		filePattern = Pattern.compile(pat,Pattern.CASE_INSENSITIVE);
		
		pat = ConfigurationManager.getInstance().getMediaConfiguration().getExcludeVideoDirsRegex();
		if (pat==null) {
			log.debug("Not Using any Directory Exclude Filters.");
		} else {
			log.debug("Using Directory Exclude Regex: " + pat);
			dirExcludePattern = Pattern.compile(pat,Pattern.CASE_INSENSITIVE);
		}
		
	}
	
	public boolean accept(IMediaResource resource) {
		if (resource == null) return false;

		if (resource.getType() == IMediaFolder.TYPE_FOLDER) {
			if (dirExcludePattern==null) {
				// keep it
				return true;
			} else {
				String name = resource.getName();
				Matcher m = dirExcludePattern.matcher(name);
				return !m.matches();
			}
		} else {
			// if this is a DVD Media Item, then keep it
			if (resource.getContentType() == IMediaResource.CONTENT_TYPE_DVD) {
				return true;
			} else {
				// otherwise, check the ext to see if this a movie
				String ext = resource.getExtension();
				if (ext == null) return false;
				
				Matcher m = filePattern.matcher(ext);
				return m.matches();
			}
		}
	}

}