package org.jdna.media.metadata;

public interface IMediaSearchResult {
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
	
	
	/**
	 * returns the provider id that created this result.
	 * @return
	 */
	public String getProviderId();

	/**
	 * id of the this result.  Should have meaning to the provider that created the result.
	 * @return
	 */
	public String getId();

	/**
	 * If the search result can determine the IMDB id, then it should return it here, if not, then return null.
	 * @return
	 */
	public String getIMDBId();
	
	public String getTitle();
	public String getYear();
	public int getResultType();
}
