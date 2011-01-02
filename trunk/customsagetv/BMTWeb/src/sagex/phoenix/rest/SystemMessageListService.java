package sagex.phoenix.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import sagex.api.SystemMessageAPI;

/**
 * Interacts with sage system messages
 * 
 * @author seans
 * 
 */
public class SystemMessageListService extends JSONHandler {
	public static final String ID = "sysmsg";
	public static final String CMD = "cmd";

	private Help help;
	
	public SystemMessageListService() {
		help = new Help();
		help.title = ID;
		help.description = "Fetches System Messages";
		help.parameters.put(CMD, "System Message Command. Currently, only 'list' is supported.");
		help.examples.add("/sysmsg?cmd=list");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object handleService(String[] args, HttpServletRequest request) throws ServiceException {
		Object messages[] = SystemMessageAPI.GetSystemMessages();

		Map reply = new HashMap();
		List mlist = new ArrayList();
		reply.put("messages", mlist);
		if (messages != null) {
			for (Object m : messages) {
				Map msg = new HashMap();
				msg.put("end_time", SystemMessageAPI.GetSystemMessageEndTime(m));
				msg.put("level", SystemMessageAPI.GetSystemMessageLevel(m));
				msg.put("repeat",
						SystemMessageAPI.GetSystemMessageRepeatCount(m));
				msg.put("message", SystemMessageAPI.GetSystemMessageString(m));
				msg.put("time", SystemMessageAPI.GetSystemMessageTime(m));
				msg.put("type_code",
						SystemMessageAPI.GetSystemMessageTypeCode(m));
				msg.put("typce_name",
						SystemMessageAPI.GetSystemMessageTypeName(m));

				Map vars = new HashMap();
				String varNames[] = SystemMessageAPI
						.GetSystemMessageVariableNames(m);
				if (varNames != null) {
					for (String name : varNames) {
						vars.put(name, SystemMessageAPI
								.GetSystemMessageVariable(m, name));
					}
				}
				msg.put("vars", vars);
				mlist.add(msg);
			}
		}

		return reply;
	}

	@Override
	public Help getHelp(HttpServletRequest request) throws IOException {
		return help;
	}
}
