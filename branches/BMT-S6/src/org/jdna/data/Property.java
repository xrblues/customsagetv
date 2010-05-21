package org.jdna.data;

import org.apache.commons.lang.math.NumberUtils;

public class Property {
    private String value = null;
    
    public String value() {
        return value;
    }
    
    public void value(String value) {
        this.value=value;
    }

    public void value(Object value) {
        value(String.valueOf(value));
    }

    public int intValue() {
        return NumberUtils.toInt(value);
    }
    
    public long longValue() {
        return NumberUtils.toLong(value);
    }

    public float floatValue() {
        return NumberUtils.toFloat(value);
    }

    public double doubleValue() {
        return NumberUtils.toDouble(value);
    }
}
