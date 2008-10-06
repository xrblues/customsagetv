package sagex.remote.minihttpd;

public interface Request {
    public String getParameter(String name);
    public String getPath();
}
