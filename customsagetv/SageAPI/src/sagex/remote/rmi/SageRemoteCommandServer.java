package sagex.remote.rmi;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;

import sagex.remote.AbstractRPCHandler;
import sagex.remote.RemoteRequest;
import sagex.remote.RemoteResponse;

public class SageRemoteCommandServer implements SageRemoteCommand, Serializable {
	private static final long serialVersionUID = 5L;
	public static final String SERVER_BINDING = "SageJavaRPC";
	private static SageRemoteCommandServer instance = new SageRemoteCommandServer();

	private transient AbstractRPCHandler handler = new AbstractRPCHandler() {
	};
	
	public SageRemoteCommandServer() {
	}

	public RemoteResponse executeCommand(RemoteRequest request) {
		System.out.println("Processing Request: " + request.getCommand());
		RemoteResponse response = new RemoteResponse();
		handler.handleRPCCall(request, response);
		return response;
	}
	
	public static void startServer(Properties sinfo) {
		System.setProperty("java.rmi.server.hostname", sinfo.getProperty("server"));
		try {
			System.out.println("Starting Sage RMI Server...");
		    SageRemoteCommandServer obj = getInstance();
		    SageRemoteCommand stub = (SageRemoteCommand) UnicastRemoteObject.exportObject(obj, 0);

		    // Bind the remote object's stub in the registry
		    Registry registry = LocateRegistry.createRegistry(Integer.parseInt(sinfo.getProperty("rmi.port")));
		    registry.rebind(SERVER_BINDING, stub);

		    System.err.println("Sage Java RMI Server ready");
		} catch (Exception e) {
		    System.err.println("Server exception: " + e.toString());
		    e.printStackTrace();
		}
	}

	private static SageRemoteCommandServer getInstance() {
		return instance;
	}

}
