package org.jdna.media.util;

import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;

public class CountResourceVisitor implements IMediaResourceVisitor {
	private int count=0;
	
	public void visit(IMediaResource resource) {
		count++;
	}

	public int getCount() {
		return count;
	}
}
