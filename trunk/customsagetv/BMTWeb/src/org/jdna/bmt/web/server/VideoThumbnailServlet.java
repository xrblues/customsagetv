package org.jdna.bmt.web.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import sagex.api.MediaFileAPI;
import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.util.ToolsConfiguration;

public class VideoThumbnailServlet extends HttpServlet {
	private transient Logger log = Logger
			.getLogger(VideoThumbnailServlet.class);

	private static final long serialVersionUID = 1L;
	private ToolsConfiguration tools = null;

	public VideoThumbnailServlet() {
		tools = GroupProxy.get(ToolsConfiguration.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String arg = req.getParameter("file");
		if (arg == null) {
			resp.sendError(404, "Missing 'file' Arg");
			return;
		}

		int mfid = NumberUtils.toInt(arg);
		Object sageFile = MediaFileAPI.GetMediaFileForID(mfid);
		if (sageFile == null) {
			resp.sendError(404, "Sage File not found " + mfid);
			return;
		}

		int seconds = NumberUtils.toInt(req.getParameter("ss"), -1);
		long offset = NumberUtils.toLong(req.getParameter("sb"), -1);
		if (seconds < 0 && offset< 0) {
			resp.sendError(501, "Missing 'ss' or 'sb' args");
			return;
		}

		int width = NumberUtils.toInt(req.getParameter("w"), 320);
		int height = NumberUtils.toInt(req.getParameter("h"), 320);

		File dir = new File(Phoenix.getInstance().getUserCacheDir(), "videothumb/" + mfid);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String prefix = "";
		if (offset>0) {
			prefix = "O" + offset;
		} else {
			prefix = "S" + seconds;
		}
		File f = new File(dir, prefix + "_" + width + "_" + height + ".jpg");
		if (!f.exists()) {
			try {
				generateThumbnail(MediaFileAPI.GetFileForSegment(sageFile, 0), f, seconds, offset, width, height);
			} catch (Exception e) {
				e.printStackTrace();
				resp.sendError(503, "Failed to generate thumbnail\n " + e.getMessage());
				return;
			}
		}

		if (!f.exists()) {
			resp.sendError(404, "Missing File: " + f);
			return;
		}

		resp.setContentType("image/jpeg");

		resp.setContentLength((int) f.length());
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			OutputStream os = resp.getOutputStream();
			IOUtils.copyLarge(fis, os);
			os.flush();
			fis.close();
		} catch (Throwable e) {
			log.error("Failed to send file: " + f);
			resp.sendError(500, "Failed to get file " + f);
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}

	private void generateThumbnail(File inFile, File outFile, int seconds, long offset,
			int w, int h) throws IOException {
		File parent = outFile.getParentFile();
		File tmp = parent.createTempFile("tmp-", "-mplayer", parent);
		if (tmp.exists()) tmp.delete();
		tmp.mkdirs();
		try {
			List<String> process = new ArrayList<String>();
			process.add(tools.getMplayerLocation());
			if (offset>0) {
				process.add("-sb");
				process.add(String.valueOf(offset));
			} else {
				process.add("-ss");
				process.add(String.valueOf(seconds));
			}
			process.add("-nosound");
			process.add("-vo");
			process.add("jpeg");
			process.add("-vf");
			process.add("scale=" + String.valueOf(w) + ":" + String.valueOf(h));
			process.add("-frames");
			process.add("1");
			process.add(inFile.getAbsolutePath());
			ProcessBuilder pb = new ProcessBuilder(process);
			pb.directory(tmp);
			pb.redirectErrorStream(true);
			Process proc = pb.start();
			try {
				int result = proc.waitFor();
				if (result!=0) {
					throw new IOException(IOUtils.toString(proc.getInputStream()));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			File created = new File(tmp, "00000001.jpg");
			if (created.exists()) {
				created.renameTo(outFile);
			} else {
				throw new IOException("Failed to create thumbnail from file");
			}
		} finally {
			try {
				FileUtils.deleteDirectory(tmp);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
