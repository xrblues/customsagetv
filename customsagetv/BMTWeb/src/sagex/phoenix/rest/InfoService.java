package sagex.phoenix.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import sagex.api.Global;
import sagex.api.Version;
import sagex.phoenix.util.SageTV;

/**
 * Most simple service that simple reports back some information about the Sage Server.
 * 
 * @author seans
 *
 */
public class InfoService extends JSONHandler {
	public static final String ID = "info";

	private static class Message {
		public String sage_version;
		public String sagex_version;
		public String phoenix_version;
		public String phoenix_json_version;
		public String java_version;
		public String sage_server_name;
		public boolean phoenix_json_update_available;
	}
	private Help help = null;
	public InfoService() {
		help = new Help();
		help.title=ID;
		help.description = "Returns system information about SageTV and Phoenix";
		help.examples.add("/info");
	}
	
	@Override
	public Object handleService(String[] args, HttpServletRequest request) throws ServiceException {
		Message m = new Message();
		m.java_version = System.getProperty("java.version");
		m.sagex_version = Version.GetVersion();
		m.sage_version = SageTV.getSageVersion();
		m.phoenix_version = phoenix.system.GetVersion();
		m.sage_server_name = Global.GetServerAddress();
		
		// todo: get this from the plugin system.
		// if there is an update required, then the client can
		// request a client update
		m.phoenix_json_version="1.0";
		m.phoenix_json_update_available=false;
		return m;
	}

	@Override
	public Help getHelp(HttpServletRequest request) throws IOException {
		return help;
	}
}
