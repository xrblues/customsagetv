package org.jdna.media;

import java.io.IOException;

public interface IMediaSourceProvider {
	public IMediaSource[] getSources() throws IOException;
	public IMediaSource addSource(IMediaSource source) throws IOException;
	public IMediaSource getSource(String name) throws IOException;
	public IMediaSource removeSource(IMediaSource source) throws IOException;
}
