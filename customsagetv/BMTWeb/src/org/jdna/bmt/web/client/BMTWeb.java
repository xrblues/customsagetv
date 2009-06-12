package org.jdna.bmt.web.client;

import org.jdna.bmt.web.client.ui.app.AppPanel;
import org.jdna.metadataupdater.Version;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BMTWeb implements EntryPoint {
    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String        SERVER_ERROR    = "An error occurred while " + "attempting to contact the server. Please check your network " + "connection and try again.";

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        // Add the nameField and sendButton to the RootPanel
        // Use RootPanel.get() to get the entire body element
        //RootPanel.get().add(new PreferencesPanel());
        // RootPanel.get().add(new StatusPanel());
        RootPanel.get().add(new AppPanel());
        
        Window.setTitle("Metadata Tools - " + Version.VERSION);
    }
}
