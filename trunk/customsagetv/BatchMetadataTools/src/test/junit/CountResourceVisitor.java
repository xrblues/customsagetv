package test.junit;

import java.util.LinkedList;
import java.util.List;

import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;

public class CountResourceVisitor implements IMediaResourceVisitor {
    List<IMediaResource> items = new LinkedList<IMediaResource>();
    public void visit(IMediaResource resource) {
        items.add(resource);
    }
    
    public List<IMediaResource> getItems() {
        return items;
    }
    
    public int getCount() {
        return items.size();
    }
}
