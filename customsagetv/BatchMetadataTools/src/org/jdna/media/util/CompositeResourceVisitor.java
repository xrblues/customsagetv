package org.jdna.media.util;

import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;

/**
 * Resource Visitor that can chain other resource visitors.  If you need to execute more visitors per item, then 
 * keep chaining them, such as, new CompositeResourceVistor(v1, new CompositeResourceVisitor(v2,v3));
 * 
 * @author seans
 *
 */
public class CompositeResourceVisitor implements IMediaResourceVisitor {
	private IMediaResourceVisitor rv1;
	private IMediaResourceVisitor rv2;
	
	public CompositeResourceVisitor(IMediaResourceVisitor v1, IMediaResourceVisitor v2) {
		this.rv1=v1;
		this.rv2=v2;
	}

	public void visit(IMediaResource resource) {
		rv1.visit(resource);
		rv2.visit(resource);
	}
}
