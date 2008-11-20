package sagex.remote.javarpc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import sagex.ISageAPIProvider;
import sagex.remote.MarshalUtils;
import sagex.remote.RemoteObjectRef;
import sagex.remote.RemoteRequest;
import sagex.remote.RemoteResponse;
import sagex.remote.SagexServlet;
import sagex.remote.server.ServerInfo;
import sagex.remote.server.SimpleDatagramClient;

public class SageAPIRemote implements ISageAPIProvider {
	private String rpcUrl = null;
	
	public SageAPIRemote(String rpcUrl) {
		this.rpcUrl=rpcUrl;
	}

	public Object callService(String context, String name, Object[] args) {
		// Create the url to the call
		Object replyData = null;
		try {
			String urlStr = rpcUrl;
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches (false);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			StringBuilder dataBuf = new StringBuilder();
			String reqData = MarshalUtils.marshal(new RemoteRequest(context, name, args));
			dataBuf.append(JavaRPCHandler.CMD_ARG).append("=").append(java.net.URLEncoder.encode(reqData,MarshalUtils.ENCODING));
			conn.setRequestProperty("Content-Length", String.valueOf(dataBuf.length()));
			
			//System.out.println("Encoded Data: " + dataBuf.toString().replace('&', '\n'));
			
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
	        wr.write(dataBuf.toString().getBytes());
	        wr.flush();
	        wr.close();
	        
	        HttpURLConnection c = (HttpURLConnection)conn;
	        if (c.getResponseCode()!= 200) {
	        	System.out.printf("Http Error From Remote Server; Code %s: %s\n", c.getResponseCode(), c.getResponseMessage());
	        	throw new RuntimeException(String.format("Remote API Failed with error from server; Code: %s: %s", c.getResponseCode(), c.getResponseMessage() ));
	        }
	        
	        System.out.printf("Http Server Response: %s: %s\n", c.getResponseCode(), c.getResponseMessage());
			
			BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String data = r.readLine();
			if (data == null) {
				throw new Exception("Response was null... not good.");
			}
			r.close();
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
			e.printStackTrace();
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

	public Object callService(String name, Object[] args) {
		return callService(null, name, args);
	}
}
