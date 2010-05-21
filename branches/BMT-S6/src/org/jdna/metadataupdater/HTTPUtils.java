package org.jdna.metadataupdater;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HTTPUtils {
    public static void configueURLConnection(final URLConnection conn, final String user, final String pass) throws Exception {
        if (user!=null) {
            if (pass==null) throw new Exception("Missing password");
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pass.toCharArray());
                }
            });
        }
        configureSSL(conn);
    }
    
    
    /**
     * If the connection is SSL, configure it to work with unsigned certificates
     * and certificates whose hostnames don't match. More sophisticated handling
     * can be performed by getting server certificates, using local keystores,
     * session context, certificate chaining, etc. But that's not really
     * necessary for these purposes. This is much simpler to code.
     */
    /*
     * See these pages for examples:
     * http://dreamingthings.blogspot.com/2006/12/no
     * -more-unable-to-find-valid.html
     * http://javasecurity.wikidot.com/example-item-1
     * http://www.java2s.com/Code/
     * JavaAPI/javax.net.ssl/SSLSessiongetPeerCertificates.htm
     */
    private static void configureSSL(URLConnection conn) throws Exception {
        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
            SSLSocketFactory sf = null;
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, new TrustManager[] { new DefaultTrustManager() }, null);
            sf = context.getSocketFactory();
            httpsConn.setSSLSocketFactory(sf);
            httpsConn.setHostnameVerifier(new DefaultHostnameVerifier());
        }
    }

    /**
     * Create a custom hostname verifier to avoid SSL errors about the
     * connection hostname not matching the hostname on the certificate.
     */
    private static class DefaultHostnameVerifier implements HostnameVerifier {
        public boolean verify(String s, SSLSession sslsession) {
            return true;
        }
    }

    /**
     * Always trust server SSL certificates
     */
    private static class DefaultTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
            return;
        }

        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }
    }

}
