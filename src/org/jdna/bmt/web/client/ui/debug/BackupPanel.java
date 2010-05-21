package org.jdna.bmt.web.client.ui.debug;

import org.jdna.bmt.web.client.Application;
import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.HorizontalButtonBar;
import org.jdna.bmt.web.client.ui.util.OKDialogHandler;
import org.jdna.bmt.web.client.ui.util.WaitingPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BackupPanel extends Composite {
    private DebugServiceAsync debug = GWT.create(DebugService.class);
    private VerticalPanel panel = new VerticalPanel();
    private VerticalPanel backups = new VerticalPanel();
    public BackupPanel() {
        panel.setWidth("100%");
        
        HorizontalButtonBar hb = new HorizontalButtonBar();
        Button b = new Button(Application.labels().backupWizBin());
        b.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Dialogs.confirm(Application.messages().backupWizBin(), new OKDialogHandler<Void>() {
                    public void onSave(Void data) {
                        final PopupPanel pp = Dialogs.showWaitingPopup("Backing up Wiz.bin...");
                        debug.backupWizBin(new AsyncCallback<Void>() {
                            public void onSuccess(Void result) {
                                pp.hide();
                                refresh();
                                Application.fireNotification("Backup Complete");
                            }
                            
                            public void onFailure(Throwable caught) {
                                pp.hide();
                                Application.fireErrorEvent("Backup Failed!", caught);
                            }
                        });
                    }
                });
            }
        });
        hb.add(b);
        panel.add(hb);
        
        // Add A Panel to get the current backup files...
        Label l = new Label(Application.labels().backupHistory());
        l.addStyleName("Header");
        
        panel.add(l);
        panel.add(backups);
        backups.add(new WaitingPanel());
        
        refresh();
        
        initWidget(panel);
    }

    private void refresh() {
        debug.getWizBinBackups(new AsyncCallback<String[]>() {
            public void onFailure(Throwable caught) {
                Application.fireErrorEvent(Application.messages().unableToGetBackupList(), caught);
            }

            public void onSuccess(String[] result) {
                updateBackupList(result);
            }

        });
    }
    
    private void updateBackupList(String[] result) {
        backups.clear();
        if (result!=null && result.length>0) {
            for (String s: result) {
                Label l = new Label(s);
                l.addStyleName("Data");
                backups.add(l);
            }
        } else {
            backups.add(new Label(Application.labels().nobackupfiles()));
        }
    }
}
