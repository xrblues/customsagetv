package org.jdna.bmt.web.client.ui.input;

public class NVP<T> {
    private T value;
    private String name;
    
    public NVP(String name, T value) {
        this.name=name;
        this.value=value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
