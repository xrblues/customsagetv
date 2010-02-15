package org.jdna.bmt.web.client.util;

import java.io.Serializable;

public class Property<T> implements Serializable {
    private T value;
    private boolean visible=true;
    
    public Property() {
    }
    
    public Property(T value) {
        this.value=value;
    }
    
    public T get() {
        return value;
    }
    
    public void set(T value) {
        this.value=value;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean vis) {
        this.visible=vis;
    }
}
