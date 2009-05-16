package sagex.remote.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sagex.api.MediaFileAPI;

public class ThumbnailRequestHandler implements SageMediaRequestHandler {

    public void processRequest(HttpServletRequest req, HttpServletResponse resp, Object sagefile) throws Exception {
        File file = MediaFileAPI.GetFileForSegment(sagefile, 0);

        File thFile = null;
        if (MediaFileAPI.IsDVD(sagefile)) {
            thFile = new File(file.getParentFile(), file.getName() + ".jpg");
            if (!thFile.exists()) {
                thFile = new File(file, "folder.jpg");
            }
        }
        
        if (thFile==null) {
            String name = file.getName();
            name = name.substring(0, name.lastIndexOf('.'));
            name += ".jpg";
            thFile = new File(file.getParentFile(), name);
        }
        
        // write sage thumbnail for this file
        if (thFile == null || !thFile.exists()) {
            MediaHandler.writeSageImage(sagefile, resp);
            return;
        }

        resp.setContentType("image/jpeg");
        resp.setHeader("Content-Length", String.valueOf(thFile.length()));
        OutputStream os = resp.getOutputStream();
        MediaHandler.copyStream(new FileInputStream(thFile), os);
        os.flush();
    }

}
