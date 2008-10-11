package sagex.remote.minihttpd;

public interface Servlet {
	public void doGet(Request req, Response res) throws Exception;
}
