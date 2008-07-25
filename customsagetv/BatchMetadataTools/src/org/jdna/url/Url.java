package org.jdna.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.log4j.Logger;

public class Url implements IUrl {
	private Logger log = Logger.getLogger(Url.class);
	
	private String url = null;
	public Url(String url) {
		this.url = url;
	}
	
	public URL getMovedUrl() throws IOException {
		return null;
	}

	public URL getUrl() throws IOException {
		return new URL(url);
	}

	public boolean hasMoved() {
		return false;
	}
	
	protected void sendCookies(URL url, URLConnection conn, ICookieHandler handler) {
		if (handler != null){
			Map<String,String> cookies = handler.getCookiesToSend(url.toExternalForm());
			if (cookies!=null) {
				for (String key : cookies.keySet()) {
					log.debug("Sending Cookie: " + key +"=" + cookies.get(key) + " to " + url.toExternalForm());
					conn.setRequestProperty("Cookie", String.format("%s=%s", key, cookies.get(key)));
				}
			}
		}
	}
	
	public InputStream getInputStream(ICookieHandler handler) throws IOException {
		URL u = getUrl();
		
		URLConnection conn = u.openConnection();
		sendCookies(u, conn, handler);
		
		// get the stream
		InputStream is = conn.getInputStream();
		
		handleCookies(u, conn, handler);
		return is;
	}

	protected void handleCookies(URL u, URLConnection conn, ICookieHandler handler) {
		if (handler != null) {
			// process the response cookies
			String headerName=null;
			for (int i=1; (headerName = conn.getHeaderFieldKey(i))!=null; i++) {
			 	if (headerName.equals("Set-Cookie")) {                  
			 		String cookie = conn.getHeaderField(i);
			 		handler.handleSetCookie(u.toExternalForm(), cookie);
			 	}
			}
		}
	}

}
