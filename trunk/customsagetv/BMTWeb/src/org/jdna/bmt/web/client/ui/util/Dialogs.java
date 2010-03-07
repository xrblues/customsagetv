package org.jdna.bmt.web.client.ui.util;

import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
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
    
    public static void showMessageDialog(String title, String body) {
        VerticalPanel panel = new VerticalPanel();
        panel.setWidth("100%");
        panel.add(new HTML(body));
        Button btn = new Button("close");
        final DialogBox dialog = new DialogBox();
        dialog.setAnimationEnabled(true);
        dialog.setAutoHideEnabled(false);
        dialog.setModal(true);
        dialog.setWidget(panel);
        dialog.setText(title);
        btn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialog.hide();
            }
        });
        panel.add(btn);
        panel.setCellHorizontalAlignment(btn, HasHorizontalAlignment.ALIGN_RIGHT);
        dialog.center();
        dialog.show();
    }

    public static void showAsDialog(String title, Widget body) {
        VerticalPanel panel = new VerticalPanel();
        panel.setWidth("100%");
        panel.add(body);
        Button btn = new Button("close");
        final DialogBox dialog = new DialogBox();
        dialog.setAnimationEnabled(true);
        dialog.setAutoHideEnabled(false);
        dialog.setGlassEnabled(true);
        dialog.setModal(true);
        dialog.setWidget(panel);
        dialog.setText(title);
        btn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                dialog.hide();
            }
        });
        panel.add(btn);
        panel.setCellHorizontalAlignment(btn, HasHorizontalAlignment.ALIGN_RIGHT);
        dialog.center();
        dialog.show();
    }
}
