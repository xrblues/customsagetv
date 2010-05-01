package sagex.api.metadata;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import sagex.api.MediaFileAPI;
import sagex.util.ILog;
import sagex.util.LogProvider;
import sagex.util.ReflectionUtil;

/**
 * SageTV Metadata Class. This class should always contain the complete list of
 * metadata fields that work with {@link MediaFileAPI}.GetMediaFileMetadata()
 * 
 * Other classes can call addProperties() to register thier own custom metadata
 * fields
 * 
 * @author seans
 * 
 */
public class SageMetadata {
    private static final ILog log = LogProvider.getLogger(SageMetadata.class);

    /**
     * Create an empty metadata container.
     * 
     * @param klas
     * @return
     */
    public static <T extends ISageMetadata> T create(Class<T> klas) {
        List<Class> interfaces = new ArrayList<Class>();
        interfaces.add(klas);

        Object o = java.lang.reflect.Proxy.newProxyInstance(klas.getClassLoader(), interfaces.toArray(new Class[] {}), new SageMetadataProxy());

        return (T) o;
    }

    /**
     * Creates a Read/Write metadata class that is backed by a Sage MediaFile
     * object. All calls to setXXX will be immediately reflected in the Sage
     * MediaFile object
     * 
     * @param sageMediaFile
     * @param klas
     * @return
     */
    public static <T extends ISageMetadata> T create(Object sageMediaFile, Class<T> klas) {
        List<Class> interfaces = new ArrayList<Class>();
        interfaces.add(klas);
        Object o = java.lang.reflect.Proxy.newProxyInstance(klas.getClassLoader(), interfaces.toArray(new Class[] {}), new SageMediaFileMetadataProxy(sageMediaFile));
        return (T) o;
    }

    /**
     * Given the Metadata, serialize it into a Properties object that SageTV can
     * consume.
     * 
     * @param md
     *            {@link ISageMetadata} instance
     * @return {@link Properties} object
     */
    public static Properties createProperties(ISageMetadata md) {
        Properties props = new Properties();

        Class classes[] = md.getClass().getInterfaces();
        for (Class cl : classes) {
            Method[] methods = cl.getMethods();
            if (methods != null) {
                for (Method m : methods) {
                    SageProperty p = m.getAnnotation(SageProperty.class);
                    if (p != null) {
                        try {
                            if (m.getName().startsWith("is") || m.getName().startsWith("get")) {
                                if (md.isSet(p.value())) {
                                    Object val = m.invoke(md, (Object[])null);
                                    if (val != null) {
                                        if (m.getReturnType().isAssignableFrom(List.class)) {
                                            Class clType = ReflectionUtil.getGenericReturnType(m);
                                            if (clType != null) {
                                                if (clType.isAssignableFrom(ISageCastMember.class)) {
                                                    // serialize the case member
                                                    // in the list
                                                    props.put(p.value(), serializeCast((List<ISageCastMember>) val));
                                                } else {
                                                    log.warn("Skipping " + p.value() + " unhandled list element type: " + clType);
                                                }
                                            } else {
                                                log.warn("Skipping " + p.value() + " no list type.");
                                            }
                                        } else if (m.getReturnType().equals(Date.class)) {
                                            log.debug("Date: " + p.value());
                                            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                                            props.put(p.value(), f.format((Date) val));
                                        } else {
                                            props.put(p.value(), String.valueOf(val));
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.error("failed to serialize field: " + p.value(), e);
                        }
                    }
                }
            }
        }

        return props;
    }

    /**
     * Given the {@link ISageMetadata} instance, write all non null values to
     * the Sage MediaFile instance.
     * 
     * @param md
     *            {@link ISageMetadata} instance
     * @return {@link Properties} object
     */
    public static void updateMediaFile(ISageMetadata md, Object sageMediaFile) {
        Class classes[] = md.getClass().getInterfaces();
        for (Class cl : classes) {
            Method[] methods = cl.getMethods();
            if (methods != null) {
                for (Method m : methods) {
                    SageProperty p = m.getAnnotation(SageProperty.class);
                    if (p != null) {
                        try {
                            if (m.getName().startsWith("is") || m.getName().startsWith("get")) {
                                if (md.isSet(p.value())) {
                                    Object val = m.invoke(md, (Object[])null);
                                    if (val != null) {
                                        if (m.getReturnType().isAssignableFrom(List.class)) {
                                            Class clType = ReflectionUtil.getGenericReturnType(m);
                                            if (clType != null) {
                                                if (clType.isAssignableFrom(ISageCastMember.class)) {
                                                    // serialize the case member
                                                    // in the list
                                                    MediaFileAPI.SetMediaFileMetadata(sageMediaFile, p.value(), serializeCast((List<ISageCastMember>) val));
                                                } else {
                                                    log.warn("Skipping " + p.value() + " unhandled list element type: " + clType);
                                                }
                                            } else {
                                                log.warn("Skipping " + p.value() + " no list type.");
                                            }
                                        } else if (m.getReturnType().equals(Date.class)) {
                                            log.debug("Date: " + p.value());
                                            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
                                            MediaFileAPI.SetMediaFileMetadata(sageMediaFile, p.value(), f.format((Date) val));
                                        } else {
                                            MediaFileAPI.SetMediaFileMetadata(sageMediaFile, p.value(), String.valueOf(val));
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.error("failed to serialize field: " + p.value(), e);
                        }
                    }
                }
            }
        }
    }

    private static String serializeCast(List<ISageCastMember> val) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < val.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(val.get(i).getName());
        }
        return sb.toString();
    }

    /**
     * Return the SageTV Property Keys that are Defined in the given interface class
     * 
     * @param klas {@link ISageMetadata} class type
     * @return String[] of Sage Keys
     */
    public static <T extends ISageMetadata> String[] getPropertyKeys(Class<T> klas) {
        Set<String> keys = new TreeSet<String>();

        Method[] methods = klas.getMethods();
        if (methods != null) {
            for (Method m : methods) {
                SageProperty p = m.getAnnotation(SageProperty.class);
                if (p != null) {
                    keys.add(p.value());
                }
            }
        }

        return keys.toArray(new String[] {});
    }
}
