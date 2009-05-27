package org.jdna.configuration;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;

import sagex.phoenix.configuration.ConfigurationManager;

public class FieldProxy<T> {
    private ConfigurationManager cm = null;
    private T defaultValue = null;
    private String key = null;
    
    public FieldProxy(T defValue) {
       this.defaultValue = defValue;
    }
    
    public void init(ConfigurationManager cm, String key) {
        this.cm=cm;
        this.key = key;
    }
    
    public T get() {
        return (T)cm.getClientProperty(key, getDefaultValueAsString());
    }
    
    public String getString() {
        Object o = cm.getClientProperty(key, getDefaultValueAsString());
        if (o==null) return null;
        return String.valueOf(o);
    }

    public int getInt() {
        Object o = cm.getClientProperty(key, getDefaultValueAsString());
        if (o==null) return 0;
        return NumberUtils.toInt(String.valueOf(o));
    }

    public long getLong() {
        Object o = cm.getClientProperty(key, getDefaultValueAsString());
        if (o==null) return 0l;
        return NumberUtils.toLong(String.valueOf(o));
    }

    public float getFloat() {
        Object o = cm.getClientProperty(key, getDefaultValueAsString());
        if (o==null) return 0f;
        return NumberUtils.toFloat(String.valueOf(o));
    }
    
    public boolean getBoolean() {
        Object o = cm.getClientProperty(key, getDefaultValueAsString());
        if (o==null) return false;
        return BooleanUtils.toBoolean(String.valueOf(o));
    }
    
    public void set(T value) {
        cm.setClientProperty(key, String.valueOf(value));
    }
    
    public T getDefaultValue() {
        return defaultValue;
    }
    
    public String getDefaultValueAsString() {
        if (defaultValue==null) return null;
        return String.valueOf(defaultValue);
    }
}
