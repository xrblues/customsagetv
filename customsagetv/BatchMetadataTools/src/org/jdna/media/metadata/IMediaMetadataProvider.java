package org.jdna.media.metadata;

import java.util.List;

public interface IMediaMetadataProvider {
	public static final int SEARCH_TITLE = 0;
	public IProviderInfo getInfo();
	public IMediaMetadata getMetaData(IMediaSearchResult result) throws Exception;
	public IMediaMetadata getMetaData(String providerDataUrl) throws Exception;
	public List<IMediaSearchResult> search(int searchType, String arg) throws Exception;
}
