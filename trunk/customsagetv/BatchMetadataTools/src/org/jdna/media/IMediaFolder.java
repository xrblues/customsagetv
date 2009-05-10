package org.jdna.media;

import java.util.List;

public interface IMediaFolder extends IMediaResource {
    public List<IMediaResource> members();
    public IMediaResource getResource(String path);
    public void accept(IMediaResourceVisitor visitor, boolean recurse);
}
