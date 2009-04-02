package org.jdna.sage.media;

import java.io.IOException;
import java.util.List;

import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceFilter;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.IMediaStackModel;
import org.jdna.media.metadata.IMediaMetadata;

public class SageMediaFolder implements IMediaFolder {
    private Object[] files = null;
    
    public SageMediaFolder(Object files[]) {
        this.files=files;
    }
    
    public void accept(IMediaResourceVisitor visitor, boolean recurse) {
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
        // TODO Auto-generated method stub
        return null;
    }

    public void setFilter(IMediaResourceFilter filter) {
        // TODO Auto-generated method stub

    }

    public void setStackingModel(IMediaStackModel stackingModel) {
        // TODO Auto-generated method stub

    }

    public void accept(IMediaResourceVisitor visitor) {
        // TODO Auto-generated method stub

    }

    public void copy() {
        // TODO Auto-generated method stub

    }

    public void delete() {
        // TODO Auto-generated method stub

    }

    public boolean exists() {
        // TODO Auto-generated method stub
        return false;
    }

    public String getBasename() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getContentType() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getExtension() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLocalBackdropUri() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLocalMetadataUri() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLocalPosterUri() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLocalSubtitlesUri() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLocationUri() {
        // TODO Auto-generated method stub
        return null;
    }

    public IMediaMetadata getMetadata() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    public IMediaResource getParent() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getPath() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getRelativePath(IMediaResource res) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getType() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isReadOnly() {
        // TODO Auto-generated method stub
        return false;
    }

    public long lastModified() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void touch() {
        // TODO Auto-generated method stub

    }

    public void updateMetadata(IMediaMetadata metadata, boolean overwrite) throws IOException {
        // TODO Auto-generated method stub

    }

    public int compareTo(IMediaResource arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

}
