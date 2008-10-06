package sagex.remote;

import java.net.DatagramPacket;
import java.net.InetAddress;

import sagex.SageAPI;
import sagex.api.Configuration;
import sagex.api.Global;
import sagex.api.Utility;
import sagex.remote.minihttpd.DatagramServer;
import sagex.remote.minihttpd.DatagramListener;
import sagex.remote.minihttpd.HTTPD;
import sagex.remote.minihttpd.HTTPDListener;
import sagex.remote.minihttpd.SageRPCServlet;
import sagex.remote.minihttpd.ServerInfo;

public class SageRPCServerRunner implements Runnable {
	public static final String MULTICAST_GROUP = "228.5.6.7";
	public static final int MULTICAST_PORT= 9998;
	
	private HTTPD server=null;
	private DatagramServer udpServer=null;
	public void run() {
		// force the running API implementation to be the local sage api
		SageAPI.setProvider(new EmbeddedSageAPIProvider());
		
		// create the udp server that will listen for "where are you?" requests
		udpServer = new DatagramServer(MULTICAST_GROUP, MULTICAST_PORT, new DatagramListener() {
			public byte[] onDatagramPacketReceived(DatagramPacket packet) {
				try {
					ServerInfo sinfo = new ServerInfo();
					sinfo.host = InetAddress.getLocalHost().getHostAddress();
					if (sinfo.host.startsWith("127")) {
						// we use the embedded api to get the server host..
						sinfo.host = Global.GetServerAddress();
					}
					sinfo.port = server.getPort();
					return MarshalUtils.marshal(sinfo).getBytes(MarshalUtils.ENCODING);
				} catch (Exception e) {
					e.printStackTrace();
					return "".getBytes();
				}
			}

			public void serverStarted(DatagramServer server) {
				System.out.println("Annoucing RCP Server availability....");
			}

			public void serverStopped(DatagramServer server) {
			}
		});
		
		// start the rpc server
		server = new HTTPD(9999, new HTTPDListener() {
			public void serverStarted(HTTPD server) {
				System.out.println("Sage RPC Server accepting connections on port: " + server.getPort());
				try {
					udpServer.startServer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			public void serverStopped(HTTPD server) {
				System.out.println("Sage RPC Server stopped.");
				udpServer.stopServer();
			}
		});
		server.addServlet(SageRPCServlet.SAGE_RPC_PATH, new SageRPCServlet());
		server.startServer();
		
		// add hook to stop the server when sage stops
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.stopServer();
			}
		});
	}
}
