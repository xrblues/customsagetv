package sagex.remote.minihttpd;

import java.io.OutputStream;
import java.io.PrintWriter;

public interface Response {
	public void setHeader(String name, String val);

	public void setContentType(String mimeType);

	public OutputStream getOutputStream();

	public PrintWriter getWriter();
}
