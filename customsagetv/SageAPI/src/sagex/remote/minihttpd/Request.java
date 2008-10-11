package sagex.remote.minihttpd;

import java.util.Map;

public interface Request {
	public Map<String, String> getParameters();

	public String getParameter(String name);

	public String getPath();
}
