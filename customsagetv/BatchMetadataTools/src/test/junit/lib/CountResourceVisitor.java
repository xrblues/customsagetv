package test.junit.lib;

import java.util.LinkedList;
import java.util.List;

import sagex.phoenix.progress.IProgressMonitor;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.IMediaResourceVisitor;

public class CountResourceVisitor implements IMediaResourceVisitor {
    List<IMediaResource> items = new LinkedList<IMediaResource>();
    
    public List<IMediaResource> getItems() {
        return items;
    }
    
    public int getCount() {
        return items.size();
    }

	public boolean visit(IMediaResource res, IProgressMonitor monitor) {
        items.add(res);
        return true;
	}
}
