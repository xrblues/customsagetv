package org.jdna.media.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaStackModel;

/**
 * As Taken from http://xbmc.org/wiki/?title=Stacking
 * <pre>
 * It can be summarized as the following
 * [token][cd|part|dvd][num]
 * </pre> 
 * @author seans
 *
 */
public class CDStackingModel implements IMediaStackModel {
	private static final Logger log = Logger.getLogger(CDStackingModel.class);
	
	public static final CDStackingModel INSTANCE = new CDStackingModel();
	
	// basic regexp that is used to stack the cd/dvd file names
	// TODO: make it so that this regexp can be put into a user setting
	private Pattern pattern = null;
	
	public CDStackingModel() {
		String pat = ConfigurationManager.getInstance().getProperty(CDStackingModel.class.getName(), "StackingRegex", "[ _\\\\.-]+(cd|dvd|part)[ _\\\\.-]*([0-9a-d]+)");
		log.debug("CD Stacking Regex: " + pat);
		pattern = Pattern.compile(pat,Pattern.CASE_INSENSITIVE);
	}
	
	public String getStackedTitle(IMediaResource resource) {
		return getStackedTitle(resource.getTitle());
	}
	
	public String getStackedTitle(String title) {
		log.debug("Title: " + title);
		if (title == null) return null;

		Matcher m = pattern.matcher(title);
		if (m.find()) {
			String s = title.substring(0,m.start());
			if (log.isDebugEnabled()) {
				log.debug(String.format("Title: %s; Stack: %s; MatchPos: %s; Regex: %s", title, s, m.start(), pattern.pattern()));
			}
			title = s;
		}

		return title;
	}
}
