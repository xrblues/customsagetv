package org.jdna.media;

import java.net.URI;

public abstract class AbstractMediaResource implements IMediaResource {
    private URI uri = null;
    
    public AbstractMediaResource(URI uri) {
        this.uri = uri;
    }
    
    public String getTitle() {
        String name = getBasename();
        if (name != null) return name.replaceAll("[^A-Za-z0-9']", " ");
        return name;
    }

    public int compareTo(IMediaResource o) {
        return this.getLocationUri().compareTo(o.getLocationUri());
    }

    public String getExtension() {
        String name = getName();
        if (name == null) return null;
        int p = name.lastIndexOf('.');
        if (p != -1) {
            return name.substring(p + 1);
        } else {
            return null;
        }
    }

    public String getBasename() {
        String name = getName();
        if (name == null) return null;
        int p = name.lastIndexOf('.');
        if (p != -1) {
            return name.substring(0, p);
        } else {
            return name;
        }
    }

    public void accept(IMediaResourceVisitor visitor) {
        visitor.visit(this);
    }

    public URI getLocationUri() {
        return uri;
    }
    
    public String getName() {
        String u = getLocationUri().getPath();
        if (u==null) return null;
        if (u.endsWith("/")) {
            u = u.substring(0, u.length()-1);
        }
        
        int l = u.lastIndexOf("/");
        if (l==-1) return null;
        return u.substring(l+1);
    }
}
