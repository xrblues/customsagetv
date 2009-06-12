package org.jdna.media.util;

import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceFilter;
import org.jdna.media.IMediaResourceVisitor;

/**
 * MediaResourceVisitor decorator that will only call the visitor on items that match the filter.
 * 
 * @author seans
 */
public class FilteredResourceVisitor implements IMediaResourceVisitor {
    private IMediaResourceVisitor visitor;
    private IMediaResourceFilter filter;
    
    public FilteredResourceVisitor(IMediaResourceFilter filter, IMediaResourceVisitor visitor) {
        this.filter=filter;
        this.visitor=visitor;
    }
    
    public void visit(IMediaResource resource) {
        if (filter.accept(resource)) {
            visitor.visit(resource);
        }
    }

    public IMediaResourceVisitor getVisitor() {
        return visitor;
    }

    public IMediaResourceFilter getFilter() {
        return filter;
    }
    
    
}
