package org.jdna.bmt.web.client.util;

import java.util.Map;

public interface MessageHandler {
	public void onMessageReceived(String msg, Map<String,?> args);
}
