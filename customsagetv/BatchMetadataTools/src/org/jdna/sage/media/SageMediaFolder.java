package org.jdna.sage.media;

import java.util.ArrayList;
import java.util.List;

import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceFilter;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.IMediaStackModel;

/**
 * Incomplete, but it should provide the basis of wrapping a Sage Media into the BMT VFS
 * 
 * @author seans
 *
 */
public class SageMediaFolder implements IMediaFolder {
    private List<IMediaResource> files=null;
    
    public SageMediaFolder(Object media[]) {
        if (media!=null) {
            files=new ArrayList<IMediaResource>(media.length);
            for (Object o : media) {
                files.add(new SageMediaFile(o));
            }
        }
    }
    
    public void accept(IMediaResourceVisitor visitor, boolean recurse) {
        visitor.visit(this);
        List<IMediaResource> mems = members();
        if (mems!=null) {
            for (IMediaResource r : mems) {
                r.accept(visitor);
            }
        }
    }

    public boolean contains(String string) {
        // TODO Auto-generated method stub
        return false;
    }

    public IMediaResourceFilter getFilter() {
        // TODO Auto-generated method stub
        return null;
    }

    public IMediaResource getResource(String path) {
        // TODO Auto-generated method stub
        return null;
    }

    public IMediaStackModel getStackingModel() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<IMediaResource> members() {
        return files;
    }

    public void setFilter(IMediaResourceFilter filter) {
        // TODO Auto-generated method stub
    }

    public void setStackingModel(IMediaStackModel stackingModel) {
        // TODO Auto-generated method stub
    }

    public void accept(IMediaResourceVisitor visitor) {
        accept(visitor, false);
    }

    public void copy() {
        // TODO Auto-generated method stub
    }

    public void delete() {
        // TODO Auto-generated method stub
    }

    public boolean exists() {
        return true;
    }

    public String getBasename() {
        return getName();
    }

    public int getContentType() {
        return CONTENT_TYPE_MOVIE;
    }

    public String getExtension() {
        return "";
    }

    public String getLocationUri() {
        return "sageFolder://";
    }

    public String getName() {
        return "SageMediaFolder";
    }

    public IMediaResource getParent() {
        return null;
    }

    public String getPath() {
        return null;
    }

    public String getTitle() {
        return getName();
    }

    public int getType() {
        return TYPE_FOLDER;
    }

    public boolean isReadOnly() {
        return true;
    }

    public long lastModified() {
        return 0;
    }

    public void touch() {
        for (IMediaResource m : members()) {
            m.touch();
        }
    }

    public int compareTo(IMediaResource arg0) {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
