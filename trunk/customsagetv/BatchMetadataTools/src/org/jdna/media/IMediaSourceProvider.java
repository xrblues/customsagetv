package org.jdna.media;

public interface IMediaSourceProvider {
	public IMediaSource[] getSources() throws Exception;
	public IMediaSource addSource(IMediaSource source) throws Exception;
	public IMediaSource getSource(String name) throws Exception;
	public IMediaSource createSource(String name, String path) throws Exception;
	public IMediaSource removeSource(IMediaSource source) throws Exception;
}
