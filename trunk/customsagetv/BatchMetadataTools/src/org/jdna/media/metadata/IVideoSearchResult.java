package org.jdna.media.metadata;

public interface IVideoSearchResult {
	public static final int RESULT_TYPE_UNKNOWN = 0;
	public static final int RESULT_TYPE_EXACT_MATCH = 1;
	public static final int RESULT_TYPE_PARTIAL_MATCH = 2;
	public static final int RESULT_TYPE_POPULAR_MATCH = 3;
	
	public static final String[] SEARCH_TYPE_NAMES = new String[] {
		"Unknown",
		"Exact Match",
		"Partial Match",
		"Popular Match"
	};

	public static final String[] SEARCH_TYPE_NAMES_CHAR = new String[] {
		"?",
		"*",
		"-",
		"+"
	};
	
	
	public IVideoMetaData getMetaData() throws MetaDataException;
	public String getTitle();
	public String getYear();
	public int getResultType();
}
