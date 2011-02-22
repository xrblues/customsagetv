package org.jdna.bmt.web.client.ui.browser;

import java.util.HashMap;
import java.util.Map;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.media.GWTMediaFolder;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.SideMenuPanel;
import org.jdna.bmt.web.client.util.MessageHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ScansPanel extends Composite implements HasInProgressStatuses, MessageHandler {
    private static class ScanInfo extends Composite implements MessageHandler {
        private VerticalPanel panel = new VerticalPanel();
        private Label name, date, message, success, failed, done, cancelled;
		private BrowsePanel controller;
        
		private String progId = null;
		public ScanInfo(final BrowsePanel controller, final String progressId) {
			this.controller=controller;
			this.progId = progressId;
			
            Simple2ColFormLayoutPanel p = new Simple2ColFormLayoutPanel();
            p.add("Name", name = new Label());
            p.add("Scan Date/Time", date = new Label());
            p.add("Last Message", message = new Label());
            p.add("Total Success", success = new Label());
            p.add("Total Failed", failed = new Label());
            p.add("Is Done?", done = new Label());
            p.add("Was Cancelled?", cancelled = new Label());

            success.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    controller.requestItemsForProgress(progressId, true);
                }
            });
            success.addStyleName("clickable");

            failed.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    controller.requestItemsForProgress(progressId, false);
                }
            });
            failed.addStyleName("clickable");
            
            panel.add(p);
            
            initWidget(panel);
        }
        
		public void updateProgressStatus(ProgressStatus status) {
			if (progId!=null && progId.equals(status.getProgressId())) {
	            name.setText(status.getLabel());
	            date.setText(String.valueOf(status.getDate()));
	            message.setText(status.getStatus());
	            success.setText(String.valueOf(status.getSuccessCount()));
	            failed.setText(String.valueOf(status.getFailedCount()));
	            done.setText(String.valueOf(status.isDone()));
	            cancelled.setText(String.valueOf(status.isCancelled()));
			}
		}

		@Override
		protected void onAttach() {
			super.onAttach();
			this.controller.getMessageBus().addHandler(BrowsePanel.MSG_PROGRESS_UPDATED, this);
			
			// force this panel to update
			this.controller.requestScanProgress(progId);
		}

		@Override
		protected void onDetach() {
			super.onDetach();
			this.controller.getMessageBus().removeHandler(BrowsePanel.MSG_PROGRESS_UPDATED, this);
		}

		@Override
		public void onMessageReceived(String msg, Map<String, ?> args) {
			updateProgressStatus((ProgressStatus) args.get(BrowsePanel.MSG_PROGRESS_UPDATED));
		}
    }
    
    private VerticalPanel panel = new VerticalPanel();
    private SideMenuPanel       views            = new SideMenuPanel(Application.labels().scans());

	private BrowsePanel controller;
    
    public ScansPanel(BrowsePanel controller) {
        this.controller=controller;
        panel.setHeight("100%");
        panel.setWidth("100%");
        views.setWidth("100%");
        panel.add(views);
        panel.setCellWidth(views, "100%");
        initWidget(panel);
        controller.requestScansInProgress(this);
    }

    protected void addScanItem(String label, final String trackingId) {
        setVisible(true);
        ScanSideMenuItem item = new ScanSideMenuItem(controller, label,null, trackingId, new ClickHandler() {
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                controller.setDisplay(new ScanInfo(controller, trackingId));
            }
        });
        views.addItem(item);
    }

	@Override
	public void setInProgress(ProgressStatus[] statuses) {
		views.clearItems();
        if (statuses!=null && statuses.length>0) {
            for (ProgressStatus p: statuses) {
                addScanItem(Application.messages().scanLabel(p.getLabel()), p.getProgressId());

                // fire event to update the initial progress
                Map<String,Object> args = new HashMap<String, Object>();
                args.put(BrowsePanel.MSG_PROGRESS_UPDATED, p);
                controller.getMessageBus().postMessage(BrowsePanel.MSG_PROGRESS_UPDATED, args);
            }
        }
	}

	@Override
	public void onMessageReceived(String msg, Map<String, ?> args) {
		if (BrowsePanel.MSG_NEW_SCAN_STARTED.equals(msg)) {
	        addScanItem(Application.messages().scanLabel(((GWTMediaFolder)args.get("folder")).getTitle()), (String)args.get("progressid"));
		}
	}

    @Override
	protected void onAttach() {
		super.onAttach();
		this.controller.getMessageBus().addHandler(BrowsePanel.MSG_NEW_SCAN_STARTED, this);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		this.controller.getMessageBus().removeHandler(BrowsePanel.MSG_NEW_SCAN_STARTED, this);
	}
}
