package sagex.remote;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sagex.api.Global;
import sagex.remote.javarpc.JavaRPCHandler;
import sagex.remote.jsonrpc.JsonRPCHandler;
import sagex.remote.media.MediaHandler;
import sagex.remote.rmi.SageRemoteCommandServer;
import sagex.remote.server.DatagramListener;
import sagex.remote.server.DatagramServer;
import sagex.remote.server.ServerInfo;
import sagex.remote.xmlrpc.XMLRPCHandler;

public class SagexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public interface SageHandler {
		public void hanleRequest(String args[], HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
	}

	private static Map<String, SageHandler> sageHandlers = new HashMap<String, SageHandler>();
	private DatagramServer udpServer = null;

	public SagexServlet() {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Handling remote request.");
		try {
			// /command/arg1/arg2/.../
			// 0 -
			// 1 - command
			// 2 - arg1
			String args[] = req.getPathInfo().split("/");
			if (args == null || args.length < 2) {
				resp.sendError(404, "No Sage Handler Specified.");
				return;
			}

			SageHandler sh = sageHandlers.get(args[1]);
			if (sh == null) {
				resp.sendError(404, "Sage Handle: " + args[1] + " not found!");
				return;
			}
			sh.hanleRequest(args, req, resp);
		} catch (Throwable t) {
			log("Failed to process Sage Handler!", t);
			resp.sendError(500, "Sage Servlet Failed: " + t.getMessage());
		}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		sagex.SageAPI.setProvider(new EmbeddedSageAPIProvider());

		// register our known handlers
		sageHandlers.put(XMLRPCHandler.SAGE_RPC_PATH, new XMLRPCHandler());
		sageHandlers.put(JavaRPCHandler.SAGE_RPC_PATH, new JavaRPCHandler());
        sageHandlers.put(JsonRPCHandler.SAGE_RPC_PATH, new JsonRPCHandler());
		sageHandlers.put(MediaHandler.SERVLET_PATH, new MediaHandler());

		System.out.println("Registered Handlers.");

		final ServerInfo sinfo = new ServerInfo();
		sinfo.host = Global.GetServerAddress();
		sinfo.port = 1098;
		sinfo.url = "rmi://"+sinfo.host + ":" + sinfo.port;

		System.out.println("RMI Server: "+ sinfo.url);
		
		SageRemoteCommandServer.startServer(sinfo);
		udpServer = new DatagramServer(DatagramServer.MULTICAST_GROUP, DatagramServer.MULTICAST_PORT, new DatagramListener() {
			public byte[] onDatagramPacketReceived(DatagramPacket packet) {
				try {
					return MarshalUtils.marshal(sinfo).getBytes(MarshalUtils.ENCODING);
				} catch (Exception e) {
					e.printStackTrace();
					return "".getBytes();
				}
			}

			public void serverStarted(DatagramServer server) {
				System.out.println("Annoucing Remote Sage API Server availability....");
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
