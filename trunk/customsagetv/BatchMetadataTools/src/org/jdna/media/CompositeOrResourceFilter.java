package org.jdna.media;

public class CompositeOrResourceFilter extends AbstractCompositeResourceFilter {

    public CompositeOrResourceFilter() {
        super();
    }

    public CompositeOrResourceFilter(IMediaResourceFilter filter1, IMediaResourceFilter filter2) {
        super(filter1, filter2);
    }

    public boolean accept(IMediaResource resource) {
        if (filters.size()==0) return true;
        
        for (IMediaResourceFilter f : filters) {
            if (f.accept(resource)) {
                return true;
            }
        }
        return false;
    }
}
