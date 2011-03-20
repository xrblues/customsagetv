package org.jdna.sagetv.networkencoder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

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
public abstract class AbstractNetworkEncoderService implements Runnable {
    private Logger                logger         = Logger.getLogger(this.getClass());

    private ServerSocket          serversocket   = null;
    private DatagramSocket        datagramSocket = null;
    private INetworkEncoder       encoder        = null;

    private boolean               isRunning      = false;

    private String serverId = null;
    
    /**
     * ServerId is used by the configuration as the server key for configuration
     * 
     * @param serverId
     */
    public AbstractNetworkEncoderService(String serverId) {
        this.serverId=serverId;
    }

    public void stopServer() {
        try {
            encoder.stop(new StopCommand("STOP ALL"));
        } catch (Throwable t) {
            t.printStackTrace();
        }

        logger.info("NetworkEncoder has been stopped: " + getServerId());
        isRunning = false;
    }

    protected abstract INetworkEncoder createNetworkEncoder();

    public void startServer() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                stopServer();
            }
        });

        this.encoder = createNetworkEncoder();
        
        // start multicast broadcast services, if enabled
        runMulticast();
        
        try {
            serversocket = new ServerSocket(getPort());
        } catch (Exception exception) {
            logger.error("Error creating EncodingServer socket: " + getPort(), exception);
            return;
        }
        logger.info("EncodingServer: " + getServerId() + " launched on port " + getPort());

        while (true) {
            try {
                Socket socket = serversocket.accept();
                socket.setSoTimeout(15000);
                logger.debug("EncodingServer:" + getServerId() + " received connection:" + socket);
                new CommandHandler(socket, encoder);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    logger.warn("Main Server interrupted.  Exiting.");
                    break;
                }
                socket.close();
            } catch (Exception exception1) {
                logger.error("Error with EncodingServer socket!", exception1);
            }
        }
    }

    public void runMulticast() {
        if (!isDiscoveryEnabled()) {
            logger.info("Discovery is disabled for " + getServerId());
            return;
        }
        
        final int port = getPort();
        isRunning = true;
        Thread thread = new Thread(getServerId() + "-" + port) {
            public void run() {
                datagramSocket = null;
                try {
                    try {
                        datagramSocket = new DatagramSocket(8271);
                    } catch (IOException ioexception) {
                        logger.error("Error creating discovery socket! (discovery disabled) for " + getServerId(), ioexception);
                        return;
                    }
                    logger.info("Encoding Discovery Server was instantiated: " + getServerId());

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
                                        String s = getServerId() + ":"+getPort();
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

    private String getServerId() {
        return serverId;
    }

    private boolean isDiscoveryEnabled() {
        Object disc = getValue("discovery");
        logger.info("Discovery Value: " + disc);
        if (disc==null) {
            return false;
        }
        boolean b = BooleanUtils.toBoolean(String.valueOf(disc)); 
        logger.info("Discovery Value: " + disc + "; " + b);
        return b;
    }

    private int getPort() {
        Object port = getValue("port");
        if (port==null) {
            throw new RuntimeException("Port Not Configured for: " + getServerId());
        }
        return NumberUtils.toInt(String.valueOf(port));
    }
    
    /**
     * Get a configuration key from org/jdna/networkencoder/serverId/key"
     * 
     * @param key
     * @return
     */
    protected Object getValue(String key) {
        String id = String.format("org/jdna/networkencoder/%s/%s", getServerId(), key);
        return phoenix.api.GetProperty(id);
    }

    @Override
    public void run() {
        try {
            // this doesn't return until it's shutdown.
            startServer();
        } catch (Exception e) {
            logger.error("Failed to start Network Encoding Server");
        }
    }
}
