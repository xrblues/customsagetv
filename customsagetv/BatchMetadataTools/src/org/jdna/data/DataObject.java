package org.jdna.data;

import java.util.HashMap;
import java.util.Map;

public class DataObject {
    private Map<String, Property> data = new HashMap<String, Property>();
    public Property get(String key) {
        return data.get(key);
    }
    
}
