package sagex.remote.minihttpd;

import java.io.Serializable;

public class ServerInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public ServerInfo() {
	}
	
	public String host;
	public int port;
}
