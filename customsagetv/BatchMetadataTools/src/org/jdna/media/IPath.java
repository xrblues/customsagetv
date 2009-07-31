package org.jdna.media;

public interface IPath extends Comparable<IPath> {
    public String toURI();
    public String getPath();
}
