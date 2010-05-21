package org.jdna.bmt.web.client;

import org.jdna.bmt.web.client.ui.app.AppPanel;
import org.jdna.bmt.web.client.ui.status.StatusServices;
import org.jdna.bmt.web.client.ui.status.StatusServicesAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BMTWeb implements EntryPoint {
    private static final String Title = "Metadata Tools for SageTV";
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        RootPanel.get().add(new AppPanel());
        Window.setTitle(Title);
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                StatusServicesAsync service = GWT.create(StatusServices.class);
                service.getBMTVersion(new AsyncCallback<String>() {
                    public void onSuccess(String result) {
                        Window.setTitle(Title + "("+result+")");
                    }
                    
                    public void onFailure(Throwable caught) {
                    }
                });
            }
        });
    }
}
