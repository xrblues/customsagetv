package org.jdna.bmt.web.client.ui.browser2;

import org.jdna.bmt.web.client.Application;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class ViewManager {
	public static ViewManager INSTANCE = new ViewManager();
	
	public ViewManager() {
	}
	
	public void getViews(final String tag, final IJSONReply reply) {
		String url = "json/views";
		url += ("?tag="+((tag==null)?"all":tag));
		RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, url);
		try {
			rb.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					JSONObject jo = (JSONObject) JSONParser.parseStrict(response.getText());
					JSONValue val = jo.get("reply");
					JSONObject o = val.isObject();
					if (o!=null && o.get("exception")!=null) {
						Application.fireErrorEvent(o.get("message").toString(), null);
					}
					reply.onReply(val);
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					Application.fireErrorEvent("Failed to get views for tag " + tag, exception);
				}
			});
		} catch (RequestException e) {
			Application.fireErrorEvent("Error calling getViews() for " + tag, e);
		}
	}
}
