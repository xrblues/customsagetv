package sagex.remote.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import sagex.ISageAPIProvider;
import sagex.remote.RemoteRequest;
import sagex.remote.RemoteResponse;

public class RMISageAPI implements ISageAPIProvider {
	private String host;
	private int port;
	
	protected RMISageAPI() {
	}

	public RMISageAPI(String host, int port) {
		this.host=host;
		this.port=port;
	}

	public Object callService(String context, String name, Object[] args) {
		RemoteRequest req = new RemoteRequest(context, name, args);
		Object replyData = null;
		try {
			Registry registry = LocateRegistry.getRegistry(host,port);
			SageRemoteCommand server = (SageRemoteCommand) registry.lookup(SageRemoteCommandServer.SERVER_BINDING);
			RemoteResponse resp = server.executeCommand(req);
			if (resp.hasError()) {
				System.out.println("Got an Error from the remote side: " + resp.getErrorCode() + "; " + resp.getErrorMessage());
				System.out.println("========== Remote Stack Dump ===========");
				System.out.println(resp.getException());
				throw new Exception(resp.getErrorMessage());
			}

			// now check from remote object references... specificlly array
			// ones, and turn those into real arrays...
			replyData = resp.getData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return replyData;
	}

	public Object callService(String name, Object[] args) {
		return callService(null, name, args);
	}
	
	public String toString() {
		return String.format("rmi://%s:%s",host,port);
	}
}
