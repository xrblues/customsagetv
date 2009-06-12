package org.jdna.bmt.web.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import sagex.SageAPI;
import sagex.remote.media.MediaHandler;

public class MediaHandlerServlet extends HttpServlet {
    private MediaHandler mediaHandler = null;
    private Logger log = null;
    public MediaHandlerServlet() {
        ServicesInit.init();
        mediaHandler = new MediaHandler();
        log = Logger.getLogger(MediaHandler.class);
        log.debug("BMT Media Servlet Created");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String mediaUrl = "/media"+req.getPathInfo();
        String parts[] = mediaUrl.split("/");
        
        if (SageAPI.isRemote()) {
            //log.debug("Using Remote API for Images: " + mediaUrl);
            if (!StringUtils.isEmpty(req.getQueryString())) {
                mediaUrl += ("?" + req.getQueryString());
            }
            proxyMediaServlet(mediaUrl, req, resp);
        } else {
            mediaHandler.hanleRequest(parts, req, resp);
        }
    }

    private void proxyMediaServlet(String url, HttpServletRequest req, HttpServletResponse resp) {
        try {
            // TODO: Use configuration
            URL u = new URL("http://mediaserver:8081/sagex" + url);
            log.debug("Proxy Media: " + u.toString());
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008072820 Firefox/3.0.1");
            OutputStream os = resp.getOutputStream();
            IOUtils.copy(c.getInputStream(), os);
            os.flush();
            resp.flushBuffer();
        } catch (Throwable t) {
            log.error("Failed to get url: " + url, t);
            try {
                resp.sendError(500, t.getMessage());
            } catch (IOException e) {
            }
        }
    }
}
