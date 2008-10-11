package sagex.remote.xmlrpc;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import sagex.remote.minihttpd.Request;
import sagex.remote.minihttpd.Response;
import sagex.remote.minihttpd.Servlet;

/**
 * Simple Mirco Server Servlet that handles the xml rpc request and delegates to
 * the XmlRPCHandler.
 * 
 * @author seans
 * 
 */
public class XMLRPCServlet implements Servlet {
	public static final String SAGE_RPC_PATH = "/sagex/rpcXml";

	public static XMLRPCHandler handler = new XMLRPCHandler();

	public XMLRPCServlet() {
		System.out.println("Sage Xml RPC Servlet Created.");
	}

	public void doGet(Request req, Response res) throws Exception {
		String path = req.getPath();

		System.out.println("Xml Rpc Command: " + path);

		// 0 - null
		// 1 - sagex
		// 2 - rcpXml
		// 3 - api
		// 4 - command
		// 5+ - args
		String parts[] = path.split("/");

		for (int i = 0; i < parts.length; i++) {
			System.out.printf("part[%s]=%s\n", i, parts[i]);
		}

		res.setContentType("text/xml");
		PrintWriter pw = res.getWriter();

		// xml rpc commands come in the form
		// /API/Command/Arg1/Arg2/Arg3/context:123
		// or
		// /API/Command?1=Arg1&2=Arg2&3=Arg3&context=123

		String context = req.getParameter("context");
		List<String> args = new ArrayList<String>();
		if (parts.length > 5) {
			// extra args or context
			for (int i = 5; i < parts.length; i++) {
				if (parts[i].startsWith("context")) {
					String cargs[] = parts[i].split(":");
					context = cargs[1];
				} else {
					args.add(parts[i]);
				}
			}
		} else {
			// process reqular args
			for (int i = 1; i < 99; i++) {
				String v = req.getParameter(String.valueOf(i));
				if (v == null)
					break;
				args.add(v);
			}
		}

		pw.print(handler.handleRPCCall(parts[3], parts[4], context, args.toArray(new String[args.size()])));
		pw.flush();
	}
}
