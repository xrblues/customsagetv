package org.jdna.media.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;

import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;

public class MediaFile extends AbstractMediaResource implements IMediaFile {
    private boolean              stacked;
    private List<IMediaResource> parts;
    private Object               sageMediaFile;
    private Object               sageAiring;

    public MediaFile(String uri) throws URISyntaxException {
        super(uri, IMediaFile.TYPE_FILE, IMediaResource.CONTENT_TYPE_MOVIE);
    }

    public MediaFile(URIAdapter uriAdapter) {
        super(uriAdapter, IMediaFile.TYPE_FILE, IMediaResource.CONTENT_TYPE_MOVIE);
    }

    public MediaFile(URI uri) {
        super(uri, IMediaFile.TYPE_FILE, IMediaResource.CONTENT_TYPE_MOVIE);
    }

    @Override
    public int compareTo(IMediaResource o) {
        if (o.getType() == IMediaFile.TYPE_FILE) {
            return super.compareTo(o);
        } else {
            return 1;
        }
    }

    public List<IMediaResource> getParts() {
        return parts;
    }

    public boolean isStacked() {
        return stacked;
    }

    public void setStacked(boolean b) {
        stacked = b;

        if (b == true) {
            if (parts == null) {
                // setup the stacking list... with ourself as the first element
                parts = new ArrayList<IMediaResource>();
                parts.add(this);
            }
        } else {
            if (parts != null) {
                parts.clear();
                parts = null;
            }
        }
    }

    public void addStackedTitle(IMediaResource res) {
        if (!isStacked()) setStacked(true);
        parts.add(res);
    }

    public boolean isWatched() {
        if (!isStacked()) {
            return AiringAPI.IsWatched(getSageAiring());
        } else {
            boolean watched = false;
            for (IMediaResource r : getParts()) {
                if (r instanceof MediaFile) {
                    watched = AiringAPI.IsWatched(((MediaFile) r).getSageAiring());
                    if (!watched) break;
                }
            }
            return watched;
        }
    }

    public void setWatched(boolean watched) {
        if (!isStacked()) {
            if (watched) {
                AiringAPI.SetWatched(getSageAiring());
            } else {
                AiringAPI.ClearWatched(getSageAiring());
            }
        } else {
            for (IMediaResource r : getParts()) {
                if (r instanceof MediaFile) {
                    if (watched) {
                        AiringAPI.SetWatched(((MediaFile) r).getSageAiring());
                    } else {
                        AiringAPI.ClearWatched(((MediaFile) r).getSageAiring());
                    }
                }
            }
        }
    }

    protected Object getSageMediaFile() {
        if (sageMediaFile == null) sageMediaFile = MediaFileAPI.GetMediaFileForFilePath(getResourceAsFile());
        return sageMediaFile;
    }

    protected Object getSageAiring() {
        if (sageAiring == null) sageAiring = MediaFileAPI.GetMediaFileAiring(getSageMediaFile());
        return sageAiring;
    }
}
