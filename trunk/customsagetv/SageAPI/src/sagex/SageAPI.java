package sagex;

import java.net.URI;
import java.util.Properties;

import sagex.remote.EmbeddedSageAPIProvider;
import sagex.remote.javarpc.SageAPIRemote;
import sagex.remote.rmi.RMISageAPI;
import sagex.remote.server.SimpleDatagramClient;

public class SageAPI {
	private static ISageAPIProvider provider = null;
	private static Properties providerProperties = null;
	
	private static ThreadLocal<String> uiContext = new ThreadLocal<String>();

	public static ISageAPIProvider getProvider() {
		if (provider == null) {
			// to find the provider to use
			try {
				System.out.println("SageAPI Provider is not set, will try to find the server...");
				
				// check if the sagex.SageAPI.remoteUrl is set
				String remoteUrl = System.getProperty("sagex.SageAPI.remoteUrl");
				if (remoteUrl==null) {
					Properties info = SimpleDatagramClient.findRemoteServer(5000);
					providerProperties = info;
					setProvider(new RMISageAPI(info.getProperty("server"), Integer.parseInt(info.getProperty("rmi.port"))));
				} else {
					URI u = new URI(remoteUrl);
					if ("rmi".equals(u.getScheme())) {
						setProvider(new RMISageAPI(u.getHost(), u.getPort()));
					} else {
						providerProperties = new Properties();
						providerProperties.put("server", u.getHost());
						providerProperties.put("http.port", u.getPort());
						setProvider(new SageAPIRemote(remoteUrl));
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
				throw new RuntimeException(t);
			}
		}
		return provider;
	}

	public static void setProvider(ISageAPIProvider provider) {
		System.out.println("Sage Provider Implementation: " + provider.getClass().getName() + "; " + provider.toString());
		SageAPI.provider = provider;
	}

	public static Object call(String serviceName, Object[] args) {
		String ctx = getUIContext();
		if (ctx!=null) {
			return call(ctx, serviceName, args);
		} else {
			return SageAPI.getProvider().callService(serviceName, args);
		}
	}

	public static Object call(String context, String serviceName, Object[] args) {
		System.out.println("Using Context: " + context + " for command: " + serviceName);
		return SageAPI.getProvider().callService(context, serviceName, args);
	}
	
	/**
	 * set the UI context for the current thread.  All calls within this thread will now use the provided context.
	 * 
	 * @deprecated may not be safe when embedded in a sage tv container
	 * @param context
	 */
	public static void setUIContext(String context) {
		uiContext.set(context);
	}

	/**
	 * returns the currently set UI context for this thread.
	 * 
	 * @return ui context for this thread.
	 */
	public static String getUIContext() {
		return uiContext.get();
	}
	
	/**
	 * returns true if the provider instance is a remote API instance.
	 * 
	 * @return
	 */
	public static boolean isRemote() {
		return (provider == null || !(provider instanceof EmbeddedSageAPIProvider));
	}
	
	/**
	 * Returns the provider properties.  Can be null.
	 * @return
	 */
	public static Properties getProviderProperties() {
		return providerProperties;
	}
	
	/**
	 * Sets the properties for a properties.  This is normally set automatically when a provider
	 * is created and set.
	 * @param props
	 */
	public static void setProviderProperties(Properties props) {
		providerProperties = props;
	}
}
