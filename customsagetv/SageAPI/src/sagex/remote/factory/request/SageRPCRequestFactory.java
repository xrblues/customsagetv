package sagex.remote.factory.request;

/**
 * Unofficial SageTV Generated File - Never Edit
 * Generated Date/Time: 10/10/08 7:29 PM
 * See Official Sage Documentation at <a href='http://download.sage.tv/api/sage/api/SageRPCRequestFactory.html'>SageRPCRequestFactory</a>
 * This Generated API is not Affiliated with SageTV.  It is user contributed.
 */

import sagex.remote.RemoteRequest;

public class SageRPCRequestFactory {
	public static RemoteRequest createRequest(String context, String api, String command, String[] parameters) {
		if ("AiringAPI".equals(api)) {
			return AiringAPIFactory.createRequest(context, command, parameters);
		}
		if ("AlbumAPI".equals(api)) {
			return AlbumAPIFactory.createRequest(context, command, parameters);
		}
		if ("CaptureDeviceAPI".equals(api)) {
			return CaptureDeviceAPIFactory.createRequest(context, command, parameters);
		}
		if ("CaptureDeviceInputAPI".equals(api)) {
			return CaptureDeviceInputAPIFactory.createRequest(context, command, parameters);
		}
		if ("ChannelAPI".equals(api)) {
			return ChannelAPIFactory.createRequest(context, command, parameters);
		}
		if ("Configuration".equals(api)) {
			return ConfigurationFactory.createRequest(context, command, parameters);
		}
		if ("Database".equals(api)) {
			return DatabaseFactory.createRequest(context, command, parameters);
		}
		if ("FavoriteAPI".equals(api)) {
			return FavoriteAPIFactory.createRequest(context, command, parameters);
		}
		if ("Global".equals(api)) {
			return GlobalFactory.createRequest(context, command, parameters);
		}
		if ("MediaFileAPI".equals(api)) {
			return MediaFileAPIFactory.createRequest(context, command, parameters);
		}
		if ("MediaPlayerAPI".equals(api)) {
			return MediaPlayerAPIFactory.createRequest(context, command, parameters);
		}
		if ("PlaylistAPI".equals(api)) {
			return PlaylistAPIFactory.createRequest(context, command, parameters);
		}
		if ("SeriesInfoAPI".equals(api)) {
			return SeriesInfoAPIFactory.createRequest(context, command, parameters);
		}
		if ("ShowAPI".equals(api)) {
			return ShowAPIFactory.createRequest(context, command, parameters);
		}
		if ("TranscodeAPI".equals(api)) {
			return TranscodeAPIFactory.createRequest(context, command, parameters);
		}
		if ("TVEditorialAPI".equals(api)) {
			return TVEditorialAPIFactory.createRequest(context, command, parameters);
		}
		if ("Utility".equals(api)) {
			return UtilityFactory.createRequest(context, command, parameters);
		}
		if ("WidgetAPI".equals(api)) {
			return WidgetAPIFactory.createRequest(context, command, parameters);
		}
		throw new RuntimeException("Invalid SageRPCRequestFactory Command: " + command);
	}

}
