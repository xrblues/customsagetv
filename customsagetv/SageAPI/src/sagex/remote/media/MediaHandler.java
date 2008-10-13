package sagex.remote.media;

import java.awt.Image;
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
import java.io.PrintStream;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sage.media.image.RawImage;
import sagex.api.MediaFileAPI;
import sagex.remote.SagexServlet.SageHandler;

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
		} else {
			resp.sendError(404, "Invalid Media Type: " + args[2]);
		}
		} catch (Exception e) {
			resp.sendError(500, "Failed to process media request!");
		}
	}

	private void debug(String mediaFileId, HttpServletResponse resp) throws Exception {
		resp.setContentType("text/plain");
		PrintStream out = new PrintStream(resp.getOutputStream());
		// get the media file that we are going to be using
		Object sagefile = MediaFileAPI.GetMediaFileForID(Integer.parseInt(mediaFileId));
		out.println("Sage Media Class: " + sagefile.getClass().getName());
		
		Object sageImage = MediaFileAPI.GetThumbnail(sagefile);
		out.println("Sage Image Class: " + sageImage.getClass().getName());

		Method method = null;
		Method m[]  = sageImage.getClass().getMethods();
		for (int i=0;i<m.length;i++) {
			out.printf("Method: %s(%s): %s\n", m[i].getName(), buildParams(m[i].getParameterTypes()), m[i].getReturnType().getName());
		}
		out.flush();
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
	
			String name = file.getName();
			name = name.substring(0,name.lastIndexOf('.'));
			name += ".jpg";
			
			File thFile = new File(file.getParentFile(), name);
			if (!thFile.exists()) throw new FileNotFoundException(thFile.getAbsolutePath());
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
	
	// this one return negative image as well
	private void writeImagey(String mediaFileId, OutputStream os) throws Exception {
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
		ImageIO.write((RenderedImage) img, "jpeg", os);
		os.flush();
	}

	
	// This one writes an image, but the colorspace is off... not sure why...
	private void writeImagex(String mediaFileId, OutputStream os) throws Exception {
		// get the media file that we are going to be using
		Object sagefile = MediaFileAPI.GetMediaFileForID(Integer.parseInt(mediaFileId));
		Object sageImage = MediaFileAPI.GetThumbnail(sagefile);

		Method method = null;
		Method m[]  = sageImage.getClass().getMethods();
		for (int i=0;i<m.length;i++) {
			if (java.awt.Image.class.equals(m[i].getReturnType())) {
				// potential contender
				if (m[i].getParameterTypes()==null || m[i].getParameterTypes().length==0) {
					// this is it;
					method = m[i];
					break;
				}
			}
			//out.printf("Method: %s(%s): %s\n", m[i].getName(), buildParams(m[i].getParameterTypes()), m[i].getReturnType().getName());
		}
		
		if (method == null) throw new Exception("No Method to return image!");
		
		Image img = (Image) method.invoke(sageImage, null);
		if (img instanceof RenderedImage) {
			ImageIO.write((RenderedImage) img, "jpg", os);
		} else {
			throw new Exception("Need to convert the image to buffered image!!");
		}
		os.flush();
	}
	
	private String buildParams(Class<?>[] parameterTypes) {
		if (parameterTypes==null) return "null";
		
		StringBuffer sb = new StringBuffer();
		
		for (Class cl : parameterTypes) {
			sb.append(cl.getName()).append("; ");
		}
		
		return sb.toString();
	}

}
