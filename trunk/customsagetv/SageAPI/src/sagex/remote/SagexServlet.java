package sagex.remote;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sagex.api.Global;
import sagex.remote.javarpc.JavaRPCHandler;
import sagex.remote.media.MediaHandler;
import sagex.remote.server.DatagramListener;
import sagex.remote.server.DatagramServer;
import sagex.remote.server.ServerInfo;
import sagex.remote.xmlrpc.XMLRPCHandler;
import sagex.stub.StubSageAPI;

public class SagexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final String MULTICAST_GROUP = "228.5.6.7";
	public static final int MULTICAST_PORT = 9998;
	
	public interface SageHandler {
		public void hanleRequest(String args[], HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
	}
	
	private static Map<String, SageHandler> sageHandlers = new HashMap<String, SageHandler>();
	private DatagramServer udpServer = null;

	public SagexServlet() {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// /command/arg1/arg2/.../
		// 0 -
		// 1 - command
		// 2 - arg1
		String args[] = req.getPathInfo().split("/");
		if (args==null|| args.length<2) {
			resp.sendError(404, "No Sage Handler Specified.");
			return;
		}
		
		SageHandler sh  = sageHandlers.get(args[1]);
		if (sh==null) {
			resp.sendError(404, "Sage Handle: " + args[1] + " not found!");
			return;
		}
		try {
			sh.hanleRequest(args, req, resp);
		} catch (Exception e) {
			log("Failed to process Sage Handler!", e);
			resp.sendError(500, "Sage Servlet Failed: " + e.getMessage());
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		// for testing, we point to the remote sage server
		// sagex.SageAPI.setProvider(new SageAPIRemote("mediaserver",9999));
		// sagex.SageAPI.setProvider(new StubSageAPI());
		
		// set the real sage provider for our requests...
		sagex.SageAPI.setProvider(new EmbeddedSageAPIProvider());
		
		
		// register our known handlers
		sageHandlers.put(XMLRPCHandler.SAGE_RPC_PATH, new XMLRPCHandler());
		sageHandlers.put(JavaRPCHandler.SAGE_RPC_PATH, new JavaRPCHandler());
		sageHandlers.put(MediaHandler.SERVLET_PATH, new MediaHandler());
		
		System.out.println("Registered Handlers.");
		// todo: Start the UDP Listener so that we can find our self....
		// use servlet that can publish it's location
		// issue under nielm's server... we need to hardcode
		// todo: find a beter way to handle this 

		final ServerInfo sinfo = new ServerInfo();
		try {
			sinfo.host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
		}
		if (sinfo.host==null || sinfo.host.startsWith("127")) {
			// we use the embedded api to get the server host..
			sinfo.host = Global.GetServerAddress();
		}
		
		sinfo.port = Integer.parseInt(sagex.api.Configuration.GetProperty("nielm/webserver/port", "8080"));
		sinfo.baseServerUrl = String.format("http://%s:%s%s",sinfo.host, sinfo.port, sagex.api.Configuration.GetProperty("sagex/rpcJava", "/sagex/rpcJava"));
		System.out.println("Java Rpc Url: " + sinfo.baseServerUrl);
		udpServer = new DatagramServer(MULTICAST_GROUP, MULTICAST_PORT, new DatagramListener() {
			public byte[] onDatagramPacketReceived(DatagramPacket packet) {
				try {
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
		try {
			udpServer.startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		udpServer.stopServer();
	}
}
