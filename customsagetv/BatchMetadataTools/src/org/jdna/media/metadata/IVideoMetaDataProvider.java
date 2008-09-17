package org.jdna.media.metadata;

import java.util.List;

public interface IVideoMetaDataProvider {
	public static final int SEARCH_TITLE = 0;
	public String getName();
	public String getIconUrl();
	public String getId();
	public IVideoMetaData getMetaData(IVideoSearchResult result) throws Exception;
	public IVideoMetaData getMetaData(String providerDataUrl) throws Exception;
	public List<IVideoSearchResult> search(int searchType, String arg) throws Exception;
}
