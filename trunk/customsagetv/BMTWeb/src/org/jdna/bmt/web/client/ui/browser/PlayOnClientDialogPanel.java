package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.json.JSON;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.ui.util.Dialogs.NeedsDialog;
import org.jdna.bmt.web.client.ui.util.WaitingPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PlayOnClientDialogPanel extends Composite implements NeedsDialog {

	private static final String ADD_TO_PLAYLIST = "Add to playlist";
	private static final String PLAYLIST_NAME = "BMT Web UI";
	
	private static PlayOnClientDialogPanelUiBinder uiBinder = GWT.create(PlayOnClientDialogPanelUiBinder.class);
	@UiField VerticalPanel connectedClients;
	private DialogBox dialog;
	private GWTMediaFile file;

	interface PlayOnClientDialogPanelUiBinder extends
			UiBinder<Widget, PlayOnClientDialogPanel> {
	}

	public PlayOnClientDialogPanel(GWTMediaFile file) {
		initWidget(uiBinder.createAndBindUi(this));
	
		this.file=file;
		
		connectedClients.add(new WaitingPanel());

		final String url = "json/client?cmd=getclients";
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    Application.fireErrorEvent("Failed while calling: " + url, exception);
                }

                public void onResponseReceived(Request request, Response response) {
                	if (200 == response.getStatusCode()) {
                		JSONObject val = (JSONObject) JSONParser.parseLenient(response.getText());
                		if (val !=null) {
                			JSONArray arr = (JSONArray) val.get("reply");
                			updateClientList(arr);
                		}
                	} else {
                		onError(request, new Exception(response.getText()));
                	}
                }
            });
        } catch (RequestException e) {
            Application.fireErrorEvent("Failed to call: " + url, e);
        }
	}

	protected void updateClientList(JSONArray arr) {
		connectedClients.clear();
		for (int i=0;i<arr.size();i++) {
			JSONObject jo = (JSONObject) arr.get(i);
			connectedClients.add(new ClientItem(this, JSON.getString(jo, "name"), JSON.getString(jo, "id")));
		}
		
		connectedClients.add(new ClientItem(this, ADD_TO_PLAYLIST, PLAYLIST_NAME));
	}

	public void playFileForClient(final ClientItem item) {
		if (PLAYLIST_NAME.equals(item.getId())) {
			final String url = "json/client?cmd=playlist&file="+file.getSageMediaFileId();
			GWT.log("Playing URL: " + url);
	        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
	        try {
	            builder.sendRequest(null, new RequestCallback() {
	                public void onError(Request request, Throwable exception) {
	                    Application.fireErrorEvent("Failed while calling: " + url, exception);
	                }
	
	                public void onResponseReceived(Request request, Response response) {
	                	GWT.log(response.getText());
	                	if (200 == response.getStatusCode()) {
	                		Application.fireNotification("Added " + file.getTitle() + " to playlist " + item.getName());
	                	} else {
	                		onError(request, new Exception(response.getText()));
	                	}
	                }
	            });
	        } catch (RequestException e) {
	            Application.fireErrorEvent("Failed to call: " + url, e);
	        }
		} else {
			final String url = "json/client?cmd=playfile&id=" +item.getId() + "&file="+file.getSageMediaFileId();
			GWT.log("Playing URL: " + url);
	        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
	        try {
	            builder.sendRequest(null, new RequestCallback() {
	                public void onError(Request request, Throwable exception) {
	                    Application.fireErrorEvent("Failed while calling: " + url, exception);
	                }
	
	                public void onResponseReceived(Request request, Response response) {
	                	if (200 == response.getStatusCode()) {
	                		Application.fireNotification("Playing mediafile " + file.getTitle() + " on client " + item.getName());
	                	} else {
	                		onError(request, new Exception(response.getText()));
	                	}
	                }
	            });
	        } catch (RequestException e) {
	            Application.fireErrorEvent("Failed to call: " + url, e);
	        }
		} 
		
		dialog.hide();
	}

	@Override
	public void setDialogReference(DialogBox dialog) {
		this.dialog = dialog;
	}
}
