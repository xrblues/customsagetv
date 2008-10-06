package sagex.remote.minihttpd;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HTTPDThreadHandler extends Thread {
	private boolean busy = false;
	private boolean running = false;
	private HTTPD server = null;

	public HTTPDThreadHandler(HTTPD server) {
		System.out.println("Connection Handler created.");
		this.server = server;
	}

	public void run() {
		running = true;
		while (running == true) {
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (!running)
				return;
		}
	}

	public void handleConnection(Socket connection) {
		System.out.println("Thread is handling connection...");
		busy = true;
		try {
			BufferedReader i = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			OutputStream o = connection.getOutputStream();

			String page = null;

			try {
				String line = null;
				String[] arr = null;
				Map params = null;
				Map replyHdrs = new HashMap();

				while ((line = i.readLine()).length() > 0) {
					// System.out.println("LINE: [" + line + "]");

					if (line.startsWith("GET")) {
						System.out.println("HTTPD: " + line);
						arr = line.split(" ");
						page = arr[1];
						arr = page.split("\\?");
						page = arr[0];

						if (arr.length == 2) {
							// name value pairs
							arr = arr[1].split("&");
							params = new HashMap();

							String[] nvp = null;

							for (int j = 0; j < arr.length; j++) {
								nvp = arr[j].split("=");
								params.put(java.net.URLDecoder.decode(nvp[0]), ((nvp.length == 2) ? java.net.URLDecoder.decode(nvp[1]) : ""));
							}
						}

						byte[] output = null;
						int length = 0;
						Servlet servlet = null;
						if (server.hasServlets()) {
							servlet = (Servlet) server.getServlet(page);
						}

						if (servlet != null) {
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							HTTPResponse resp = new HTTPResponse(baos);

							long st = System.currentTimeMillis();
							servlet.doGet(new HTTPRequest(params, page), resp);
							long et = System.currentTimeMillis();
							long dt = et-st;
							output = baos.toByteArray();
							replyHdrs.put("X-ServletRuntime", String.valueOf(dt));
							replyHdrs.put("Content-Type", resp.getContentType());
							length = output.length;
							replyHdrs.putAll(resp.getHeaders());
						} else {
							// no serlet... check file system
							if (server.getDocRoot() == null) {
								throw new FileNotFoundException("Page Not Found: " + page);
							}
							
							File file = new File(server.getDocRoot(), page);
							System.out.println("Attempting to get file: [" + file.getAbsolutePath() + "]");
							if (!file.exists()) {
								throw new FileNotFoundException("No File: " + file.getAbsolutePath());
							}

							length = (int) file.length();
							output = new byte[(int) file.length()];
							BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
							bis.read(output);
						}

						if (output != null) {
							
							replyHdrs.put("Content-Length", String.valueOf(length));
							o.write(("HTTP/1.0 200 OK\n").getBytes());

							Map.Entry entry = null;
							String hdr = null;
							for (Iterator h = replyHdrs.entrySet().iterator(); h.hasNext();) {
								entry = (Map.Entry) h.next();
								hdr = entry.getKey() + ": " + entry.getValue() + "\n";
								System.out.print("Reply Header: " + hdr);
								o.write(hdr.getBytes());
							}
							o.write("\n".getBytes());
							o.write(output, 0, length);
						} else {
							throw new Exception("No Response from page: " + page);
						}
					} else {
						// ignore those lines
					}
				}

				// nothing sent
				if (page == null) {
					throw new Exception("Invalid Request!");
				}
			} catch (FileNotFoundException fne) {
				o.write(("HTTP/1.0 404 ERROR\n\nPage Not Found: " + page + "\n\n").getBytes());
				fne.printStackTrace(System.out);
			} catch (Exception e) {
				o.write(("HTTP/1.0 500 ERROR\n\nServer Error: " + page + "\n\n").getBytes());
				e.printStackTrace(new PrintStream(o));
				e.printStackTrace(System.out);
			}

			o.flush();
			o.close();
		} catch (Exception e) {
		} finally {
			busy = false;
		}
	}

	public boolean isBusy() {
		return busy;
	}

	public void shutdown() {
		running = false;
	}
}
