package sagex.phoenix.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Will echo back a request paramater called 'msg'.  Used to test server connectivity.
 * 
 * @author seans
 */
public class EchoService extends JSONHandler {
	public static final String ID = "echo";
	private Help help;

	public EchoService() {
		help = new Help();
		help.title = ID;
		help.description = "A test service used to echo back a message that is passed. Used to test if the services are available.";
		help.parameters.put("msg", "Message to echo");
		help.examples.add("/echo?msg=hello");
	}

	@Override
	public Object handleService(String[] args, HttpServletRequest request) throws ServiceException {
		Map<String, String> reply = new HashMap<String, String>();
		reply.put("msg", request.getParameter("msg"));
		return reply;
	}

	@Override
	public Help getHelp(HttpServletRequest request) throws IOException {
		return help;
	}
}
