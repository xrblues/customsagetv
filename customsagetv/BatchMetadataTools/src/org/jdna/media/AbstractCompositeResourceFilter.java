package org.jdna.media;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCompositeResourceFilter implements IMediaResourceFilter {
    protected List<IMediaResourceFilter> filters = new LinkedList<IMediaResourceFilter>();
    
    public AbstractCompositeResourceFilter() {
    }
    
    public AbstractCompositeResourceFilter(IMediaResourceFilter filter1, IMediaResourceFilter filter2) {
        addFilter(filter1);
        addFilter(filter2);
    }
    
    public void addFilter(IMediaResourceFilter filter) {
        filters.add(filter);
    }
}
