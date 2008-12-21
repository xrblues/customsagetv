package org.jdna.media.util;

import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;

public class NullResourceVisitor implements IMediaResourceVisitor {

	public void visit(IMediaResource resource) {
		// do nothing
	}

}
