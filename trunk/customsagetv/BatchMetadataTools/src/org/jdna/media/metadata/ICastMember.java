package org.jdna.media.metadata;

public interface ICastMember {
	public String getId();
	public String getName();
	public String getPart();
	public int getType();

	public static final int ACTOR = 0;
	public static final int WRITER = 1;
	public static final int DIRECTOR = 2;
	public static final int OTHER = 99;
	public String getProviderDataUrl();
	
}
