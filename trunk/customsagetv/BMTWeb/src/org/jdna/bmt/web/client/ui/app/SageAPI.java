package org.jdna.bmt.web.client.ui.app;

import java.io.Serializable;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaFile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SageAPI {
    private static SageServiceAsync api = GWT.create(SageService.class); 

    public static void refreshLibrary(boolean fullScan, AsyncCallback<String> callback) {
        api.refreshLibrary(fullScan, callback);
    }
    
    public static void call(String cmd, Serializable[] args) {
        StringBuilder arg = new StringBuilder();
        arg.append("c=").append(cmd);
        if (args!=null && args.length>0) {
            for (int i=0;i<args.length;i++) {
                if (args[i] instanceof GWTMediaFile) {
                    args[i] = "mediafile:" + ((GWTMediaFile)args[i]).getSageMediaFileId();
                }
                arg.append("&").append(String.valueOf(i)).append("=").append(URL.encodeComponent(String.valueOf(args[i])));
            }
        }
        arg.append("&encoder=json");
        final String url = GWT.getModuleBaseURL() + "sageapi/api?"+arg;
        
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    Application.fireErrorEvent("Failed while calling: " + url, exception);
                }

                public void onResponseReceived(Request request, Response response) {
                    JSONObject val = (JSONObject) JSONParser.parse(response.getText());
                    
                    Window.alert(val.get("Result").toString());
                }
            });
        } catch (RequestException e) {
            Application.fireErrorEvent("Failed to call: " + url, e);
        }
    }

    public static <T> void call(String cmd, Object[] args, final AsyncCallback<T> callback) {
        StringBuilder arg = new StringBuilder();
        arg.append("c=").append(cmd);
        if (args!=null && args.length>0) {
            for (int i=0;i<args.length;i++) {
                if (args[i] instanceof GWTMediaFile) {
                    args[i] = "mediafile:" + ((GWTMediaFile)args[i]).getSageMediaFileId();
                }
                arg.append("&").append(String.valueOf(i)).append("=").append(URL.encodeComponent(String.valueOf(args[i])));
            }
        }
        arg.append("&encoder=json");
        final String url = GWT.getModuleBaseURL() + "sageapi/api?"+arg;
        
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    callback.onFailure(new Exception("Url: " + url, exception));
                }

                public void onResponseReceived(Request request, Response response) {
                    try {
                        JSONObject obj = (JSONObject) JSONParser.parse(response.getText());
                        JSONValue val = obj.get("Result");
                        if (val.isBoolean()!=null) {
                            callback.onSuccess((T)Boolean.valueOf(val.isBoolean().booleanValue()));
                        } else if (val.isNumber()!=null) {
                            //callback.onSuccess((T)Integer.valueOf(val.is));
                        }
                    } catch (Exception e) {
                        callback.onFailure(e);
                    }
                    
                    //Window.alert();
                }
            });
        } catch (RequestException e) {
            Application.fireErrorEvent("Failed to call: " + url, e);
        }
    }
    
    public static SageServiceAsync getService() {
        return api;
    }
}
