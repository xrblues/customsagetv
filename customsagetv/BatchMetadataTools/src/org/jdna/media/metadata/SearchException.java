package org.jdna.media.metadata;


public class SearchException extends Exception {

	public SearchException(String msg, Throwable e) {
		super(msg, e);
	}

	public SearchException(String msg) {
		super(msg);
	}
}
