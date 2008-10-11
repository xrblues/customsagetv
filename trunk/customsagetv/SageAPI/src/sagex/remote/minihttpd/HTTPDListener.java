package sagex.remote.minihttpd;

public interface HTTPDListener {
	public void serverStarted(HTTPD server);

	public void serverStopped(HTTPD server);
}
