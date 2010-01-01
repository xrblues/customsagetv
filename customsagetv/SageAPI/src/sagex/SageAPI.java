package sagex;

import java.net.URI;
import java.util.Properties;

import sagex.remote.EmbeddedSageAPIProvider;
import sagex.remote.api.ServiceFactory;
import sagex.remote.javarpc.SageAPIRemote;
import sagex.remote.rmi.RMISageAPI;
import sagex.remote.server.SimpleDatagramClient;
import sagex.stub.NullSageAPIProvider;
import sagex.stub.StubSageAPI;

public class SageAPI {
    private static ISageAPIProvider       remoteProvider           = null;
    private static ISageAPIProvider       provider                 = null;
    private static Properties             remoteProviderProperties = null;
    private static ServiceFactory scriptingServices = null;

    static {
        try {
            scriptingServices = new ServiceFactory();
        } catch (Throwable t) {
            System.out.println("Scripting Services Disabled because: " + t.getMessage());
        }
    }

    public static ISageAPIProvider getProvider() {
        if (provider == null) {
            try {
                setProvider(new EmbeddedSageAPIProvider());
            } catch (Throwable t) {
                try {
                    // System.out.println("INFO: Attempting to set Remote API Provider...");
                    setProvider(getRemoteProvider());
                } catch (Throwable tt) {
                    System.out.println("ERROR: No Remote Provider, using Null API Provider (this is ok some times).");
                    setProvider(new NullSageAPIProvider());
                }
            }
        }
        return provider;
    }

    public static ISageAPIProvider getRemoteProvider() {
        if (remoteProvider == null) {
            // to find the provider to use
            try {
                // System.out.println("SageAPI Provider is not set, will try to find the server...");

                // check if the sagex.SageAPI.remoteUrl is set
                String remoteUrl = System.getProperty("sagex.SageAPI.remoteUrl");
                if (remoteUrl == null) {
                    Properties info = SimpleDatagramClient.findRemoteServer(5000);
                    remoteProviderProperties = info;
                    remoteProvider = (new RMISageAPI(info.getProperty("server"), Integer.parseInt(info.getProperty("rmi.port"))));
                } else {
                    URI u = new URI(remoteUrl);
                    if ("rmi".equals(u.getScheme())) {
                        remoteProvider = (new RMISageAPI(u.getHost(), u.getPort()));
                    } else if ("null".equals(u.getScheme())) {
                        remoteProvider = new NullSageAPIProvider();
                    } else if ("stub".equals(u.getScheme())) {
                        remoteProvider = new StubSageAPI();
                    } else {
                        remoteProviderProperties = new Properties();
                        remoteProviderProperties.put("server", u.getHost());
                        remoteProviderProperties.put("http.port", u.getPort());
                        remoteProvider = (new SageAPIRemote(remoteUrl));
                    }
                }
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
        return remoteProvider;
    }

    public static void setProvider(ISageAPIProvider provider) {
        // System.out.println("Sage Provider Implementation: " +
        // provider.getClass().getName() + "; " + provider.toString() +
        // "; Version: " + Version.VERSION);
        SageAPI.provider = provider;
    }

    public static Object call(String serviceName, Object[] args) {
        try {
            return SageAPI.getProvider().callService(serviceName, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object call(String context, String serviceName, Object[] args) {
        // System.out.println("Using Context: " + context + " for command: " +
        // serviceName);
        try {
            return SageAPI.getProvider().callService(context, serviceName, args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object call(UIContext context, String serviceName, Object[] args) {
        if (context == null) {
            UIContext ctx = getUIContext();
            if (ctx!=null) {
                return call(ctx, serviceName, args);
            } else {
                return call(serviceName, args);
            }
        } else {
            return call(context.getName(), serviceName, args);
        }
    }

    /**
     * Sets the current UI context that is accessible for this thread and its children.  This does not FORCE a UI to be passed, but only
     * ensures the when getUIContext() is called withing the running thread, then it will return this value.  You still need to pass
     * the UI context explicitly to any method that requires a UI context.
     * 
     * @param context
     *            - String or UIContext object
     */
    public static void setUIContext(Object context) {
        if (context instanceof String) {
            UIContext.setCurrentContext(new UIContext((String) context));
        } else if (context instanceof UIContext) {
            UIContext.setCurrentContext((UIContext) context);
        } else {
            UIContext.setCurrentContext(null);
        }
    }

    /**
     * returns the currently set UI context for this thread.
     * 
     * @return ui context for this thread. (can be null)
     */
    public static UIContext getUIContext() {
        return UIContext.getCurrentContext();
    }

    /**
     * returns true if the provider instance is a remote API instance.
     * 
     * @return
     */
    public static boolean isRemote() {
        return (getProvider() == null || !(getProvider() instanceof EmbeddedSageAPIProvider));
    }

    /**
     * Returns the provider properties. Can be null.
     * 
     * @return
     */
    public static Properties getProviderProperties() {
        return remoteProviderProperties;
    }

    /**
     * Sets the properties for a properties. This is normally set automatically
     * when a provider is created and set.
     * 
     * @param props
     */
    public static void setProviderProperties(Properties props) {
        remoteProviderProperties = props;
    }
    
    /**
     * Calls a scripting service.  Scripts are places in SAGETV_HOME/sages/services/.  These are simple javascript files.
     * 
     * @param context UI context
     * @param packageName usually the script filename (not the full directory path)
     * @param serviceName usually the function to call within the script
     * @param args string array of args
     * @return object reply, or throws a RuntimeException if the script generates and error, or if the scripting services are disabled
     */
    public Object callScript(UIContext context, String packageName, String serviceName, String[] args) {
        if (scriptingServices!=null) {
            try {
                return scriptingServices.callService((context==null)?null:context.getName(), packageName, serviceName, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Javascript Service Factory is disabled.");
    }

    /**
     * Same as other callScript() except without a UI context.  Scripts can always call SageAPI.GetUIContext() to get the current 
     * UI context as needed.
     * 
     * @param packageName
     * @param serviceName
     * @param args
     * @return
     */
    public Object callScript(String packageName, String serviceName, String[] args) {
        return callScript(null, packageName, serviceName, args);
    }
}
