package sagex.remote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import sagex.ISageAPIProvider;
import sagex.remote.javarpc.JavaRPCHandler;
import sagex.remote.server.ServerInfo;
import sagex.remote.server.SimpleDatagramClient;

public class SageAPIRemote implements ISageAPIProvider {
	private String rpcUrl;

	public SageAPIRemote() {
		try {
			ServerInfo si = findRemoteServer(5000);
			this.rpcUrl = si.baseServerUrl;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SageAPIRemote(String rpcUrl) {
		this.rpcUrl = rpcUrl;
	}

	public Object callService(String context, String name, Object[] args) {
		// Create the url to the call
		Object replyData = null;
		try {
			String urlStr = rpcUrl;
			urlStr += ("?" + JavaRPCHandler.CMD_ARG + "=" + java.net.URLEncoder.encode(MarshalUtils.marshal(new RemoteRequest(context, name, args)), MarshalUtils.ENCODING));

			// System.out.println("Request: " + urlStr);

			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.connect();
			BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String data = r.readLine();
			if (data == null) {
				throw new Exception("Response was null... not good.");
			}
			RemoteResponse resp = (RemoteResponse) MarshalUtils.unmarshal(data);
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
			throw new RuntimeException("Failed to call command: " + name, e);
		}
		return replyData;
	}

	public String getServerUrl() {
		return rpcUrl;
	}

	public String toString() {
		return rpcUrl;
	}

	public static ServerInfo findRemoteServer(long timeout) throws Exception {
		SimpleDatagramClient<ServerInfo> client = new SimpleDatagramClient<ServerInfo>();
		ServerInfo info = client.send("where are you?", SagexServlet.MULTICAST_GROUP, SagexServlet.MULTICAST_PORT, timeout);
		return info;
	}

	public Object callService(String name, Object[] args) {
		return callService(null, name, args);
	}
}
