package sagex.remote.minihttpd;

import java.util.Map;

public class HTTPRequest implements Request {
	private Map<String, String> args = null;
	private String path = null;

	public HTTPRequest(Map<String, String> param, String path) {
		this.args = param;
		this.path = path;
	}

	public String getParameter(String name) {
		if (args != null) {
			return (String) args.get(name);
		} else {
			return null;
		}
	}

	public String getPath() {
		return path;
	}

	public Map<String, String> getParameters() {
		return args;
	}

}
