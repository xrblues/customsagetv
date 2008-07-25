package org.jdna.url;

import java.io.IOException;

public class CachedUrlFactory implements IUrlFactory {

	public IUrl createUrl(String url) throws IOException {
		return new CachedUrl(url);
	}

}
