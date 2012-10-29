package sagex.remote.media;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.RandomAccess;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sagex.api.MediaFileAPI;

public class MediaFileRequestHandler implements SageMediaRequestHandler {
	public void processRequest(HttpServletRequest req, HttpServletResponse resp, Object sagefile) throws Exception {
		// get the media file that we are going to be using
		File file = MediaFileAPI.GetFileForSegment(sagefile, 0);

		if (!file.exists())
			throw new FileNotFoundException(file.getAbsolutePath());

		// check for a range request
		String range = req.getHeader("Range");
		if (range != null && range.trim().length() > 0) {
			doRangeRequest(file, req, resp, sagefile, getRanges(range, file.length()));
		} else {
			doNormalRequest(file, req, resp, sagefile);
		}
	}

	public void doRangeRequest(File file, HttpServletRequest req, HttpServletResponse resp, Object sagefile, long[] ranges)
			throws IOException {
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		try {
			raf.seek(ranges[0]);
			FileInputStream fis = new FileInputStream(raf.getFD());
			long size = ranges[1] - ranges[0];
			long counted = 0;
			int bufSize = (int) Math.min(size, 64 * 1024);
			byte[] buf = new byte[bufSize];
			int curRead = 0;
			OutputStream os = resp.getOutputStream();
			while (counted < size) {
				curRead = fis.read(buf, 0, (int) Math.min(bufSize, size - counted));
				if (curRead == 0 || curRead == -1)
					break;
				counted += curRead;
				os.write(buf, 0, curRead);
			}
			os.flush();
			fis.close();
		} finally {
			if (raf != null) {
				raf.close();
			}
		}
	}

	public long[] getRanges(String rangeHeader, long fileLength) {
		if (rangeHeader == null)
			return null;
		rangeHeader = rangeHeader.trim();
		String bytes = rangeHeader.substring(rangeHeader.indexOf('=') + 1);
		if (bytes.length() == 0) {
			return new long[] { 0, fileLength };
		}
		if (bytes.startsWith("-")) {
			return new long[] { 0, Long.valueOf(bytes.substring(1)) };
		} else if (bytes.endsWith("-")) {
			return new long[] { Long.valueOf(bytes.substring(0, bytes.length() - 1)), fileLength };
		} else {
			int pos = bytes.indexOf("-");
			return new long[] { Long.valueOf(bytes.substring(0, pos)), Long.valueOf(bytes.substring(pos + 1)) };
		}
	}

	private void doNormalRequest(File file, HttpServletRequest req, HttpServletResponse resp, Object sagefile) throws IOException {
		resp.setHeader("Content-Length", String.valueOf(file.length()));

		if (MediaFileAPI.IsMusicFile(sagefile)) {
			resp.setContentType("audio/mpeg");
		} else if (MediaFileAPI.IsPictureFile(sagefile)) {
			resp.setContentType("image/jpeg");
		} else {
			resp.setContentType("video/mpeg");
		}

		String forceMime = req.getParameter("force-mime");
		if (forceMime != null && forceMime.length() > 0) {
			resp.setContentType(forceMime);
		}

		OutputStream os = resp.getOutputStream();
		MediaHandler.copyStream(new FileInputStream(file), os);
		os.flush();
	}
}
