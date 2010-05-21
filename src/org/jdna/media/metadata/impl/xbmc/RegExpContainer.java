package org.jdna.media.metadata.impl.xbmc;

public interface RegExpContainer {
    public void addRegExp(RegExp regexp);
    public RegExp[] getRegExps();
    public boolean hasRegExps();
}
