package org.jdna.bmt.web.client.ui.app;

import org.jdna.bmt.web.client.Application;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class RemoteAPISelectionPanel extends Composite {
    private static RemoteAPISelectionPanelUiBinder uiBinder = GWT.create(RemoteAPISelectionPanelUiBinder.class);

    interface RemoteAPISelectionPanelUiBinder extends UiBinder<Widget, RemoteAPISelectionPanel> {
    }

    private HeaderTitleSection headerTitleSection;
    
    @UiField ListBox remoteProviders;
    @UiField Button cmdSelect;
    public RemoteAPISelectionPanel(HeaderTitleSection headerTitleSection) {
        initWidget(uiBinder.createAndBindUi(this));
        this.headerTitleSection = headerTitleSection;
        remoteProviders.clear();
        remoteProviders.addItem("Loading...");
        SageAPI.getService().getSagexApiConnections(new AsyncCallback<ConnectionInfo[]>() {
            public void onSuccess(ConnectionInfo[] result) {
                remoteProviders.clear();
                for (ConnectionInfo c: result) {
                    remoteProviders.addItem(c.getHost() + ":" + c.getPort(), c.getHost());
                }
            }
            
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("Failed to get the remote server list");
            }
        });
    }

    @UiHandler("cmdSelect")
    public void onSelect(ClickEvent evt) {
        SageAPI.getService().setSagexApiConnection(remoteProviders.getValue(remoteProviders.getSelectedIndex()), new AsyncCallback<Void>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent("Failed to set new remote provider: " + caught.getMessage(), caught);
            }

            public void onSuccess(Void result) {
                headerTitleSection.refresh();
            }
        });
    }
}
