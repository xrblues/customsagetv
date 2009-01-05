package test;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class TestHttpUserAgent {
    public static void main(String args[]) throws Throwable {
        System.setProperty("http.agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/2008111318 Ubuntu/8.10 (sean) Firefox/3.0.4");
        URL u = new URL("http://localhost:8080/SageMediaToolsWeb/MediaTools/browse?location=sdfsd");
        URLConnection conn = u.openConnection();
        HttpURLConnection uconn = (HttpURLConnection) conn;
        uconn.getInputStream();
        System.out.println("Response Code: " + uconn.getResponseCode());
        System.out.println("Response Msg: " + uconn.getResponseMessage());
        System.out.println("System User Agent: " + System.getProperty("http.agent"));
    }
}
