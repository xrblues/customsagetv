package org.jdna.sage.media;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

import org.jdna.media.IMediaResource;

public class SageMediaList extends AbstractList<IMediaResource> implements List<IMediaResource> {
    private Object[] mediaFiles = null;
    private Object[] sageFile = null;
    
    public SageMediaList(Object files[]) {
        this.mediaFiles=files;
        if (mediaFiles!=null) {
            sageFile = new Object[mediaFiles.length];
        }
    }
    
    @Override
    public IMediaResource get(int i) {
        if (mediaFiles==null) throw new IndexOutOfBoundsException("List is Empty;  Index out of bounds: " + i);
        if (i<0) throw new IndexOutOfBoundsException("Index out of bounds: " + i);
        if (i>mediaFiles.length) throw new IndexOutOfBoundsException("Index out of bounds: " + i);
        Object o = sageFile[i];
        if (o==null) {
            o=mediaFiles[i];
            o = new SageMediaFile(o);
            sageFile[i] = o;
        }
        return (IMediaResource) o;
    }

    @Override
    public int size() {
        if (mediaFiles==null) return 0;
        return mediaFiles.length;
    }

    public boolean add(IMediaResource arg0) {
        throw new UnsupportedOperationException("Cannot Add to a Sage Media File List");
    }

    public void add(int arg0, IMediaResource arg1) {
        throw new UnsupportedOperationException("Cannot Add to a Sage Media File List");
    }

    public boolean addAll(Collection<? extends IMediaResource> arg0) {
        return false;
    }

    public boolean addAll(int arg0, Collection<? extends IMediaResource> arg1) {
        return false;
    }

    public boolean containsAll(Collection<?> arg0) {
        return false;
    }

    public boolean removeAll(Collection<?> arg0) {
        return false;
    }

    public boolean retainAll(Collection<?> arg0) {
        return false;
    }

    public IMediaResource set(int arg0, IMediaResource arg1) {
        throw new UnsupportedOperationException("Cannot Set to a Sage Media File List");
    }

    public <T> T[] toArray(T[] arg0) {
        if (mediaFiles==null) return arg0;
        
        for (int i=0;i<mediaFiles.length;i++) {
            arg0[i] = (T) get(i);
        }
        return arg0;
    }
}
