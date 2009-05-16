package sagex.remote.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sagex.api.MediaFileAPI;

public class MediaFileRequestHandler implements SageMediaRequestHandler {
    public void processRequest(HttpServletRequest req, HttpServletResponse resp, Object sagefile) throws Exception {
        // get the media file that we are going to be using
        File file = MediaFileAPI.GetFileForSegment(sagefile, 0);

        if (!file.exists()) throw new FileNotFoundException(file.getAbsolutePath());

        resp.setHeader("Content-Length", String.valueOf(file.length()));
        resp.setContentType("video/mpeg");

        OutputStream os = resp.getOutputStream();
        MediaHandler.copyStream(new FileInputStream(file), os);
        os.flush();
    }
}
