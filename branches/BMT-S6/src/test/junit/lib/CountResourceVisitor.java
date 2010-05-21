package test.junit.lib;

import java.util.LinkedList;
import java.util.List;

import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.IMediaResourceVisitor;

public class CountResourceVisitor implements IMediaResourceVisitor {
    List<IMediaResource> items = new LinkedList<IMediaResource>();
    public boolean visit(IMediaResource resource) {
        items.add(resource);
        return true;
    }
    
    public List<IMediaResource> getItems() {
        return items;
    }
    
    public int getCount() {
        return items.size();
    }
}
