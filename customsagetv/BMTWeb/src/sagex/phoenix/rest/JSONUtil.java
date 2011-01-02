package sagex.phoenix.rest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;


public class JSONUtil {
	public static final String RESPONSE_KEY = "reply";
	private static final Logger log = Logger.getLogger(JSONUtil.class);
	
	public static void writeResponse(Object serviceMessage, HttpServletResponse resp) {
		Map reply = new HashMap();
		reply.put(RESPONSE_KEY, serviceMessage);
		Gson gson = new Gson();
		try {
			String json = gson.toJson(reply);
			if (log.isDebugEnabled()) {
				log.debug("JSON Reply: " + json);
			}
			resp.getWriter().append(json);
			resp.getWriter().flush();
		} catch (IOException e) {
			try {
				log.warn("Failed to write response", e);
				resp.sendError(500, "Failed: " + e.getMessage());
			} catch (IOException e1) {
			}
		}
	}
	
	public static void writeError(int code, String message, Throwable t, HttpServletResponse resp) {
		Map error = new HashMap();
		error.put("code", code);
		error.put("message", message);
		error.put("exception", t);
		writeResponse(error, resp);
	}
}
