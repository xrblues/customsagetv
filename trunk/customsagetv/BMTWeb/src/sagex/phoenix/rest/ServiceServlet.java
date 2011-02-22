package sagex.phoenix.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import sagex.phoenix.Phoenix;
import sagex.phoenix.rest.JSONHandler.Help;
import sagex.remote.SagexServlet.SageHandler;

/**
 * NOTES:
 * path like /json/servicename/serviceargs/.../
 * clientid will be an agreed upon client id sent from the device to the server
 * clientid may be authenticated on the server
 */
public class ServiceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Map<String, JSONHandler> services = new HashMap<String, JSONHandler>();
    
    private transient Logger log = Logger.getLogger(this.getClass());
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServiceServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			log.debug("Request: " + req.getContextPath() + "/" + getServletName() + req.getPathInfo() + "?" + req.getQueryString());
			if (req.getPathInfo()==null) {
	        	help("A Service is requred", req, resp);
	        	return;
			}

			// url looks like
			// http://localhost:8075/phoenix-rest/json/SERVICE
			// args[0] = json
			// args[1] = serviceid
			// args[2+] = extra commands		
	        String args[] = req.getPathInfo().split("/");
	        if (args.length < 2) {
	        	help("A Service is requred", req, resp);
	        	return;
	        }

	        String service = args[1];
	        SageHandler sh = services.get(service);
	        if (sh==null) {
	        	help("Not a valid json service: " + service, req, resp);
	        	return;
	        }
	       
	        sh.handleRequest(args, req, resp);
		} catch (Throwable t) {
			log.warn("service failed", t);
			ServiceException se = new ServiceException("unknown",t.getMessage(), t);
			JSONUtil.writeResponse(se.getServiceMessage(),resp);
		}
	}


	private void help(String message, HttpServletRequest req, HttpServletResponse resp) throws IOException {
		PrintWriter p = resp.getWriter();
		
		p.println("<h1>Rest API Extensions</h1>");
		p.println("<p>These as extensions to the normal sagex-apis, that offer other services, such as browsing media items, but they require phoenix to be installed.</p>");
		
		if (message!=null) {
			p.printf("<br/><h2>Error: %s</h2><br/>", message);
		}
		
		p.println("<br/>");
		p.println("<h1>Services</h1>");
		String path = req.getContextPath() + "/json";
		for (JSONHandler j: services.values()) {
			Help h = j.getHelp(req);
			p.printf("<h2>Service: %s</h2>\n", h.title);
			p.printf("<p>%s</p>\n", h.description);
			p.println("<h3>Parameters</h3>");
			p.println("<ul>");
			for (Map.Entry me : h.parameters.entrySet()) {
				p.printf("<li><var>%s</var>: %s</li>\n", me.getKey(), me.getValue());
			}
			p.println("</ul>");

			p.println("<h3>Examples</h3>");
			p.println("<ul>");
			for (String s: h.examples) {
				p.printf("<li>%s%s</li>\n", path, s);
			}
			p.println("</ul>");
			p.println("<hr/>");
		}
		
		p.flush();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}



	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
//		try {
//			Log4jConfigurator.configure("phoenix-rest");
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		
		services.put(InfoService.ID, new InfoService());
		services.put(EchoService.ID, new EchoService());
		services.put(VFSService.ID, new VFSService());
		services.put(SystemMessageListService.ID, new SystemMessageListService());
		services.put(ViewService.ID, new ViewService());
		services.put(ClientService.ID, new ClientService());
		
		Phoenix.getInstance();
		
//		BasicConfigurator.resetConfiguration();
//        BasicConfigurator.configure();
	}
}
