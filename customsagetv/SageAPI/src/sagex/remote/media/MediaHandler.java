package sagex.remote.media;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sagex.api.MediaFileAPI;
import sagex.api.Utility;
import sagex.remote.SagexServlet.SageHandler;

public class MediaHandler implements SageHandler {
    public static final String                   SERVLET_PATH = "media";
    private Map<String, SageMediaRequestHandler> handlers     = new HashMap<String, SageMediaRequestHandler>();

    public MediaHandler() {
        System.out.println("Media Servlet Handler Created.");
        handlers.put("properties", new PropertiesSageRequestHandler());
        handlers.put("thumbnail", new ThumbnailRequestHandler());
        handlers.put("logo", new LogoRequestHandler());
        handlers.put("mediafile", new MediaFileRequestHandler());
        handlers.put("albumart", new AlbumArtHandler());

        handlers.put("poster", new ProxySageMediaRequestHandler("sagex.phoenix.fanart.FanartMediaRequestHandler", "poster"));
        handlers.put("background", new ProxySageMediaRequestHandler("sagex.phoenix.fanart.FanartMediaRequestHandler", "background"));
        handlers.put("banner", new ProxySageMediaRequestHandler("sagex.phoenix.fanart.FanartMediaRequestHandler", "banner"));
    }

    public void hanleRequest(String args[], HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	// URL looks like
    	// /media/thumnail/213233
        // 0 - null
        // 1 - media
        // 2 - mediafile|thumbnail|debug
        // 3 - media id
        try {
            if (args.length < 3) {
                throw new ServletException("missing media artifact type (ie, thumbnail, poster, etc)");
            }
            
            // special case for logos
            if ("logo".equals(args[2])) {
            	SageMediaRequestHandler handler = handlers.get("logo");
            	handler.processRequest(req, resp, args[3]);
            	return;
            }
            
            
            // process mediafile requests
            String mediaFileId = req.getParameter("mediafile");
            if (mediaFileId == null) {
                mediaFileId = args[3];
            }
            
            if (mediaFileId == null) {
                help(resp, "Missing mediafile");
                return;
            }
            
            Object sageMedia = getMediaFile(mediaFileId);

            SageMediaRequestHandler handler = handlers.get(args[2]);
            if (handler == null) {
                help(resp, "Unknown Media Command: " + args[2]);
                return;
            }
            
            if ("poster".equals(args[2])) {
                try {
                    // try poster handler, and then the thumbnail handler
                    handler.processRequest(req, resp, sageMedia);
                } catch (Exception e) {
                    handlers.get("thumbnail").processRequest(req, resp, sageMedia);
                }
            } else {
                handler.processRequest(req, resp, sageMedia);
            }
        } catch (FileNotFoundException e) {
        	resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            help(resp, e);
        }
    }
    
    private void help(HttpServletResponse resp, String msg) throws IOException {
        help(resp, msg, null);
    }

    private void help(HttpServletResponse resp, Throwable t) throws IOException {
        help(resp, t.getMessage(), t);
    }
    
    private void help(HttpServletResponse resp, String msg, Throwable t) throws IOException {
        PrintWriter w = resp.getWriter();
        w.printf("<h1>sagex.api (%s): Media Handler Error</H1>\n", sagex.api.Version.GetVersion());
        w.printf("<h2>%s</H2>", msg);
        if (t!=null) {
            w.println("<pre>");
            t.printStackTrace(w);
            w.println("</pre>");
        }
        w.println("<br/>");
        w.println("<h2>Usage</h2>");
        w.println("<pre>");
        w.println("/sagex/media/<b>COMMAND</b>/<b>MEDIA_FILE</b>");
        w.println("or");
        w.println("/sagex/media/<b>COMMAND</b>?mediafile=<b>MEDIA_FILE</b>\n");
        w.print("Where <b>COMMAND</b> is one of ");
        for (String s : handlers.keySet()) {
            w.print("<i>"+s+"</i>");
            w.print(", ");
        }
        w.println();
        w.println("And <b>MEDIA_FILE</b> is a Sage MediaFileId or File Path");
        w.println("</pre>");
        w.println("<br/>");
        w.println("<h2>Examples</h2>");
        w.println("<pre>");
        w.println("/sagex/media/thumbnail/3212321");
        w.println("/sagex/media/mediafile/3212321");
        w.println("/sagex/media/mediafile?mediafile=/sagetv/vidoes/tv/futurama.avi");
        w.println("/sagex/media/background/3212321");
        w.println("NOTE: background, banner, and poster all require Phoenix Fanart APIs build 30 (1.30) or later.");
        w.println("");
        w.println("You can also fetch logos");
        w.println("/sagex/media/logo/WTVDDT");
        w.println("</pre>");
        w.flush();
    }
    
    public Object getMediaFile(String id) throws Exception {
        try {
            int mfid = Integer.parseInt(id);
            Object o  = MediaFileAPI.GetMediaFileForID(mfid);
            if (o==null) {
                throw new Exception("Unknown MediaFile: " + id);
            }
            return o;
        } catch (Exception e) {
            File f = new File(id);
            if (f.exists()) {
                Object o = MediaFileAPI.GetMediaFileForFilePath(f);
                if (o==null) {
                    throw new Exception("Unknown MediaFile: " + id);
                }
                return o;
            } else {
                throw new Exception("Not A MediaFile: " + id); 
            }
        }
    }

    public static void copyStream(InputStream is, OutputStream os) throws IOException {
        byte buf[] = new byte[4096];
        BufferedInputStream bis = new BufferedInputStream(is);
        BufferedOutputStream bos = new BufferedOutputStream(os);

        try {
            int s;
            while ((s = bis.read(buf)) > 0) {
                bos.write(buf, 0, s);
            }
        } finally {
            try {
                bos.flush();
            } catch (Exception x) {
            }
        }
        bis.close();
    }

    public static void writeSageImage(Object sagefile, HttpServletResponse resp) throws FileNotFoundException, Exception {
        // get the media file that we are going to be using
        // TODO: Maybe cache this for performance reasons
        writeSageImageFile(MediaFileAPI.GetThumbnail(sagefile), resp);
    }

    public static void writeSageImageFile(Object sageImage, HttpServletResponse resp) throws FileNotFoundException, Exception {
        if (sageImage==null) throw new FileNotFoundException("No Image");
        BufferedImage img = Utility.GetImageAsBufferedImage(sageImage);
        if (img==null) throw new FileNotFoundException("Unable to get BufferedImage");
        resp.setContentType("image/png");
        OutputStream os = resp.getOutputStream();
        ImageIO.write((RenderedImage) img, "png", os);
        os.flush();
    }
}
