package org.jdna.bmt.web.client.ui.util;

import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

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

    public static PopupPanel showWaitingPopup(String message) {
        DecoratedPopupPanel pop = new DecoratedPopupPanel(true);
        pop.setAnimationEnabled(true);
        pop.center();
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(new Label(message));
        Widget w = new WaitingPanel();
        w.setWidth("40px");
        hp.add(w);
        pop.setWidget(hp);
        pop.show();
        return pop;
    }

    public static void hidePopup(final PopupPanel popup, final int ms) {
        if (popup==null) return;
        if (ms > 0) {
            Timer t = new Timer() {
                @Override
                public void run() {
                    popup.hide();
                }
            };
            t.schedule(ms);
        } else {
            popup.hide();
        }
    }
}
