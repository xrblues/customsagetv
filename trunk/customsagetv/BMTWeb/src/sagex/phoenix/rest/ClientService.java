package sagex.phoenix.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import sagex.UIContext;
import sagex.api.Global;
import sagex.api.MediaFileAPI;
import sagex.api.MediaPlayerAPI;
import sagex.api.PlaylistAPI;
import sagex.api.WidgetAPI;
import sagex.api.enums.SageCommandEnum;

public class ClientService extends JSONHandler {
	private static final String PARAM_PLAYLIST_NAME = "playlist";
	public static final String PARAM_FILE = "file";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_ID = "id";
	public static final String CMD = "cmd";
	public static final String CMD_GET_CLIENTS = "getclients";
	public static final String CMD_IS_CONNECTED = "isconnected";
	public static final String CMD_SET_NAME = "setname";
	public static final String CMD_PLAY_FILE = "playfile";
	public static final String CMD_PLAYLIST = "playlist";
	
	public static final String ID = "client";
	
	private static class Client {
		public String id;
		public String name;
	}

	private Help help;
	
	public ClientService() {
		help = new Help();
		help.title=ID;
		help.description = "Manage Clients connected to a SageTV server";
		help.parameters.put(CMD, "Client Command: getclients, isconnected");
		help.parameters.put(PARAM_ID, "Client Context ID.  Used with isconnected.");
		help.parameters.put(PARAM_NAME, "Extender Name. (used with cmd=setname)");
		help.examples.add("/client?cmd=getclients");
		help.examples.add("/client?cmd=isconnected&id=3344556677");
		help.examples.add("/client?cmd=setname&id=3344556677&name=NewName");
		help.examples.add("/client?cmd=playfile&id=3344556677&file=123543");
		help.examples.add("/client?cmd=playlist&id=3344556677&file=123543&playlist=name");
	}
	
	@Override
	public Object handleService(String[] args, HttpServletRequest request) throws ServiceException {
		String cmd = request.getParameter(CMD);
		if (CMD_GET_CLIENTS.equals(cmd)) {
			List<Client> all = new ArrayList<ClientService.Client>();
			Set<String> clients= phoenix.client.GetConnectedClients();
			for (String s: clients) {
				Client c = new Client();
				c.id=s;
				c.name = phoenix.client.GetName(s);
				all.add(c);
			}
			return all;
		} else if (CMD_PLAY_FILE.equals(cmd)) {
			return playMediaFile(request.getParameter(PARAM_ID), request.getParameter(PARAM_FILE));
		} else if (CMD_PLAYLIST.equals(cmd)) {
			return addToPlaylist(request.getParameter(PARAM_ID), request.getParameter(PARAM_FILE), request.getParameter(PARAM_PLAYLIST_NAME));
		} else if (CMD_IS_CONNECTED.equals(cmd)) {
			return phoenix.client.IsConnected(request.getParameter(PARAM_ID));
		} else if (CMD_SET_NAME.equals(cmd)) {
			String name = request.getParameter(PARAM_NAME);
			if (StringUtils.isEmpty(name)) {
				throw new ServiceException("client_no_name", "Missing Extender Name");
			}
			phoenix.client.SetName(request.getParameter(PARAM_ID), name);
			String newName = phoenix.client.GetName(request.getParameter(PARAM_ID));
			if (StringUtils.isEmpty(newName) || !newName.equals(name)) {
				return false;
			}
			return true;
		} else {
			throw new ServiceException("client_cmd_invalid", "Invalid Client Command: " + cmd);
		}
	}

	private Object addToPlaylist(String id, String file, String playlist) {
		if (playlist==null) playlist = "BMT Web UI";
		log.info("Adding " + file + " to playlist " + playlist);
		Object list[] = PlaylistAPI.GetPlaylists();
		Object plist = null;
		if (list!=null) {
			for (Object o : list) {
				if (playlist.equals(PlaylistAPI.GetName(o))) {
					log.debug("Found existing playlist: " + playlist);
					plist = o;
					break;
				}
			}
		}
		
		if ( plist == null) {
			plist = PlaylistAPI.AddPlaylist(playlist);
			log.info("Created Playlist: " + playlist + "; " + plist);
		}
		
		PlaylistAPI.AddToPlaylist(plist, MediaFileAPI.GetMediaFileForID(NumberUtils.toInt(file)));
		return new Message(0, "Item added to playlist: " + PlaylistAPI.GetName(plist));
	}

	private Object playMediaFile(String ctx, String sageid) {
		try {
			UIContext uictx = new UIContext(ctx);
			Object o = MediaPlayerAPI.Watch(uictx, MediaFileAPI.GetMediaFileForID(NumberUtils.toInt(sageid)));
			if (o instanceof String) {
				throw new Exception((String)o);
			}
			//phoenix.umb.Play(res, ctx);
			Thread.sleep(500);
			WidgetAPI.LaunchMenuWidget(uictx, "MediaPlayer OSD");
			Thread.sleep(200);
			Global.SageCommand(uictx, SageCommandEnum.TV.command());
		} catch (Exception e) {
			log.warn("Failed to play file: " + sageid);
			return new Message(1, e.getMessage());
		}
		
		return new Message(0, "File is playing");
	}

	@Override
	public Help getHelp(HttpServletRequest request) throws IOException {
		return help;
	}
}
