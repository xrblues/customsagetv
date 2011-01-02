package org.jdna.bmt.web.client;

import org.jdna.bmt.web.client.ui.app.AppPanel;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BMTWeb implements EntryPoint {
    private static final String Title = "Batch Metadata Tools";
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
    	Log.debug(Window.Location.getHref());
    	
    	if (Window.Location.getHref().contains("bmt5")) {
    		Application.BMT5=true;
    	}
    	
    	if (Window.Location.getHref().endsWith("mobile")) {
    		Log.debug("Using Mobile");
    		//RootPanel.get().add(new AppPanelMobile());
    	} else {
    		RootPanel.get().add(new AppPanel());
    	}
        Window.setTitle(Title + " (" + Version.VERSION+ ")");
    }
}
