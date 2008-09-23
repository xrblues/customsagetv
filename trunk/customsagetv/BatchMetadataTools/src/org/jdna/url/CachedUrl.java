package org.jdna.url;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.util.IOUtils;

public class CachedUrl extends Url implements IUrl {
	private static final Logger log = Logger.getLogger(CachedUrl.class);

	private File propFile = null;
	private Properties props = null;
	public File urlCacheDir = null; 

	public CachedUrl(String url) throws IOException {
		super(url);
		
		String cachedFileName = getCachedFileName(url);
		propFile = new File(getCacheDir(), cachedFileName + ".properties");
		props = new Properties();
		if (propFile.exists()) {
			log.debug("Reloading existing cached url: " + propFile.getAbsolutePath());
			props.load(new FileInputStream(propFile));
			File f = getCachedFile(); 
			if (f.exists() && isExpired(f)) {
				log.debug("Expiring Cached Url File: " + f.getAbsolutePath());
				f.delete();
			}
		} else {
			File f = propFile.getParentFile();
			f.mkdirs();
			log.debug("Creating a new cached url for: " + url);
			props.setProperty("url", url);
			props.setProperty("file", createCachedFile());
		}
		
		// sanity check
		if (!url.equals(props.getProperty("url"))) {
			throw new IOException("Caching is messed up.  The Cached url does not match the one passed! " + props.getProperty("url") + " != " + url);
		}
	}

	private String getCachedFileName(String url) {
		try {
			URL u = new URL(url);
			String path = u.getPath();
			String q = u.getQuery();
			if (q==null) {
				return path;
			} else {
				String name = q.replaceAll("[^a-zA-Z0-9]+", "_");
				return path + "_" + name;
			}
		} catch (MalformedURLException e) {
			log.error("Failed to create cached filename for url: " + url, e);
			throw new RuntimeException(e);
		}
	}
	
	private boolean isExpired(File cachedFile) {
		long secs = Long.parseLong(ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "expireSeconds", String.valueOf(60*60*24)));
		long diff = (System.currentTimeMillis()/1000) - cachedFile.lastModified();
		if (diff>secs) {
			return true;
		}
		return false;
	}

	private File getCacheDir() {
		if (urlCacheDir==null) {
			urlCacheDir = new File(ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "cacheDir", "cache/url/"));
			if (!urlCacheDir.exists()) urlCacheDir.mkdirs();
		}
		return urlCacheDir;
	}

	private String createCachedFile() throws IOException {
		File f = File.createTempFile(propFile.getName(), ".cache", propFile
				.getParentFile());
		
		// we just want the name
		f.delete();
		
		return f.getCanonicalPath();
	}

	public URL getOriginalUrl() throws IOException {
		return new URL(props.getProperty("url"));
	}

	public File getPropertyFile() {
		return propFile;
	}

	public File getCachedFile() {
		return new File(props.getProperty("file"));
	}

	public boolean hasMoved() {
		return Boolean.parseBoolean(props.getProperty("moved", "false"));
	}

	public URL getMovedUrl() throws IOException {
		return new URL(props.getProperty("movedUrl"));
	}

	public URL getUrl() throws IOException {
		return getUrl(null);
	}
	
	public URL getUrl(ICookieHandler handler) throws IOException {
		File f = getCachedFile();
		if (!f.exists()) {
			cache(handler);
		} else {
			log.debug("Cached File exists: " + f.getAbsolutePath() + " so we'll just use it.");
		}
		return f.toURI().toURL();
	}

	public void cache(ICookieHandler handler) throws IOException {
		log.debug("Caching Url: " + getOriginalUrl().toExternalForm());
		URL u = getOriginalUrl();
		URLConnection c = u.openConnection();
		sendCookies(u, c, handler);
		if (c instanceof HttpURLConnection) {
			HttpURLConnection conn = (HttpURLConnection) c;
			conn.setInstanceFollowRedirects(false);
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008072820 Firefox/3.0.1");
			InputStream is = conn.getInputStream();
			int rc = conn.getResponseCode();
			if (rc == HttpURLConnection.HTTP_MOVED_PERM
					|| rc == HttpURLConnection.HTTP_MOVED_TEMP) {
				props.setProperty("moved", "true");
				String redirectUrl = conn.getHeaderField("Location");
				if (redirectUrl != null) {
					int p = redirectUrl.indexOf('?');
					if (p != -1) {
						redirectUrl = redirectUrl.substring(0, p);
					}
					props.setProperty("movedUrl", redirectUrl);
				}
				File f = getCachedFile();
				IOUtils.copyStream(is, new FileOutputStream(f));
				log.debug("Url " + u.toExternalForm() +" Cached To: " + f.getAbsolutePath());
				log.debug(String.format("Url: %s moved to %s", u.toExternalForm(), redirectUrl));
			} else if (rc == HttpURLConnection.HTTP_OK) {
				handleCookies(u, c, handler);
				File f = getCachedFile();
				IOUtils.copyStream(is, new FileOutputStream(f));
				log.debug("Url " + u.toExternalForm() +" Cached To: " + f.getAbsolutePath());
			} else {
				throw new IOException("Http Response Code: " + rc
						+ "; Message: " + conn.getResponseMessage());
			}
		} else {
			// do nothing... we can't cache local urls
			log.warn("Cannot Cache Url Connection Type; "
					+ c.getClass().getName() );
			
			
		}
		props.store(new FileOutputStream(getPropertyFile()), "Cached Url Properties");
		log.debug("Properties for cached url are now stored: " + getPropertyFile().getAbsolutePath());
	}

	@Override
	public InputStream getInputStream(ICookieHandler handler) throws IOException {
		URL u = getUrl(handler);
		
		return u.openStream();
	}

	/**
	 * Will remove a url from the cache, in the event that url caching is enabled.
	 * @param dataUrl
	 */
	public static void remove(String dataUrl) {
		try {
			CachedUrl cu = new CachedUrl(dataUrl);
			//TODO uncomment 
			//cu.remove();
		} catch (IOException e) {
			log.error("Unabled to remove cached data url: " + dataUrl);
		}
		
	}

	private void remove() {
		try {
			log.debug("Removing Cached Url: " + this.getOriginalUrl().toExternalForm());
			if (props!=null) {
				File f =getCachedFile();
				if (f.exists()) {
					log.debug("Removing Cached File: " + f.getAbsolutePath());
					f.delete();
				}
			}
		} catch (IOException e) {
		}
	}
}
