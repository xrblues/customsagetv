package org.jdna.media.metadata;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.impl.CDStackingModel;

public class VideoMetaDataUtils{
	
	private static final Logger log = Logger.getLogger(VideoMetaDataUtils.class);
	
	/**
	 * Using a URL Connection, it will write the URL to 
	 * @param string
	 * @param out
	 */
	public static void writeImageFromUrl(String url, File out) throws IOException {
		BufferedImage img = ImageIO.read(new URL(url));
		ImageIO.write(img, "jpg", out);
	}
	
	/**
	 * Utility method that can be used to clean the search criteria before
	 * sending it to the seach function. eg, it removes any non alphanumeric
	 * characters and replaces them with spaces.  remove cd volumes, and some common words like dvd, dvrip, etc.
	 * 
	 * You can control the cd and word tokens following keys in the configuration manager.
	 * <pre>
	 * org.jdna.media.metadata.VideoMetaDataUtils.cleanSearchCriteria.cdTokens
	 * org.jdna.media.metadata.VideoMetaDataUtils.cleanSearchCriteria.wordTokens
	 * </pre>
	 */
	public static String cleanSearchCriteria(String s, boolean removeYear) {
		String wordTokens[] = ConfigurationManager.getInstance().getProperty("org.jdna.media.metadata.VideoMetaDataUtils.cleanSearchCriteria.wordTokens", "dvd,dvdrip,cam,ts,tc,scr,screener,dvdscr,xvid,divx,avi,vrs,repack,mallat,proper,dmt,dmd,stv").split(",");

		s = CDStackingModel.INSTANCE.getStackedTitle(s);
		log.debug("Cleaning Search Criteria: " + s);
		
		String parts[] = (s.replaceAll("[^A-Za-z0-9']", " ")).split(" ");
		StringBuffer sb = new StringBuffer();
		int l = parts.length;
		for (int i = 0; i < l; i++) {
			String ss = parts[i].trim();

			// check all tokens for the word tokens to ignore
			boolean found = false;
			for (String t : wordTokens) {
				if (ss.equalsIgnoreCase(t)) {
					log.debug("Word Token Matched: " + t);
					found = true;
					break;
				}
			}
			// stop processing if you find a word token
			if (found) break;
			
			sb.append(ss).append(" ");
		}

		String v = sb.toString().trim();
		if (v.length()==0) {
			log.warn("After cleaning title we have no title... reseting title to the original: " +s);
			v=s;
		}
		
		if (removeYear) {
			v = v.replaceAll("19[0-9]+", "");
			v = v.replaceAll("20[0-9]+", "");
		}
		
		log.debug("Cleaned Title: " + v);
		return v;
	}
	
	public static String getBasename(File videoFile) {
		try {
			String name = videoFile.getName();
			name = name.substring(0,name.lastIndexOf("."));
			return name;
		} catch (Exception e) {
			return videoFile.getName();
		}
	}
	
	
	/**
	 * Given a String in the format, ${KEY}..., it will replace all occurances of ${KEY}
	 * with a lookup in the Map for KEY.
	 * 
	 * ie, ${TEST1} -- ${TEST2} would result in Hello -- There, provided that the map contained 2 keys TEST1=Hello, TEST2=There
	 * 
	 * @param s Format String
	 * @param map Map of Named Paramters
	 * @return
	 */
	public static String format(String s, Properties props) {
		log.debug("Formatting: " + s);
		Pattern p = Pattern.compile("(\\$\\{[_\\.a-zA-Z]+\\})");
		Matcher m = p.matcher(s);
		StringBuffer sb = new StringBuffer();
		
		int lastStart = 0;
		while (m.find()) {
			String token = m.group(0);
			sb.append(s.substring(lastStart, m.start()));
			lastStart=m.end();
			String key = token.substring(2,token.length()-1);
			String val = props.getProperty(key, "");
			sb.append(val);
		}
		
		sb.append(s.substring(lastStart));
		
		return sb.toString();
	}	
}