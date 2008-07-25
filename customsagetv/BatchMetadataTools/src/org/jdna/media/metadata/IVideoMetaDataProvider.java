package org.jdna.media.metadata;

import java.io.IOException;
import java.util.List;

public interface IVideoMetaDataProvider {
	public static final int SEARCH_TITLE = 0;
	public String getName();
	public String getIconUrl();
	public List<IVideoSearchResult> search(int searchType, String arg) throws SearchException;
	public Object getId();
	public IVideoMetaData getMetaData(String providerDataUrl) throws IOException;
}
