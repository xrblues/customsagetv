package sagex.remote.javarpc;

import java.io.PrintWriter;

import sagex.remote.minihttpd.Request;
import sagex.remote.minihttpd.Response;
import sagex.remote.minihttpd.Servlet;

/**
 * Simple Mirco Server Servlet that handles the java rpc request and delegates
 * to the JavaRPCHandler.
 * 
 * @author seans
 * 
 */
public class SageRPCServlet implements Servlet {
	public static final String SAGE_RPC_PATH = "/sagex/rpcJava";
	public static final String CMD_ARG = "request";

	public static JavaRPCHandler handler = new JavaRPCHandler();

	public SageRPCServlet() {
		System.out.println("Sage Java RPC Servlet Created.");
	}

	public void doGet(Request req, Response res) throws Exception {
		String payload = req.getParameter(CMD_ARG);
		res.setContentType("text/plain");
		PrintWriter pw = res.getWriter();
		pw.print(handler.handleRPCCall(payload));
		pw.flush();
	}
}
