package org.jdna.sagetv.networkencoder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.text.MessageFormat;

import org.apache.log4j.Logger;

/**
 * Handler for incomming Network Encoder commands.
 * 
 * @author seans
 * 
 */
public class CommandHandler {
	private static final Logger logger = Logger.getLogger(CommandHandler.class);
	private Socket socket = null;
	private INetworkEncoder encoder = null;

	public CommandHandler(Socket socket, INetworkEncoder encoder) {
		this.socket = socket;
		this.encoder = encoder;
		handleIt();
	}

	private void write(String msg, DataOutputStream dos) throws Exception {
		logger.debug("Sending: " + msg);
		dos.write(msg.getBytes("ISO8859_1"));
		dos.write("\r\n".getBytes("ISO8859_1"));
	}

	public void handleIt() {
		DataOutputStream dos = null;
		DataInputStream dis = null;

		try {
			dos = new DataOutputStream(new BufferedOutputStream(socket
					.getOutputStream()));
			dis = new DataInputStream(new BufferedInputStream(socket
					.getInputStream()));

			//while (true) {
				// only read if there is data...
				// prevents socket timeouts...
				// NOTE: commented out because for some reason dis.available
				// wasn't
				// returning >1 when there were bytes in the stream.
				// maybe should try socket input stream directly
				// while(dis.available()<=0) {
				// Thread.yield();
				// }

				String s = readCommandString(dis);
				if (s==null) {
				    return;
				}

				//logger.debug(MessageFormat.format("Command: [{0}]", s));
				try {
					if (s.equals("VERSION")) {
						write("3.0", dos);
					} else if (s.equals("NOOP")) {
						write("OK", dos);
					} else if (s.startsWith(StartCommand.CMD_NAME + " ")) {
						try {
							StartCommand sc = new StartCommand(s);
							encoder.start(sc);
							logger.info("Tuned Channel for Sage COmmand: " + s);
							write("OK", dos);
                            logger.info("Sending back OK for: " + s);
						} catch (Exception ex) {
							logger.error("StartCommand Error", ex);
							write("ERROR Device Load Failed", dos);
						}
					} else if (s.startsWith(StopCommand.CMD_NAME + " ")) {
						StopCommand sc = new StopCommand(s);
						encoder.stop(sc);
						write("OK", dos);
					} else if (s.startsWith(GetFileSizeCommand.CMD_NAME)) {
						if (!encoder.isActive()) {
							write("ERROR Tuner not tuned", dos);
						} else {
							GetFileSizeCommand c = new GetFileSizeCommand(s);
							long size = encoder.getFileSize(c);
							write(String.valueOf(size), dos);
						}
					} else {
						logger.warn(MessageFormat.format("Unhandled Command: [{0}]",s));
						write("OK", dos);
					}
					// unhandled commands
					// BUFFER LinuxDVBNetworkEncoder TV
					// Tuner|1924943524|615|16777216|/var/media/tv/LinuxDVBNetworkEncoderonlocalhost5000TVTuner-0.mpgbuf|Grea
				} catch (CommandException ce) {
					logger.info("Haven an error, Shutting down the encoder.");
					encoder.stop(new StopCommand("STOP"));
					logger.error("CommandException Error", ce);
					write("ERROR " + ce.getMessage(), dos);
				}

                dos.flush();
                dis.close();
                dos.close();
			//}
		} catch (Exception e) {
		    logger.warn("Error", e);
		    /*
			logger.error("CommandHandler Error! Closing Socket.", e);
			try {
				dis.close();
			} catch (Exception ex) {
			}
			try {
				dos.close();
			} catch (Exception ex) {
			}
			try {
				socket.close();
			} catch (Exception ex) {
			}
			*/
		}
		logger.debug("Command Handled");
	}

	private static String readCommandString(DataInput datainput)
			throws InterruptedIOException, IOException {
		return datainput.readLine();
	}
}
