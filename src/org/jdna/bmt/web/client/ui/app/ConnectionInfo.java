package org.jdna.bmt.web.client.ui.app;

import java.io.Serializable;

public class ConnectionInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String host;
    private int port;
    public ConnectionInfo() {
    }
    
    public ConnectionInfo(String host, int port) {
        this.host=host;
        this.port=port;
    }
    
    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }
    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }
    
}
