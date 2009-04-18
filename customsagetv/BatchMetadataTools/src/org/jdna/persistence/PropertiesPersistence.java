package org.jdna.persistence;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdna.persistence.annotations.Table;

public class PropertiesPersistence implements IPersistence {
    private static final Logger log = Logger.getLogger(PropertiesPersistence.class);
    private Properties          props;

    public PropertiesPersistence(Properties props2) {
        this.props = props2;
    }

    public void delete(Object cfg) throws Exception {
        if (cfg == null) return;

        log.debug("Removing: " + cfg.getClass().getName());
        Table cpath = (Table) cfg.getClass().getAnnotation(Table.class);
        if (cpath == null) throw new Exception("Class: " + cfg.getClass().getName() + " is not Configurable.  Missing ConfigurationPath annotation.");

        String path = "/" + cpath.name() + "/";

        Field fields[] = cfg.getClass().getDeclaredFields();

        // find the key field
        String key = null;
        for (Field f : fields) {
            org.jdna.persistence.annotations.Field c = f.getAnnotation(org.jdna.persistence.annotations.Field.class);
            // we don't store non confiurable entries
            if (c == null) continue;

            // we don't store keys
            if (c.key()) {
                f.setAccessible(true);
                key = String.valueOf(f.get(cfg));
                if (key == null) {
                    throw new Exception("Object: " + cfg.getClass().getName() + " defined a key field, but it was null for object instance: " + cfg);
                }
            }
        }

        if (key == null && cpath.requiresKey()) {
            throw new Exception("Could not remove object: " + cfg + "; Missing Key Field!");
        }

        if (key != null) {
            path = path + key;

            Pattern regex = Pattern.compile(path + ".*");
            for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
                String k = (String) e.nextElement();
                Matcher m = regex.matcher(k);
                if (m.matches()) {
                    props.remove(k);
                }
            }
        } else {
            for (Field f : fields) {
                String propKey = path;
                org.jdna.persistence.annotations.Field c = f.getAnnotation(org.jdna.persistence.annotations.Field.class);

                // we don't store non confiurable entries
                if (c == null) continue;

                f.setAccessible(true);

                // we don't store keys
                if (c.key()) {
                    continue;
                }

                // we don't store null values
                Object o = f.get(cfg);
                if (o == null) {
                    continue;
                }

                propKey = path + (c.name().equals(org.jdna.persistence.annotations.Field.USE_FIELD_NAME) ? f.getName() : c.name());
                props.remove(propKey);
            }
        }
    }

    /**
     * return all the keys groups that match the given regex. The regex must
     * have 1 group that returns the keyId. All unique keys are returned.
     * 
     * ie, /query/1/somekey=value /query/2/samekey=value
     * 
     * This would return a string array of 1 and 2.
     * 
     * @param regex
     * @return
     */
    private String[] getUniqueKeys(Pattern regex) {
        Set<String> keys = new TreeSet<String>();

        for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            Matcher m = regex.matcher(key);
            if (m.matches()) {
                keys.add(m.group(1));
            }
        }

        String[] all = keys.toArray(new String[keys.size()]);
        Arrays.sort(all);
        return all;
    }

    public <T> List<T> loadAll(Class<T> objectType) throws Exception {
        Table cpath = (Table) objectType.getAnnotation(Table.class);
        if (cpath == null) throw new Exception("Class: " + objectType.getName() + " is not Configurable.  Missing ConfigurationPath annotation.");

        Pattern p = Pattern.compile("/" + cpath.name() + "/" + "([^/]+)/.*");

        List<T> l = new ArrayList<T>();
        String keys[] = getUniqueKeys(p);

        for (int i = 0; i < keys.length; i++) {
            l.add(load(objectType, keys[i]));
        }

        return l;
    }

    public <T> T load(Class<T> objectType, String key) throws Exception {
        Table cpath = (Table) objectType.getAnnotation(Table.class);
        if (cpath == null) throw new Exception("Class: " + objectType.getName() + " is not Configurable.  Missing ConfigurationPath annotation.");
        String path = "/" + cpath.name() + "/";

        // System.out.println("Configuration path: " + path);

        T o = objectType.newInstance();

        Field fields[] = objectType.getDeclaredFields();

        for (Field f : fields) {
            String propKey = path;
            org.jdna.persistence.annotations.Field c = f.getAnnotation(org.jdna.persistence.annotations.Field.class);
            // we don't store non confiurable entries
            if (c == null) continue;

            f.setAccessible(true);
            // we don't store keys
            if (c.key()) {
                f.set(o, parseType(f.getType(), key));
                continue;
            }

            propKey = path + key + "/" + (c.name().equals(org.jdna.persistence.annotations.Field.USE_FIELD_NAME) ? f.getName() : c.name());
            if (c.map()) {
                log.debug("Attempting to load a map for: " + propKey);
                String[] keys = getUniqueKeys(Pattern.compile(propKey + "/(.*)"));
                if (keys!=null) {
                    Map<String, String> m = new HashMap<String, String>();
                    for (String k : keys) {
                        String pk = propKey + "/" + k;
                        log.debug("Attempting to load value for map entry: " + pk);
                        String propVal = getProperty(pk, null);
                        log.debug("Got a value: " + propVal);
                        if (propVal == null || propVal.trim().length() == 0) continue;
                        m.put(k, propVal);
                    }
                    f.set(o, m);
                } else {
                    log.debug("No Map Keys for: " + propKey);
                }
            } else {
                String propVal = getProperty(propKey, null);
    
                // don't bother with empty props
                if (propVal == null || propVal.trim().length() == 0) continue;
    
                // convert types
                f.set(o, parseType(f.getType(), propVal));
            }
        }

        return o;
    }

    public <T> T load(Class<T> objectType) throws Exception {
        Table cpath = (Table) objectType.getAnnotation(Table.class);
        if (cpath == null) throw new Exception("Class: " + objectType.getName() + " is not Configurable.  Missing ConfigurationPath annotation.");
        String path = "/" + cpath.name() + "/";

        // System.out.println("Configuration path: " + path);

        T o = objectType.newInstance();

        Field fields[] = objectType.getDeclaredFields();

        for (Field f : fields) {
            String propKey = path;
            org.jdna.persistence.annotations.Field c = f.getAnnotation(org.jdna.persistence.annotations.Field.class);
            // we don't store non confiurable entries
            if (c == null) continue;

            f.setAccessible(true);

            propKey = path + (c.name().equals(org.jdna.persistence.annotations.Field.USE_FIELD_NAME) ? f.getName() : c.name());
            String propVal = getProperty(propKey, null);

            // don't bother with empty props
            if (propVal == null || propVal.trim().length() == 0) continue;

            // convert types
            f.set(o, parseType(f.getType(), propVal));
        }

        return o;
    }

    private Object parseType(Class<?> f, String value) throws Exception {
        // convert types
        if (f.equals(String.class)) {
            return value;
        } else if (f.equals(int.class)) {
            return Integer.parseInt(value);
        } else if (f.equals(boolean.class)) {
            return Boolean.parseBoolean(value);
        } else if (f.equals(long.class)) {
            return Long.parseLong(value);
        } else if (f.equals(float.class)) {
            return Float.parseFloat(value);
        } else {
            throw new Exception("Property Type: " + f.getName() + " is not supported for value: " + value);
        }
    }

    public void save(Object obj) throws Exception {
        if (obj == null) return;
        log.debug("Saving: " + obj.getClass().getName());
        Table cpath = (Table) obj.getClass().getAnnotation(Table.class);
        if (cpath == null) throw new Exception("Class: " + obj.getClass().getName() + " is not Configurable.  Missing ConfigurationPath annotation.");
        String path = "/" + cpath.name() + "/";

        Field fields[] = obj.getClass().getDeclaredFields();

        // find the key field
        String key = null;
        for (Field f : fields) {
            org.jdna.persistence.annotations.Field c = f.getAnnotation(org.jdna.persistence.annotations.Field.class);
            // we don't store non confiurable entries
            if (c == null) continue;

            // we don't store keys
            if (c.key()) {
                f.setAccessible(true);
                key = String.valueOf(f.get(obj));
                if (key == null) {
                    throw new Exception("Object: " + obj.getClass().getName() + " defined a key field, but it was null for object instance: " + obj);
                }
            }
        }

        if (cpath.requiresKey() && key == null) {
            throw new Exception("Key Field cannot be null for object type: " + obj.getClass().getName());
        }

        for (Field f : fields) {
            String propKey = path;
            org.jdna.persistence.annotations.Field c = f.getAnnotation(org.jdna.persistence.annotations.Field.class);

            // we don't store non confiurable entries
            if (c == null) continue;

            f.setAccessible(true);

            // we don't store keys
            if (c.key()) {
                continue;
            }

            // we don't store null values
            Object o = f.get(obj);
            if (o == null) {
                continue;
            }

            if (key == null) {
                propKey = path + (c.name().equals(org.jdna.persistence.annotations.Field.USE_FIELD_NAME) ? f.getName() : c.name());
            } else {
                propKey = path + key + "/" + (c.name().equals(org.jdna.persistence.annotations.Field.USE_FIELD_NAME) ? f.getName() : c.name());
            }
            String propVal = String.valueOf(o);
            // encode newlines
            propVal = propVal.replaceAll("\n", "\\\\n");
            setProperty(propKey, propVal);
        }
    }

    public void dumpValues(Object obj, PrintWriter pw) throws Exception {
        if (obj == null) return;
        Table cpath = (Table) obj.getClass().getAnnotation(Table.class);
        if (cpath == null) throw new Exception("Class: " + obj.getClass().getName() + " is not Configurable.  Missing ConfigurationPath annotation.");
        String path = "/" + cpath.name() + "/";

        Field fields[] = obj.getClass().getDeclaredFields();

        // find the key field
        String key = null;
        for (Field f : fields) {
            org.jdna.persistence.annotations.Field c = f.getAnnotation(org.jdna.persistence.annotations.Field.class);
            // we don't store non confiurable entries
            if (c == null) continue;

            // we don't store keys
            if (c.key()) {
                f.setAccessible(true);
                key = String.valueOf(f.get(obj));
                if (key == null) {
                    throw new Exception("Object: " + obj.getClass().getName() + " defined a key field, but it was null for object instance: " + obj);
                }
            }
        }

        if (cpath.requiresKey() && key == null) {
            throw new Exception("Key Field cannot be null for object type: " + obj.getClass().getName());
        }

        pw.printf("#       Begin: %s\n", cpath.name());
        pw.printf("# Description: %s\n", cpath.description());

        for (Field f : fields) {
            String propKey = path;
            org.jdna.persistence.annotations.Field c = f.getAnnotation(org.jdna.persistence.annotations.Field.class);

            // we don't store non confiurable entries
            if (c == null) continue;

            f.setAccessible(true);

            // we don't store keys
            if (c.key()) {
                continue;
            }

            Object o = f.get(obj);
            if (key == null) {
                propKey = path + (c.name().equals(org.jdna.persistence.annotations.Field.USE_FIELD_NAME) ? f.getName() : c.name());
            } else {
                propKey = path + key + "/" + (c.name().equals(org.jdna.persistence.annotations.Field.USE_FIELD_NAME) ? f.getName() : c.name());
            }
            String propVal = (o == null ? "" : String.valueOf(o));

            propVal = propVal.replaceAll("\n", "\\\\n");
            pw.printf("\n# Description: %s\n", c.description());
            pw.printf("#        Type: %s\n", f.getType().getName());
            pw.printf("%s=%s\n", propKey, propVal);
        }
        pw.printf("# End: %s\n\n", cpath.name());
    }

    private void setProperty(String key, String val) {
        props.setProperty(key, val);
    }

    public String getProperty(String key, String defValue) {
        if (props == null) return defValue;
        return props.getProperty(key, defValue);
    }
}
