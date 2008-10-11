package sagex.remote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import sagex.ISageAPIProvider;
import sagex.remote.javarpc.SageRPCServlet;
import sagex.remote.minihttpd.ServerInfo;
import sagex.remote.minihttpd.SimpleDatagramClient;

public class SageAPIRemote implements ISageAPIProvider {
	private String server;
	private int port;
	private String baseUrl;

	public SageAPIRemote() {
		try {
			ServerInfo si = findRemoteServer(5000);
			updateServer(si.host, si.port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SageAPIRemote(String server, int port) {
		updateServer(server, port);
	}

	private void updateServer(String server, int port) {
		this.server = server;
		this.port = port;
		this.baseUrl = String.format("http://%s:%s%s", server, port, SageRPCServlet.SAGE_RPC_PATH);
	}

	public Object callService(String context, String name, Object[] args) {
		// Create the url to the call
		Object replyData = null;
		try {
			String urlStr = baseUrl;
			urlStr += ("?" + SageRPCServlet.CMD_ARG + "=" + java.net.URLEncoder.encode(MarshalUtils.marshal(new RemoteRequest(context, name, args)), MarshalUtils.ENCODING));

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

	public String getServer() {
		return server;
	}

	public int getPort() {
		return port;
	}

	public String toString() {
		return "sage://" + server + ":" + port + "/";
	}

	public static ServerInfo findRemoteServer(long timeout) throws Exception {
		SimpleDatagramClient<ServerInfo> client = new SimpleDatagramClient<ServerInfo>();
		ServerInfo info = client.send("where are you?", SageRPCServerRunner.MULTICAST_GROUP, SageRPCServerRunner.MULTICAST_PORT, timeout);
		return info;
	}

	public Object callService(String name, Object[] args) {
		return callService(null, name, args);
	}
}
