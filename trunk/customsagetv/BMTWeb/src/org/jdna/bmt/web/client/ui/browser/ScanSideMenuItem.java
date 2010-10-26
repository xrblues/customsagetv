package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.ui.util.CommandItem;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar;
import org.jdna.bmt.web.client.ui.util.SideMenuItem;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ScanSideMenuItem extends SideMenuItem implements ScanUpdateHandler {
    private class ItemActions extends Composite {
        private DockPanel panel = new DockPanel();
        private Label status = new Label();
        
        public ItemActions(Widget primaryLabel) {
            panel.setWidth("100%");
            panel.setHeight("100%");
            panel.add(primaryLabel, DockPanel.NORTH);
            status.addStyleName("ScanSideMenuItem-labelStatus");
            status.setWordWrap(true);
            status.setText("Waiting for update...");
            panel.add(status, DockPanel.CENTER);
            
            HorizontalButtonBar buttons = new HorizontalButtonBar();
            //buttons.basicStyle();
            buttons.setWidth("100%");
            
            CommandItem cancel = new CommandItem("images/16x16/process-stop.png", "Cancel", new Command() {
                public void execute() {
                    BrowsingServicesManager.getInstance().cancelScan(progressId);
                }
            });
            buttons.add(cancel);
            
            CommandItem remove = new CommandItem("images/16x16/edit-delete.png", "Remove", new Command() {
                public void execute() {
                    BrowsingServicesManager.getInstance().removeScan(progressId);
                    removeItem();
                }
            });
            buttons.add(remove);
            
            panel.add(buttons, DockPanel.SOUTH);
            panel.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_RIGHT);
            panel.setCellWidth(buttons, "100%");
            initWidget(panel);
        }
        
        public void setStatusText(String text) {
            status.setText(text);
        }
    }
    
    private String progressId = null;
    private HandlerRegistration handler = null;
    private ProgressStatus progressStatus = null;
    
    public ScanSideMenuItem(String label, String iconUrl, String progressId, ClickHandler onclick) {
        super(label, iconUrl, onclick);
        this.progressId = progressId;
    }
    
    public void removeItem() {
        removeFromParent();
    }
    
    /* (non-Javadoc)
     * @see org.jdna.bmt.web.client.ui.util.SideMenuItem#createLabelWidget()
     */
    @Override
    protected Widget createLabelWidget() {
        return new ItemActions(super.createLabelWidget());
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        handler = Application.events().addHandler(ScanUpdateEvent.TYPE, this);
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
            progressStatus =  event.getProgressStatus();
            
            if (event.getProgressStatus().isCancelled()) {
                setStatus(Application.messages().cancelledWithStatus(event.getProgressStatus().getSuccessCount(), event.getProgressStatus().getFailedCount()));
                setBusy(false);
            } else if (event.getProgressStatus().isDone()) {
                setStatus(Application.messages().completeWithStatus(event.getProgressStatus().getSuccessCount(), event.getProgressStatus().getFailedCount()));
                setBusy(false);
            } else {
                setBusy(true);
                setStatus(Application.messages().scanStatus(event.getProgressStatus().getStatus(), (event.getProgressStatus().getWorked())));
            }
        }
    }
    
    public void setStatus(String status) {
        if (getLabelWidget()!=null) {
            ((ItemActions)getLabelWidget()).setStatusText(status);
        }
    }
    
    public ProgressStatus getProgress() {
        return progressStatus;
    }
}
