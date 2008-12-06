package org.jdna.media.metadata;

public interface ICoverProvider {
	public static final int COVER_MOVIE = 0;
	public static final int COVER_TV = 1;
	
	public IProviderInfo getInfo();
	public ICoverResult[] findCovers(int COVER_TYPE, String title);
}
