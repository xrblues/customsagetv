package org.jdna.media;

import java.util.List;

public interface IMediaFile extends IMediaResource {
    public static final int TYPE_FILE = 1;

    public boolean isStacked();

    public List<IMediaResource> getParts();

    public boolean isWatched();

    public void setWatched(boolean watched);
}
