package sagex.api.metadata;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sagex.api.MediaFileAPI;
import sagex.api.metadata.StringList.Adapter;
import sagex.util.ILog;
import sagex.util.LogProvider;
import sagex.util.ReflectionUtil;
import sagex.util.TypesUtil;

/**
 * This Class will eventually provide a Proxy between the {@link ISageMetadata}
 * interfaces and the sagetv metadata.
 * 
 * @author seans
 * 
 */
public class SageMediaFileMetadataProxy implements InvocationHandler {
    private ILog   log           = LogProvider.getLogger(SageMediaFileMetadataProxy.class);
    private Object sageMediaFile = null;
    private Map<String, List<ISageCastMember>> lists = new HashMap<String, List<ISageCastMember>>();
    
    public SageMediaFileMetadataProxy(Object sageMediaFile) {
        this.sageMediaFile = sageMediaFile;
        log.debug("SageMediaFileMetadataProxy Proxy class created.");
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("toString".equals(method.getName())) {
            return "SageMediaFileMetadataProxy["+sageMediaFile+"]";
        }

        if ("isSet".equals(method.getName())) {
            return isSet((String) args[0]);
        }

        SageProperty md = method.getAnnotation(SageProperty.class);
        if (md == null) {
            log.warn("Missing MD annotation on method: " + method.getName());
            return null;
        }

        String name = method.getName();
        if (name.startsWith("set")) {
            if (args[0] == null) {
                MediaFileAPI.SetMediaFileMetadata(sageMediaFile, md.value(), null);
            } else {
                MediaFileAPI.SetMediaFileMetadata(sageMediaFile, md.value(), TypesUtil.toString(args[0]));
            }
            return null;
        }

        if (name.startsWith("get") || name.startsWith("is")) {
            if (method.getReturnType().isAssignableFrom(List.class)) {
                Class cl = ReflectionUtil.getGenericReturnType(method);
                if (cl.isAssignableFrom(ISageCastMember.class)) {
                    final String key = md.value();
                    List<ISageCastMember> cast = lists.get(key);
                    if (cast==null) {
                        cast=new StringList<ISageCastMember>(new Adapter<ISageCastMember>() {
                            public String fromItem(ISageCastMember el) {
                                return el.getName();
                            }

                            public String get() {
                                return MediaFileAPI.GetMediaFileMetadata(sageMediaFile, key);
                            }

                            public String getSeparator() {
                                return ";";
                            }

                            public void set(String data) {
                                MediaFileAPI.SetMediaFileMetadata(sageMediaFile, key, data);
                            }

                            public ISageCastMember toItem(String data) {
                                return new SageCastMember(data,null);
                            }
                        });
                        lists.put(key, cast);
                    }
                    return cast;
                } else {
                    log.warn("Unknown List Type: " + cl);
                }
            }
            
            return convertFromString(MediaFileAPI.GetMediaFileMetadata(sageMediaFile, md.value()), method);
        }

        // account for non standard methods
        if (args != null && args.length > 0) {
            MediaFileAPI.SetMediaFileMetadata(sageMediaFile, md.value(), String.valueOf(args[0]));
            return null;
        }

        return MediaFileAPI.GetMediaFileMetadata(sageMediaFile, md.value());
    }

    private Object convertFromString(String value, Method method) {
        return TypesUtil.fromString(value, method.getReturnType());
    }

    private Object isSet(String key) {
        return MediaFileAPI.GetMediaFileMetadata(sageMediaFile, key) != null;
    }
}
