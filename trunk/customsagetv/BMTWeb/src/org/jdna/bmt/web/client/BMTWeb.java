package org.jdna.bmt.web.client;

import org.jdna.bmt.web.client.ui.app.AppPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BMTWeb implements EntryPoint {
    private static final String Title = "Metadata Tools for SageTV7";
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        RootPanel.get().add(new AppPanel());
        Window.setTitle(Title + " (" + Version.VERSION+ ")");
    }
}
