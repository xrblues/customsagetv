package sagex.remote.media;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sagex.api.ChannelAPI;
import sagex.api.Global;
import sagex.util.TypesUtil;

/**
 * 
 * Resolves the logo and writes it
 * @author seans
 *
 */
public class LogoRequestHandler implements SageMediaRequestHandler {
    public void processRequest(HttpServletRequest req, HttpServletResponse resp, Object logo) throws Exception {
    	String type = req.getParameter("type");
    	if (type!=null&&type.trim().length()>0) {
    		// use new api
    		MediaHandler.writeSageImageFile(ChannelAPI.GetChannelLogo(ChannelAPI.GetChannelForStationID(TypesUtil.toInt((String)logo, 0)), type, TypesUtil.toInt(req.getParameter("index"),0), TypesUtil.toBoolean(req.getParameter("fallback"), true)), req, resp);
    	} else {
    		Object l = Global.GetLogo((String)logo);
    		if (l==null) {
    			l = ChannelAPI.GetChannelLogo(ChannelAPI.GetChannelForStationID(TypesUtil.toInt((String)logo, 0)));
    		}
    		MediaHandler.writeSageImageFile(l, req, resp);
    	}
    }
}
