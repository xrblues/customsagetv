package sagex.phoenix.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import sagex.remote.SagexServlet.SageHandler;

public abstract class JSONHandler implements SageHandler {
	protected Logger log = Logger.getLogger(getClass());
	
	public static class Help {
		public String title;
		public String description;
		public Map<String, String> parameters = new HashMap<String, String>();
		public List<String> examples= new ArrayList<String>();
	}
	
	@Override
	public void handleRequest(String[] args, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			JSONUtil.writeResponse(handleService(args, req), resp);
		} catch (ServiceException se) {
			log.warn("JSON Service Failed", se);
			JSONUtil.writeError(1, "Service Failed", se,resp);
		}
	}
	
	/**
	 * Must Return a serializable object that can be serialized to JSON
	 * 
	 * @param args
	 * @param request
	 * @return
	 * @throws ServiceException
	 */
	protected abstract Object handleService(String[] args, HttpServletRequest request) throws ServiceException;
	
	/**
	 * The JSON Service should be able to render a help segment.  It should follow a stanard
	 * @param request
	 * @param resp
	 * @throws IOException
	 */
	public abstract Help getHelp(HttpServletRequest request) throws IOException;
}
