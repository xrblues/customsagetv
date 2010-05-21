package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.SideMenuPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ScansPanel extends Composite implements ScanRequestHandler, ScansInProgressHandler {
    private class ScanInfo extends Composite implements ScanUpdateHandler {
        private HandlerRegistration handler = null;
        private VerticalPanel panel = new VerticalPanel();
        private String progressId = null;
        private Label name, date, message, success, failed, done, cancelled;
        
        public ScanInfo(final String progressId) {
            this.progressId = progressId;
            
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
                    MetadataServicesManager.getInstance().requestItemsForProgress(progressId, true);
                }
            });
            success.addStyleName("clickable");

            failed.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    MetadataServicesManager.getInstance().requestItemsForProgress(progressId, false);
                }
            });
            failed.addStyleName("clickable");
            
            panel.add(p);
            
            initWidget(panel);
        }
        
        public void updateDisplay(ProgressStatus status) {
            name.setText(status.getLabel());
            date.setText(String.valueOf(status.getDate()));
            message.setText(status.getStatus());
            success.setText(String.valueOf(status.getSuccessCount()));
            failed.setText(String.valueOf(status.getFailedCount()));
            done.setText(String.valueOf(status.isDone()));
            cancelled.setText(String.valueOf(status.isCancelled()));
        }

        /* (non-Javadoc)
         * @see com.google.gwt.user.client.ui.Composite#onAttach()
         */
        @Override
        protected void onAttach() {
            super.onAttach();
            handler = Application.events().addHandler(ScanUpdateEvent.TYPE, this);
            MetadataServicesManager.getInstance().requestScanProgress(progressId);
        }



        /* (non-Javadoc)
         * @see com.google.gwt.user.client.ui.Composite#onDetach()
         */
        @Override
        protected void onDetach() {
            super.onDetach();
            handler.removeHandler();
        }

        public void onScanUpdate(ScanUpdateEvent event) {
            if (progressId.equals(event.getProgressStatus().getProgressId())) {
                updateDisplay(event.getProgressStatus());
            }
        }
    }
    
    private VerticalPanel panel = new VerticalPanel();
    private SideMenuPanel       views            = new SideMenuPanel(Application.labels().scans());

    private HandlerRegistration scansInProgressHandler = null;
    private HandlerRegistration newScansHandler = null;
    
    private BrowserView           folder           = null;
    
    public ScansPanel(BrowserView folder) {
        this.folder = folder;
        panel.setHeight("100%");
        panel.setWidth("100%");
        views.setWidth("100%");
        panel.add(views);
        panel.setCellWidth(views, "100%");
        initWidget(panel);
    }

    @Override
    protected void onAttach() {
        newScansHandler = Application.events().addHandler(ScanRequestEvent.TYPE, this);
        scansInProgressHandler =  Application.events().addHandler(ScansInProgressEvent.TYPE, this);
        
        // Get the scans in progress
        MetadataServicesManager.getInstance().requestScansInProgress();

        super.onAttach();
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        newScansHandler.removeHandler();
        scansInProgressHandler.removeHandler();
    }

    public void onScanRequest(ScanRequestEvent event) {
        addScanItem(Application.messages().scanLabel(event.getFolder().getTitle()), event.getTrackingId());
    }

    protected void addScanItem(String label, final String trackingId) {
        setVisible(true);
        ScanSideMenuItem item = new ScanSideMenuItem(label,null, trackingId, new ClickHandler() {
            public void onClick(ClickEvent event) {
                event.stopPropagation();
                folder.setDisplay(new ScanInfo(trackingId));
            }
        });
        views.addItem(item);
    }


    public void onScansInProgress(ScansInProgressEvent event) {
        ProgressStatus[] statuses = event.getProgressStatuses();
        if (statuses!=null && statuses.length>0) {
            for (ProgressStatus p: statuses) {
                addScanItem(Application.messages().scanLabel(p.getLabel()), p.getProgressId());
                Application.events().fireEvent(new ScanUpdateEvent(p));
            }
        }
    }
}
