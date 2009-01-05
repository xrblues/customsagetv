package org.jdna.media.util;

import java.util.ArrayList;
import java.util.List;

import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;

public class CollectorResourceVisitor implements IMediaResourceVisitor {
    private List<IMediaResource> collected = new ArrayList<IMediaResource>();

    public CollectorResourceVisitor() {
    }

    public void visit(IMediaResource resource) {
        collected.add(resource);
    }

    public List<IMediaResource> getCollection() {
        return collected;
    }
}
