package sagex;

import sagex.remote.EmbeddedSageAPIProvider;
import sagex.remote.SageAPIRemote;
import sagex.remote.minihttpd.ServerInfo;

public class SageAPI {
	private static ISageAPIProvider provider = null;

	public static ISageAPIProvider getProvider() {
		if (provider == null) {
			// to find the provider to use
			try {
				System.out.println("SageAPI Provider is not set, will try to find the server...");
				ServerInfo info = SageAPIRemote.findRemoteServer(5000);
				setProvider(new SageAPIRemote(info.host, info.port));
			} catch (Throwable t) {
				setProvider(new EmbeddedSageAPIProvider());
			}
		}
		return provider;
	}

	public static void setProvider(ISageAPIProvider provider) {
		System.out.println("Sage Provider Implementation: " + provider.getClass().getName() + "; " + provider.toString());
		SageAPI.provider = provider;
	}

	public static Object call(String serviceName, Object[] args) {
		// TODO: Check for context, and if so, then call context API
		return SageAPI.getProvider().callService(serviceName, args);
	}

	public static Object call(String context, String serviceName, Object[] args) {
		System.out.println("Using Context: " + context + " for command: " + serviceName);
		return SageAPI.getProvider().callService(context, serviceName, args);
	}
}
