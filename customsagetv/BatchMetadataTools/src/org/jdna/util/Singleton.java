package org.jdna.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Pattern that defers the Singleton instance until it is needed.
 * 
 * Objects much have a default contructor.
 * 
 * @author seans
 *
 */
public class Singleton {
    private static Map<Class, Object> groups = new HashMap<Class, Object>();
    
    public static <T extends Object> T get(Class<T> cls) {
        T group = (T) groups.get(cls);
        if (group==null) {
            try {
                group = cls.newInstance();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            groups.put(cls, group);
        }
        return group;
    }
}
