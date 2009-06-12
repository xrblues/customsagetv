package org.jdna.media;

public class CompositeAndResourceFilter extends AbstractCompositeResourceFilter {

    public CompositeAndResourceFilter() {
        super();
    }

    public CompositeAndResourceFilter(IMediaResourceFilter filter1, IMediaResourceFilter filter2) {
        super(filter1, filter2);
    }

    public boolean accept(IMediaResource resource) {
        for (IMediaResourceFilter f : filters) {
            if (!f.accept(resource)) {
                return false;
            }
        }
        return true;
    }
}
