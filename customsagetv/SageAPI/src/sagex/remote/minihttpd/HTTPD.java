package sagex.remote.minihttpd;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple HTTP Server.
 * 
 * @version $Revision: 1.7 $
 * @author $author$
 */
public class HTTPD {
	private List<HTTPDThreadHandler> handlers = new ArrayList<HTTPDThreadHandler>();
	private Map<String, Servlet> servlets = new HashMap<String, Servlet>();
	private int port = 9999;
	private File docRoot = null;
	private boolean running = false;
	private int threads = 4;
	private ServerSocket sock;
	private HTTPDListener serverListener = null;

	public HTTPD(int port) {
		this(port, null);
	}

	public HTTPD(int port, HTTPDListener listener) {
		this.port = port;
		this.serverListener = listener;

		// create the connection pool
		for (int i = 0; i < threads; i++) {
			HTTPDThreadHandler h = new HTTPDThreadHandler(this);
			h.start();
			handlers.add(h);
		}
	}

	public void addServlet(String path, Servlet servlet) {
		servlets.put(path, servlet);
	}

	public void setDocRoot(File path) {
		this.docRoot = path;
	}

	public void startServer() {
		final HTTPD This = this;
		try {
			Thread server = new Thread() {
				public void run() {
					try {
						running = true;
						sock = new ServerSocket(port);

						while (running) {
							System.out.println("Waiting for connection....");
							Socket s = sock.accept();
							if (!running)
								return;
							System.out.println("Have a connection... handling...");
							boolean handled = false;
							for (HTTPDThreadHandler h : handlers) {
								if (!h.isBusy()) {
									handled = true;
									h.handleConnection(s);
								}
							}
							if (!handled) {
								System.out.println("Creating new Connection Handler...");
								HTTPDThreadHandler h = new HTTPDThreadHandler(This);
								handlers.add(h);
								h.handleConnection(s);
							}
						}
					} catch (Exception e) {
						e.printStackTrace(System.out);
					}
				}
			};
			server.start();
			if (serverListener != null)
				serverListener.serverStarted(this);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public boolean hasServlets() {
		return servlets != null && servlets.size() > 0;
	}

	public Servlet getServlet(String page) {
		Servlet s = servlets.get(page);
		if (s == null) {
			// if there isn't an explicit servlet, then try to find the nearest
			// match...
			for (Map.Entry<String, Servlet> e : servlets.entrySet()) {
				if (page.startsWith(e.getKey())) {
					s = e.getValue();
					break;
				}
			}
		}
		return s;
	}

	public File getDocRoot() {
		return docRoot;
	}

	public int getPort() {
		return port;
	}

	public int getThreadCount() {
		return handlers.size();
	}

	public void stopServer() {
		for (HTTPDThreadHandler h : handlers) {
			h.shutdown();
		}
		running = false;
		if (sock != null) {
			try {
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (serverListener != null)
			serverListener.serverStopped(this);
	}
}
