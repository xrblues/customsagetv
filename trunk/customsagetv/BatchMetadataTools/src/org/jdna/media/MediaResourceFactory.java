package org.jdna.media;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.jdna.media.impl.MediaFile;
import org.jdna.media.impl.MediaFolder;

public class MediaResourceFactory {
	public static MediaResourceFactory instance;
	
	public static MediaResourceFactory getInstance() {
		if (instance == null) instance = new MediaResourceFactory();
		return instance;
	}
	
	public MediaResourceFactory() {
	}
	
	//public IMediaResource createResource(String uri) throws IOException {
	//	return createResource(new String[] {uri});
	//}
	
	/**
	 * creates a MediaResource based on the give uri.  If an array of uris are passed in, then the resulting MediaResource is a "Stacked" resource.
	 * @param uri
	 * @return
	 * @throws IOException
	 */
	public IMediaResource createResource(String... uri) throws IOException {
		URI u = null;
			u = URI.create(uri[0]);
		
		if ("file".equals(u.getScheme()) || u.getScheme()==null || u.getScheme().length()==0) {
			File f = new File(u);
			if (f.isDirectory()) {
				return new MediaFolder(null, f);
			} else {
				MediaFile mf = new MediaFile(null, f);
				if (uri.length>1) {
					for (int i=1;i<uri.length;i++) {
						mf.addStackedTitle(createResource(uri[i]));
					}
				}
				return mf;
			}
		} else {
			throw new IOException("Can't Handle Scheme: " + u.getScheme());
		}
	}
}
