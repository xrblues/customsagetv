package org.jdna.bmt.web.client.ui.app;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.animation.Effects;
import org.jdna.bmt.web.client.ui.util.Dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class HeaderTitleSection extends Composite {

    private static HeaderTitleSectionUiBinder uiBinder = GWT.create(HeaderTitleSectionUiBinder.class);

    interface HeaderTitleSectionUiBinder extends UiBinder<Widget, HeaderTitleSection> {
    }

    @UiField Label title;
    @UiField Label server;
    @UiField Panel serverInfo;
    
    public HeaderTitleSection() {
        initWidget(uiBinder.createAndBindUi(this));
        
        server.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Dialogs.showAsDialog("Select Remote API Server", new RemoteAPISelectionPanel(HeaderTitleSection.this));
            }
        });
    }
    
    public void refresh() {
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                SageAPI.getService().getSagexApiConnectionInfo(new AsyncCallback<ConnectionInfo>() {
                    public void onSuccess(ConnectionInfo result) {
                        server.setText(result.getHost());
                        if (result.getPort()>0) {
                            server.setText(result.getHost() + ":" + result.getPort());
                        }
                        
                        if (!"LOCAL".equals(result.getHost())) {
                            Effects.fadeIn(serverInfo, 500);
                        }
                    }
                    
                    public void onFailure(Throwable caught) {
                        server.setText("UNKNOWN");
                    }
                });
            }
        });
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        title.setText(Application.labels().appTitle());
        refresh();
    }
}
