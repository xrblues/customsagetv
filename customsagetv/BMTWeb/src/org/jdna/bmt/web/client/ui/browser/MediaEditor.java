package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.ui.layout.FlowGrid;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MediaEditor extends Composite {
    private MediaResult[] items = null;

    private HorizontalPanel panel = new HorizontalPanel();
    private VerticalPanel panItems = new VerticalPanel();
    private FlowPanel editor = new FlowPanel();
    
    public MediaEditor(MediaResult[] items) {
        this.items=items;

        panel.setWidth("100%");
        panel.setHeight("100%");
        
        panel.add(panItems);
        panel.setCellWidth(panItems, "300px");
        panItems.setWidth("300px");
        
        editor.setWidth("100%");
        editor.setHeight("100%");

        panel.add(editor);
        
        initWidget(panel);
    }

    @Override
    protected void onLoad() {
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
        editor.clear();
        editor.add(new Label(mi.getMediaTitle()));
    }
}
