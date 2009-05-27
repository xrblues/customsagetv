package org.jdna.configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.ConfigurationManager;

public class GroupProxy {
    protected String groupPath = null;
    public GroupProxy() {
        Group grp = this.getClass().getAnnotation(Group.class);
        groupPath = grp.path();
    }
    
    protected void init(Object instance) {
        try {
            for (java.lang.reflect.Field f : instance.getClass().getDeclaredFields()) {
                Field fld = f.getAnnotation(Field.class);
                if (fld!=null) {
                    String name = fld.name();
                    if (name.equals(Field.USE_FIELD_NAME)) {
                        name=f.getName();
                    }
                    name = groupPath + "/" + name;
                    Method m = f.getType().getMethod("init", ConfigurationManager.class, String.class);
                    f.setAccessible(true);
                    m.invoke(f.get(instance), Phoenix.getInstance().getConfigurationManager(), name);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
    }
    
    private static Map<Class, Object> groups = new HashMap<Class, Object>();
    public static <T extends GroupProxy> T get(Class<T> cls) {
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
