package sagex.api.metadata;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sagex.util.ILog;
import sagex.util.LogProvider;
import sagex.util.ReflectionUtil;

public class SageMetadataProxy implements InvocationHandler {
    private ILog                log  = LogProvider.getLogger(SageMetadataProxy.class);

    private Map<String, Object> data = new HashMap<String, Object>();

    public SageMetadataProxy() {
        log.debug("SageMetadataProxy Proxy class created.");
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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
                data.remove(md.value());
            } else {
                data.put(md.value(), args[0]);
            }
            return null;
        }

        if (name.startsWith("get") || name.startsWith("is")) {
            if (method.getReturnType().isAssignableFrom(List.class)) {
                return getList(method, md);
            }
            return returnTypedObject(data.get(md.value()), method.getReturnType());
        }

        // account for non standard methods
        if (args != null && args.length > 0) {
            data.put(md.value(), args[0]);
            return null;
        }

        return returnTypedObject(data.get(md.value()), method.getReturnType());
    }

    private Object isSet(String key) {
        return data.containsKey(key);
    }

    private List getList(Method method, SageProperty md) {
        List l = (List) data.get(md.value());
        if (l == null) {
            Class cl = ReflectionUtil.getGenericReturnType(method);
            if (cl.isAssignableFrom(ISageCastMember.class)) {
                l = new ArrayList<ISageCastMember>();
                data.put(md.value(), l);
            } else {
                log.warn("Unhandled List Type: " + cl);
            }
        }
        return l;
    }

    public Object returnTypedObject(Object o, Class type) {
        if (o == null && type.isPrimitive()) {
            if (type.equals(int.class) || type.equals(long.class)) {
                return 0;
            }
            if (type.equals(float.class) || type.equals(double.class)) {
                return 0.0;
            }
            if (type.equals(boolean.class)) {
                return false;
            }
            if (type.equals(char.class) || type.equals(byte.class)) {
                return 0;
            }
        }

        return o;
    }
}
