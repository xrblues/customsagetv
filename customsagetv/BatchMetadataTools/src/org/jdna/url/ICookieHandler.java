package org.jdna.url;

import java.util.Map;

public interface ICookieHandler {
	public void handleSetCookie(String url, String cookie);
	public Map<String, String> getCookiesToSend(String url);
}
