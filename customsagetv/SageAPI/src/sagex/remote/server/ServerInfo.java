package sagex.remote.server;

import java.io.Serializable;

public class ServerInfo implements Serializable {
	private static final long serialVersionUID = 3L;
	public ServerInfo() {
	}
	public String host;
	public int port;
}
