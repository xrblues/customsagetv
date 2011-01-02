package org.jdna.bmt.web.client.util;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class ObservableProperty<T> extends Property<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	private Set<PropertyObserver<T>> observers = new TreeSet<PropertyObserver<T>>();
    
    private T oldValue;
    public ObservableProperty(T value) {
        super(value);
        oldValue = value;
    }
    
    @Override
    public void set(T value) {
        oldValue=get();
        super.set(value);
        if (oldValue!=null && !oldValue.equals(value) || value!=null && !value.equals(oldValue)) {
            notifyChanges();
        }
    }
    
    public void addPropertyObserver(PropertyObserver<T> ob) {
        observers.add(ob);
    }
    
    private void notifyChanges() {
        for (PropertyObserver<T> po : observers) {
            po.updated(this);
        }
    }
}
