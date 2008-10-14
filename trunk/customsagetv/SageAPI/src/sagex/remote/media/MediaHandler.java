package sagex.remote.media;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sage.media.image.RawImage;
import sagex.api.MediaFileAPI;
import sagex.remote.SagexServlet.SageHandler;
import sun.util.logging.resources.logging;

public class MediaHandler implements SageHandler {

	public static final String SERVLET_PATH = "media";

	public MediaHandler() {
		System.out.println("Media Servlet Handler Created.");
	}

	public void hanleRequest(String args[], HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 0 - null
		// 1 - media
		// 2 - mediafile|thumbnail|debug
		// 3 - media id 
		if (args.length<3) {
			throw new ServletException("media missing one of 'thumbnail' or 'mediafile'");
		}
		
		try {
		if ("debug".equals(args[2])) {
			debug(args[3], resp);
		} else if ("mediafile".equals(args[2])) {
			writeMediaFile(args[3], resp);
		} else if ("thumbnail".equals(args[2])) {
			writeImage(args[3], resp);
		} else if ("sagethumbnail".equals(args[2])) {
			writeSageImage(args[3], resp);
		} else if ("properties".equals(args[2])) {
			writeProperties(args[3], resp);
		} else {
			resp.sendError(404, "Invalid Media Type: " + args[2]);
		}
		} catch (Exception e) {
			resp.sendError(500, "Failed to process media request!");
		}
	}

	private void writeProperties(String mediaFileId, HttpServletResponse resp) throws Exception {
		try {
			// get the media file that we are going to be using
			Object sagefile = MediaFileAPI.GetMediaFileForID(Integer.parseInt(mediaFileId));
			File file = MediaFileAPI.GetFileForSegment(sagefile, 0);

			File pFile = null;
			pFile = new File(file.getParentFile(), file.getName() + ".properties");
			if (pFile==null || !pFile.exists()) throw new FileNotFoundException(pFile.getAbsolutePath());
			
			resp.setContentType("text/plain");
			resp.setHeader("Content-Length", String.valueOf(pFile.length()));
			OutputStream os = resp.getOutputStream();
			copyStream(new FileInputStream(pFile), os);
			os.flush();
		} catch (Exception e) {
			resp.sendError(404, "Properties Not Found: " + mediaFileId + "; " + e.getMessage());
		}
	}

	private void debug(String mediaFileId, HttpServletResponse resp) throws Exception {
		resp.setContentType("text/plain");
 	    resp.getWriter().write("debug");
	}

	// This one writes an image, but the colorspace is off... not sure why...
	private void writeMediaFile(String mediaFileId, HttpServletResponse resp) throws Exception {
		try {
			// get the media file that we are going to be using
			Object sagefile = MediaFileAPI.GetMediaFileForID(Integer.parseInt(mediaFileId));
			File file = MediaFileAPI.GetFileForSegment(sagefile, 0);
	
			if (!file.exists()) throw new FileNotFoundException(file.getAbsolutePath());
			
			resp.setHeader("Content-Length", String.valueOf(file.length()));
			resp.setContentType("video/mpeg");
			
			OutputStream os = resp.getOutputStream();
			copyStream(new FileInputStream(file), os);
			os.flush();
		} catch (Exception e) {
			resp.sendError(404, "MediaFile Not Found: " + mediaFileId);
		}
	}
	
	// This one writes an image, but the colorspace is off... not sure why...
	private void writeImage(String mediaFileId, HttpServletResponse resp) throws Exception {
		try {
			// get the media file that we are going to be using
			Object sagefile = MediaFileAPI.GetMediaFileForID(Integer.parseInt(mediaFileId));
			File file = MediaFileAPI.GetFileForSegment(sagefile, 0);

			File thFile = null;
			if (MediaFileAPI.IsDVD(sagefile)) {
				if (file.isDirectory()) {
					thFile = new File(file.getParentFile(), file.getName() + ".jpg");
					if (!thFile.exists()) {
						thFile = new File(file, "folder.jpg");
					}
					if (!thFile.exists()) {
						thFile = new File(file, "VIDEO_TS/folder.jpg");
					}
				} else {
					String name = file.getName();
					name = name.substring(0,name.lastIndexOf('.'));
					name += ".jpg";
					thFile = new File(file.getParentFile(), name);
				}
			} else {
				String name = file.getName();
				name = name.substring(0,name.lastIndexOf('.'));
				name += ".jpg";
				thFile = new File(file.getParentFile(), name);
			}
			if (thFile==null || !thFile.exists()) throw new FileNotFoundException(thFile.getAbsolutePath());
			
			resp.setContentType("image/jpeg");
			resp.setHeader("Content-Length", String.valueOf(thFile.length()));
			OutputStream os = resp.getOutputStream();
			copyStream(new FileInputStream(thFile), os);
			os.flush();
		} catch (Exception e) {
			resp.sendError(404, "Image Not Found: " + mediaFileId);
		}
	}

	public static void copyStream(InputStream is, OutputStream os) throws IOException {
		byte buf[] = new byte[4096];
		BufferedInputStream bis = new BufferedInputStream(is);
		BufferedOutputStream bos = new BufferedOutputStream(os);
		
		try {
			int s;
			while ((s = bis.read(buf))>0) {
				bos.write(buf, 0, s);
			}
		} finally {
			try {
				bos.flush();
			} catch (Exception x) {
			}
		}
	}
	
	// this one return negative image, which is wrong, and I don't know why??
	private void writeSageImage(String mediaFileId, HttpServletResponse resp) throws Exception {
		// get the media file that we are going to be using
		Object sagefile = MediaFileAPI.GetMediaFileForID(Integer.parseInt(mediaFileId));
		Object sageImage = MediaFileAPI.GetThumbnail(sagefile);

		Method method = null;
		Method m[]  = sageImage.getClass().getMethods();
		for (int i=0;i<m.length;i++) {
			if (sage.media.image.RawImage.class.equals(m[i].getReturnType())) {
				// potential contender
				method = m[i];
				break;
			}
		}
		
		if (method == null) throw new Exception("No Method to return image!");
		
		RawImage rimg  = (RawImage) method.invoke(sageImage, new Object[] {0});
		BufferedImage img =rimg.convertToBufferedImage();
		resp.setContentType("image/jpeg");
		OutputStream os = resp.getOutputStream();
		ImageIO.write((RenderedImage) img, "jpeg", os);
		os.flush();
	}
}
