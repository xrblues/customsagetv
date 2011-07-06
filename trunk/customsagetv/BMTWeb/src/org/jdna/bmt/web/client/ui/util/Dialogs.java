package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
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
	private static PopupPanel modelWaitingDialog=null;
	
	public interface NeedsDialog {
		public void setDialogReference(DialogBox dialog);
	}
	
    public static PopupPanel showWaitingPopup(String message) {
        DecoratedPopupPanel pop = new DecoratedPopupPanel(true);
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
    
    public static void showWaiting(String message) {
    	modelWaitingDialog = showWaitingPopup(message);
    }
    
    public static void hideWaiting() {
    	if (modelWaitingDialog!=null) {
    		modelWaitingDialog.hide();
    	}
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

    public static DialogBox showAsDialog(String title, Widget body) {
        VerticalPanel panel = new VerticalPanel();
        panel.setWidth("100%");
        panel.add(body);
        body.setWidth("100%");
        Button btn = new Button("close");
        final DialogBox dialog = new DialogBox();
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
        dialog.setAutoHideOnHistoryEventsEnabled(true);
        
        if (body instanceof NeedsDialog) {
        	((NeedsDialog) body).setDialogReference(dialog);
        }
        
        dialog.center();
        dialog.show();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
                dialog.center();
			}
        });
        return dialog;
    }

    public static void confirm(String message, DialogHandler<Void> handler) {
        if (Window.confirm(message)) {
            handler.onSave(null);
        }
    }
    
    public static DialogBox show(final DialogBox dialog) {
    	dialog.setGlassEnabled(true);
    	dialog.setModal(true);
    	dialog.setAutoHideOnHistoryEventsEnabled(true);
        dialog.center();
        dialog.show();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
                dialog.center();
			}
        });
        return dialog;
    }
}
