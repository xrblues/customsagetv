package sagex.phoenix.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class ClientService extends JSONHandler {
	public static final String PARAM_NAME = "name";
	public static final String PARAM_ID = "id";
	public static final String CMD = "cmd";
	public static final String CMD_GET_CLIENTS = "getclients";
	public static final String CMD_IS_CONNECTED = "isconnected";
	public static final String CMD_SET_NAME = "setname";
	
	public static final String ID = "client";
	
	private class Client {
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

	@Override
	public Help getHelp(HttpServletRequest request) throws IOException {
		return help;
	}
}
