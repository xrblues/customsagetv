package org.jdna.sagetv.networkencoder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import sagex.SageAPI;
import sagex.phoenix.configuration.proxy.GroupProxy;

/**
 * Main server for handling the network encoder communications.
 * 
 * Server should automatically register with sagetv if the server has <b>network_encoder_discovery=true</b> set. 
 * <br/>
 * 
 * If sage does not find the server, then add this to the sage.properties
 * <pre>
mmc/encoders/123/1/0/video_crossbar_index=0
mmc/encoders/123/1/0/video_crossbar_type=1
mmc/encoders/123/capture_config=2050
mmc/encoders/123/encoder_merit=0
mmc/encoders/123/encoding_host=localhost:5000
mmc/encoders/123/encoding_host_login_md5=
mmc/encoders/123/video_capture_device_name=Faker
mmc/encoders/123/video_capture_device_num=0
 * </pre>
 * 
 * @author seans
 * 
 */
public class DVBTunerServer {
    private Logger                logger         = Logger.getLogger(DVBTunerServer.class);

    private ServerSocket          serversocket   = null;
    private DatagramSocket        datagramSocket = null;
    private INetworkEncoder       encoder        = null;

    private boolean               isRunning      = false;
    private boolean               verbose        = false;

    private ServerConfiguration   config         = null;

    public DVBTunerServer() {
        config = GroupProxy.get(ServerConfiguration.class);
    }

    public static void main(String args[]) throws Exception {
        SageAPI.setProvider(new ConsoleSageAPIProvider());
        final DVBTunerServer server = new DVBTunerServer();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                server.stopServer();
            }
        });

        // this doesn't return until it's shutdown.
        server.startServer();
    }

    public void stopServer() {
        try {
        	if (encoder!=null) {
        		encoder.stop(new StopCommand("STOP ALL"));
        	}
        } catch (Throwable t) {
            logger.warn("unable to stop tuner", t);
        }
        
        try {
        	if (serversocket!=null) {
        		serversocket.close();
        	}
        } catch (Throwable t) {
            logger.warn("Unable to close server socket", t);
        }

        try {
        	if (datagramSocket!=null) {
        		datagramSocket.close();
        	}
        } catch (Throwable t) {
            logger.warn("Unable to close datagram socket", t);
        }
        
        encoder=null;
        logger.info("NetworkEncoder has been stopped.");
        isRunning = false;
    }

    public void init() throws Exception {
        this.encoder = (INetworkEncoder) Class.forName(config.getDefaultEncoderClass()).newInstance();
        logger.debug("NetworkEncoder created: " + config.getDefaultEncoderClass());
    }

    public void startServer() {
        try {
			init();
		} catch (Exception e) {
			logger.error("Failed to initialize the Network Encoder!", e);
		}
		
        runMulticast();

        try {
            serversocket = new ServerSocket(config.getPort());
        } catch (Exception exception) {
            logger.error("Error creating EncodingServer socket!", exception);
            return;
        }
        logger.info("EncodingServer launched on port " + config.getPort());
        System.out.println("EncodingServer launched on port " + config.getPort());

        while (true) {
            try {
                Socket socket = serversocket.accept();
                socket.setSoTimeout(15000);
                logger.debug("EncodingServer received connection:" + socket);
                new CommandHandler(socket, encoder);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    logger.warn("Main DVB Server interrupted.  Exiting.");
                    break;
                }
                socket.close();
            } catch (Exception exception1) {
                logger.error("Error with EncodingServer socket!", exception1);
            }
        }
    }

    public void runMulticast() {
        if (!config.getDiscoveryEnabled()) {
            logger.info("Discovery is disabled.");
            return;
        }
        
        final int port = config.getPort();
        isRunning = true;
        Thread thread = new Thread("DVBNetworkEncoderDiscoveryServer-" + port) {

            public void run() {
                datagramSocket = null;
                try {
                    try {
                        datagramSocket = new DatagramSocket(8271);
                    } catch (IOException ioexception) {
                        logger.error("Error creating discovery socket! (discovery disabled)", ioexception);
                        return;
                    }
                    logger.info("Encoding Discovery Server was instantiated.");

                    do {
                        if (datagramSocket == null) break;
                        try {
                            DatagramPacket datagrampacket = new DatagramPacket(new byte[4096], 4096);
                            datagramSocket.receive(datagrampacket);
                            logger.debug("Server got broadcast packet: " + datagrampacket);
                            if (datagrampacket.getLength() >= 6) {
                                byte abyte0[] = datagrampacket.getData();
                                if (abyte0[0] == 83 && abyte0[1] == 84 && abyte0[2] == 78) {
                                    byte byte0 = abyte0[3];
                                    byte byte1 = abyte0[4];
                                    byte byte2 = abyte0[5];
                                    if (byte0 > 4 || byte0 == 4 && (byte1 > 1 || byte1 == 1 && byte2 >= 0)) {
                                        abyte0[3] = 4;
                                        abyte0[4] = 1;
                                        abyte0[5] = 0;
                                        abyte0[6] = (byte) (port >> 8 & 255);
                                        abyte0[7] = (byte) (port & 255);
                                        String s = "DVB Network Encoder ("+config.getPort()+")";
                                        byte abyte1[] = s.getBytes("UTF-8");
                                        abyte0[8] = (byte) abyte1.length;
                                        System.arraycopy(abyte1, 0, abyte0, 9, abyte1.length);
                                        datagrampacket.setLength(9 + abyte1.length);
                                        logger.debug("Server sent back discovery data:" + datagrampacket);
                                        datagramSocket.send(datagrampacket);
                                    }
                                }
                            }
                        } catch (IOException ioexception1) {
                            logger.error("Error client connection!", ioexception1);
                            try {
                                Thread.sleep(100L);
                            } catch (Exception exception4) {
                            }
                        }
                    } while (isRunning);
                    try {
                        datagramSocket.close();
                    } catch (Exception exception3) {
                    }
                } finally {
                    logger.info("Discovery Server shutting down...");
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
}
