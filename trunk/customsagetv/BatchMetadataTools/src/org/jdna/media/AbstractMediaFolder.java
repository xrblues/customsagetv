package org.jdna.media;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class AbstractMediaFolder extends AbstractMediaResource implements IMediaFolder {
    private static final Logger log =  Logger.getLogger(AbstractMediaFolder.class);
    protected List<IMediaResource> members = new LinkedList<IMediaResource>();
    private boolean loaded = false;
    
    public AbstractMediaFolder(IPath uri) {
        super(uri);
    }
    
    @Override
    public void accept(IMediaResourceVisitor visitor) {
        this.accept(visitor, true);
    }

    public void accept(IMediaResourceVisitor visitor, boolean recurse) {
        visitor.visit(this);
        List<IMediaResource> m = members();
        for (IMediaResource r : m) {
            if (recurse || r.getType() == IMediaResource.Type.File) {
                r.accept(visitor);
            }
        }
    }
    
    public IMediaFolder filter(IMediaResourceFilter filter) {
        try {
            VirtualMediaFolder mf = new VirtualMediaFolder("filter:/" + filter.toString());
            List<IMediaResource> m = members();
            for (IMediaResource r : m) {
                if (filter.accept(r)) {
                    mf.addMember(r);
                }
            }
            return mf;
        } catch (Throwable e) {
            log.error("Failed to create filtered view!", e);
            return null;
        }
    }

    public void delete() {
        for (IMediaResource r : members()) {
            r.delete();
        }
        deleteFolder();
    }


    public Type getType() {
        return Type.Folder;
    }

    public void touch() {
        for (IMediaResource r : members()) {
            r.touch();
        }
        touchFolder();
    }

    protected abstract void touchFolder();
    protected abstract void deleteFolder();
    protected abstract void loadMembers();
    
    protected void addMember(IMediaResource res) {
        loaded=true;
        members.add(res);
    }

    public List<IMediaResource> members() {
        if (!loaded ) {
            loadMembers();
            loaded=true;
        }
        return members;
    }
}
