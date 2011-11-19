package sagex.plugintools.http;

import java.io.*;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.*;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 1.7 $
 * @author $author$
 */
public class HTTPD extends Thread {
	private static int port = 9999;
	private static String docRoot = ".";
	private Socket connection;

	public HTTPD(Socket sock) {
		long st = System.currentTimeMillis();
		connection = sock;
		start();
		long et = System.currentTimeMillis();
		System.out.println("Server Request Handled in " + String.valueOf(et - st) + "ms");
	}

	public static void startServer(int port, String docRoot) {
		HTTPD.port = port;
		HTTPD.docRoot = docRoot;

		try {
			Thread server = new Thread() {
				public void run() {
					try {
						ServerSocket sock = new ServerSocket(HTTPD.port);

						while (true) {
							new HTTPD(sock.accept());
						}
					} catch (Exception e) {
						e.printStackTrace(System.out);
					}
				}
			};
			server.start();
			System.out.println("Accepting Connections on port: " + port);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public void run() {
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
								params.put(java.net.URLDecoder.decode(nvp[0]), ((nvp.length == 2) ? java.net.URLDecoder.decode(nvp[1])
										: ""));
							}
						}

						byte[] output = null;
						int length = 0;
						
						// no serlet... check file system
						if (docRoot == null) {
							throw new FileNotFoundException("The server does not have a docroot");
						}
						
						File file = new File(docRoot, page);
						System.out.println("Attempting to get file: [" + file.getAbsolutePath() + "]");
						if (!file.exists()) {
							throw new FileNotFoundException("No File: " + file.getAbsolutePath());
						}

						length = (int) file.length();
						output = new byte[(int) file.length()];
						BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
						bis.read(output);

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
							throw new Exception("No Output!!");
						}
					} else {
						// ignore those lines
					}
				}

				// nothing sent
				if (page == null) {
					throw new Exception("Either a valid page was not requested, or GET method was not used in thise request!");
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
		}
	}
}
