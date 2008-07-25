package org.jdna.url;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

public class UrlUtil {
	private static final Logger log = Logger.getLogger(UrlUtil.class);
	
	/**
	 * Returns the the entire Url Path except the filename, like doing a basedir on a filename.
	 * 
	 * @param url
	 * @return
	 */
	public static String getBaseUrl(String url) {
		return url.substring(0,url.lastIndexOf("/"));
	}
	
	public static String getDomainUrl(String url) {
		URL u;
		try {
			u = new URL(url);
			return String.format("%s://%s/", u.getProtocol(), u.getHost());
		} catch (MalformedURLException e) {
			log.error("Failed to get domain url for: " + url);
		}
		return null;
	}

	public static String joinUrlPath(String baseUrl, String path) {
		StringBuffer sb = new StringBuffer(baseUrl);
		if (baseUrl.endsWith("/") && path.startsWith("/")) {
			path = path.substring(1);
		}
		sb.append(path);
		
		return sb.toString();
	}

	public static String getPathName(String url) {
		URL u;
		try {
			u = new URL(url);
			return u.getPath();
		} catch (MalformedURLException e) {
			log.error("getPathName() Failed! " + url, e);
		}
		return null;
	}
}
