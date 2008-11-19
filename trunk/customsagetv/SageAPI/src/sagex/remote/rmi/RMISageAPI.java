package sagex.remote.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import sagex.ISageAPIProvider;
import sagex.remote.RemoteObjectRef;
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
			Object rdata = resp.getData();
			if (rdata instanceof RemoteObjectRef && ((RemoteObjectRef) rdata).isArray()) {
				// we are dealing a complex remote array reference
				// we need to convert into a local array copy so the local api
				// can deal with it
				RemoteObjectRef ref = ((RemoteObjectRef) rdata);
				replyData = new Object[ref.getArraySize()];
				for (int i = 0; i < ref.getArraySize(); i++) {
					((Object[]) replyData)[i] = new RemoteObjectRef(ref, i);
				}
			} else {
				// assume the data type was normal, ie not a Sage object of any
				// type
				replyData = rdata;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return replyData;
	}

	public Object callService(String name, Object[] args) {
		return callService(null, name, args);
	}
	
	public String toString() {
		return String.format("RMISageAPI(%s,%s)",host,port);
	}
}
