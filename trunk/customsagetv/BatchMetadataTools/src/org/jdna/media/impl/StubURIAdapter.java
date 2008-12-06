package org.jdna.media.impl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class StubURIAdapter extends URIAdapter {

	public StubURIAdapter(URI uri) {
		super(uri);
	}

	@Override
	public String getName() {
		File f = new File(getUri().toString());
		return f.getName();
	}

	@Override
	public URI getParentUri() {
		File f = new File(getUri().toString());
		return f.getParentFile().toURI();
	}

	@Override
	public URIAdapter createUriAdapter(String name) {
		try {
			return URIAdapterFactory.getAdapter(new URI(getUri().toString() + "/" + name));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
