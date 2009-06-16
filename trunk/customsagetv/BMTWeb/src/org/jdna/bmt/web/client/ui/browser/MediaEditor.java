package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.ui.util.Dialogs;
import org.jdna.bmt.web.client.ui.util.WaitingPanel;
import org.jdna.bmt.web.client.util.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MediaEditor extends Composite {
    private final BrowserServiceAsync browserService = GWT.create(BrowserService.class);

    private MediaResult[] items = null;

    private HorizontalPanel panel = new HorizontalPanel();
    private VerticalPanel panItems = new VerticalPanel();
    private DockPanel editor = new DockPanel();

    private ScrollPanel scrollItems = null;
    private ScrollPanel scrollDetails = null;
    
    public MediaEditor(MediaResult[] items) {
        this.items=items;

        panel.setWidth("100%");
        panel.setHeight("100%");
        
        scrollItems = new ScrollPanel(panItems);
        scrollItems.setWidth("300px");
        panel.add(scrollItems);
        //panel.setCellWidth(panItems, "290px");
        panItems.setWidth("290px");
        
        editor.setWidth("100%");
        editor.setHeight("100%");

        panel.add(editor);
        panel.setCellWidth(editor, "90%");
        initWidget(panel);
        
        DeferredCommand.addCommand(new Command() {
            public void execute() {
                setScrollSize();
            }
        });
        
        Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent event) {
                setScrollSize();
            }
        });
    }

    protected void setScrollSize() {
        scrollItems.setPixelSize((panItems.getOffsetWidth() + 15), panel.getOffsetHeight());
    }

    @Override
    protected void onLoad() {
        scrollItems.setHeight(getOffsetHeight() + "px");
        for (int i=0;i<items.length;i++) {
            final MediaEditorWidget item = new MediaEditorWidget(items[i]);
            item.setWidth("100%");
            item.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    updateMediaEditorPanel(item);
                }
            });
            panItems.add(item);
        }
    }

    protected void updateMediaEditorPanel(MediaEditorWidget item) {
        MediaResult mi = item.getMediaItem();
        final DecoratedPopupPanel waiting = Dialogs.showWaitingPopup("Getting details for " + mi.getMediaTitle());
        browserService.getMediaItem(mi, new AsyncCallback<MediaItem>() {
            public void onFailure(Throwable caught) {
                Dialogs.hidePopup(waiting, 1000);
                editor.clear();
                Log.error("Failed to load metadata.", caught);
            }
            public void onSuccess(MediaItem result) {
                Dialogs.hidePopup(waiting, 1000);
                updateEditorPanel(result);
            }
        });
    }

    protected void updateEditorPanel(MediaItem result) {
        editor.clear();
        MediaEditorPanel details = new MediaEditorPanel(result);
        scrollDetails = new ScrollPanel(details);
        scrollDetails.setWidth("100%");
        scrollDetails.setHeight(panel.getOffsetHeight()+"px");
        editor.add(scrollDetails, DockPanel.CENTER);
        editor.setCellWidth(scrollDetails, "100%");
    }
}