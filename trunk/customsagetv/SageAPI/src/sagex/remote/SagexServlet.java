package sagex.remote;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sagex.SageAPI;
import sagex.api.Configuration;
import sagex.api.Global;
import sagex.remote.api.ApiHandler;
import sagex.remote.javarpc.JavaRPCHandler;
import sagex.remote.jsonrpc.JsonRPCHandler;
import sagex.remote.media.MediaHandler;
import sagex.remote.rmi.SageRemoteCommandServer;
import sagex.remote.server.DatagramListener;
import sagex.remote.server.DatagramServer;
import sagex.remote.xmlrpc.XMLRPCHandler;
import sagex.remote.xmlxbmc.XMLXBMCHandler;

public class SagexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public interface SageHandler {
		public void hanleRequest(String args[], HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
	}

	private static boolean initialized = false; 
	private static Map<String, SageHandler> sageHandlers = new HashMap<String, SageHandler>();
	private static DatagramServer udpServer = null;

	public SagexServlet() {
		System.out.println("Sage Remote API Servlet created.");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("Handling remote request: " + req.getPathInfo());
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
	public void init() throws ServletException {
		super.init();
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		if (!initialized) {
			initServices(config.getClass().getName());
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		udpServer.stopServer();
	}

	/**
	 * Initialize the Sage Remote Service Handlers
	 * @param serverType - String containing 'jetty' for jetty, or null, for nielm
	 */
	public static void initServices(String serverType) {
		initialized=true;
		
		System.out.println("Remote API Servlet initializing.");
		sagex.SageAPI.setProvider(new EmbeddedSageAPIProvider());

		// register our known handlers
		sageHandlers.put(XMLRPCHandler.SAGE_RPC_PATH, new XMLRPCHandler());
		sageHandlers.put(JavaRPCHandler.SAGE_RPC_PATH, new JavaRPCHandler());
		sageHandlers.put(JsonRPCHandler.SAGE_RPC_PATH, new JsonRPCHandler());
		sageHandlers.put(MediaHandler.SERVLET_PATH, new MediaHandler());
		sageHandlers.put(XMLXBMCHandler.SAGE_RPC_PATH, new XMLXBMCHandler());
		sageHandlers.put(ApiHandler.SAGE_RPC_PATH, new ApiHandler());

		System.out.println("Registered Handlers.");

		if (!SageAPI.isRemote()) {
			System.out.println("Configuring Remote Broadcast Services...");
			final Properties serverInfo = new Properties();
			File f = new File("sagex-api.properties");
			if (!f.exists()) {
				System.out.println("Properties not found: " + f.getAbsolutePath() + "; using defaults.");
				serverInfo.put("server", Global.GetServerAddress());
				serverInfo.put("rmi.port", "1098");
				if (serverType!=null && serverType.indexOf("jetty") != -1) {
					// jetty
					File jfile = new File("JettyStarter.properties");
					String jettyPort = "8080";
					if (jfile.exists()) {
						Properties props = new Properties();
						try {
							props.load(new FileInputStream(jfile));
							if (props.containsKey("jetty.port")) {
								jettyPort = props.getProperty("jetty.port");
							}
						} catch (Throwable e) {
							System.out.println("Wasn't able to laod the jetty properties");
						}
					}
					serverInfo.put("http.port", jettyPort);
				} else {
					// neil
					serverInfo.put("http.port", Configuration.GetProperty("nielm/webserver/port", "8080"));
				}
			} else {
			    InputStream is = null;
				try {
				    is = new FileInputStream(f);
					serverInfo.load(is);
				} catch (Exception e) {
					serverInfo.put("error", e.getMessage());
				} finally {
				    if (is !=null) {
				        try {
				            is.close();
				        } catch (Exception e) {}
				    }
				}
			}
			SageAPI.setProviderProperties(serverInfo);

			System.out.println("Sage Remote Api Info: Server: "+ serverInfo.getProperty("server") + "; Rmi Port: "+ serverInfo.getProperty("rmi.port") +"; Http Port: " + serverInfo.getProperty("http.port"));

			SageRemoteCommandServer.startServer(serverInfo);
			udpServer = new DatagramServer(DatagramServer.MULTICAST_GROUP, DatagramServer.MULTICAST_PORT, new DatagramListener() {
				public byte[] onDatagramPacketReceived(DatagramPacket packet) {
					try {
						// just ship the properties as plain text as the
						// response.
						// this is friendly to all clients that want to find out
						// where the server is located
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						serverInfo.list(new PrintStream(baos));
						baos.flush();
						return baos.toByteArray();
					} catch (Exception e) {
						e.printStackTrace();
						return "".getBytes();
					}
				}

				public void serverStarted(DatagramServer server) {
					System.out.println("Annoucing Remote Sage API Server availability....");
				}

				public void serverStopped(DatagramServer server) {
					System.out.println("Stopping Remote Sage API Server....");
				}
			});

			try {
				udpServer.startServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Sage Servlet is operating in Remote Mode.  Broadcasting disabled.");
		}
	}
}
