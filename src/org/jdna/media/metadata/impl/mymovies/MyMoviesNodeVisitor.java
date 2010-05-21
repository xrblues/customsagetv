package org.jdna.media.metadata.impl.mymovies;

import org.w3c.dom.Element;

public interface MyMoviesNodeVisitor {
    public void visitMovie(Element el);
}
