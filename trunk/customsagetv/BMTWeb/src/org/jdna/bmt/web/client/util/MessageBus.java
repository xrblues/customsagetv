package org.jdna.bmt.web.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdna.bmt.web.client.ui.input.NVP;

/**
 * Very simply message bus
 * 
 * @author sean
 */
public class MessageBus {
	private Map<String, List<MessageHandler>> msgMap = new HashMap<String, List<MessageHandler>>();
	
	public void addHandler(String msgId, MessageHandler handler) {
		List<MessageHandler> handlers = getHandlers(msgId);
		if (!handlers.contains(handler)) {
			handlers.add(handler);
		}
	}
	
	public boolean removeHandler(String msgId, MessageHandler handler) {
		return getHandlers(msgId).remove(handler);
	}
	
	private synchronized List<MessageHandler> getHandlers(String msgId) {
		List<MessageHandler> h = msgMap.get(msgId);
		if (h==null) {
			h = new ArrayList<MessageHandler>();
			msgMap.put(msgId, h);
		}
		return h;
	}

	/**
	 * converts the args into a map where each arg is assigned value# as the key
	 *  
	 * @param id
	 * @param args
	 */
	public void postMessage(String id, Object... args) {
		Map<String, Object> data = new HashMap();
		if (args!=null&&args.length>0) {
			for (int i=0;i<args.length;i++) {
				data.put("value"+i, args[i]);
			}
		}
		postMessage(id, data);
	}

	/**
	 * Posts a message where each object is a name value pair
	 * @param id
	 * @param args
	 */
	public void postMessage(String id, NVP<?>... args) {
		Map<String, Object> data = new HashMap();
		if (args!=null&&args.length>0) {
			for (int i=0;i<args.length;i++) {
				data.put(args[i].getName(), args[i].getValue());
			}
		}
		postMessage(id, data);
	}

	public void postMessage(String id, Map<String, ?> args) {
		for (MessageHandler mh : getHandlers(id)) {
			mh.onMessageReceived(id, args);
		}
	}
	
	public void postMessage(String id) {
		postMessage(id, (Map)null);
	}
}
