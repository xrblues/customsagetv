package sagex.remote.minihttpd;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class HTTPResponse implements Response {
	private PrintWriter pw = null;
	private OutputStream os = null;
	private Map headers = new HashMap();
	private String contentType = "text/html";

	public HTTPResponse(OutputStream os) {
		this.os = os;
	}

	public void setHeader(String name, String val) {
		headers.put(name, val);
	}

	public Map getHeaders() {
		return headers;
	}

	public OutputStream getOutputStream() {
		return os;
	}

	public void setContentType(String mimeType) {
		contentType = mimeType;
	}

	public Object getContentType() {
		return contentType;
	}

	public PrintWriter getWriter() {
		if (pw == null) {
			pw = new PrintWriter(getOutputStream());
		}
		return pw;
	}
}
