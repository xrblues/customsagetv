package org.jdna.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface IUrl {
	public URL getUrl() throws IOException;
	public URL getMovedUrl() throws IOException;
	public boolean hasMoved();
	public InputStream getInputStream(ICookieHandler handler) throws IOException;
}
