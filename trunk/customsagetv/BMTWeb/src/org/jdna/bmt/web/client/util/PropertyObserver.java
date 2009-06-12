package org.jdna.bmt.web.client.util;

public interface PropertyObserver<T> {
    public void updated(Property<T> prop);
}
