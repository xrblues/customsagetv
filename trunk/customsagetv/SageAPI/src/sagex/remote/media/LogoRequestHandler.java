package sagex.remote.media;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sagex.api.Global;

/**
 * 
 * Resolves the logo and writes it
 * @author seans
 *
 */
public class LogoRequestHandler implements SageMediaRequestHandler {
    public void processRequest(HttpServletRequest req, HttpServletResponse resp, Object logo) throws Exception {
        MediaHandler.writeSageImageFile(Global.GetLogo((String)logo), resp);
    }
}
