package org.jdna.media;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class VirtualMediaFolder extends AbstractMediaFolder {
    private long lastUpdated = 0;
    
    public VirtualMediaFolder(String uri) throws URISyntaxException {
        this(new URI(uri));
    }
    
    public VirtualMediaFolder(URI uri) {
        super(uri);
    }
    
    public void addResources(List<IMediaResource> resources) {
        members().addAll(resources);
    }

    @Override
    protected void deleteFolder() {
        // do nothing
    }

    @Override
    protected void loadMembers() {
        // do nothing.. maybe override by sub classes
    }

    @Override
    protected void touchFolder() {
        lastUpdated=System.currentTimeMillis();
    }

    public IMediaResource getResource(String path) {
        // do nothing
        return null;
    }

    public boolean exists() {
        return false;
    }

    public IMediaResource getParent() {
        // virtual folders do not have parents
        return null;
    }

    public boolean isReadOnly() {
        return false;
    }

    public long lastModified() {
        return lastUpdated;
    }

    @Override
    public void addMember(IMediaResource res) {
        super.addMember(res);
    }
}
