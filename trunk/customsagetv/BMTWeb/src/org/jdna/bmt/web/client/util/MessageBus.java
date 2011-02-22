package org.jdna.bmt.web.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public void postMessage(String id, Map<String, ?> args) {
		for (MessageHandler mh : getHandlers(id)) {
			mh.onMessageReceived(id, args);
		}
	}
}
