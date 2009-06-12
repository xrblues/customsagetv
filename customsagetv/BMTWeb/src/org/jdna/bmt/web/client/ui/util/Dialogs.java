package org.jdna.bmt.web.client.ui.util;

import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Label;

public class Dialogs {
    public static void showMessage(String message) {
        showMessage(message, 2000);
    }
    
    public static void showMessage(String message, int time) {
        final DecoratedPopupPanel pop = new DecoratedPopupPanel(true);
        pop.setAnimationEnabled(true);
        pop.center();
        pop.setWidget(new Label(message));
        pop.show();
        Timer t = new Timer() {
            @Override
            public void run() {
                if (pop.isVisible()) {
                    pop.hide();
                }
            }
        };
        t.schedule(time);
        Log.debug("dialog: " + message + "; time: " + time);
    }
}
