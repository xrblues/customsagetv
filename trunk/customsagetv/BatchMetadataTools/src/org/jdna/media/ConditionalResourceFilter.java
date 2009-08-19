package org.jdna.media;


/**
 * Create a Filter that is composed of serveral AND and OR filters.
 * 
 * The filter return true is all of the AND filters return true, AND at least 1 or filter
 * returns true.
 * 
 * @author seans
 *
 */
public class ConditionalResourceFilter implements IMediaResourceFilter {
    private CompositeAndResourceFilter ands = new CompositeAndResourceFilter();
    private CompositeOrResourceFilter ors = new CompositeOrResourceFilter();
    
    public ConditionalResourceFilter() {
    }

    public ConditionalResourceFilter and(IMediaResourceFilter filter) {
        ands.addFilter(filter);
        return this;
    }
    
    public ConditionalResourceFilter or(IMediaResourceFilter filter) {
        ors.addFilter(filter);
        return this;
    }
    
    public boolean accept(IMediaResource resource) {
        return ands.accept(resource) && ors.accept(resource);
    }

}
