package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.ui.layout.FlowGrid;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;

public class MediaBrowser extends Composite {
    private MediaResult[] items = null;
    private FlowGrid grid = null;
    private DockPanel panel = null;
    
    public MediaBrowser(MediaResult[] items) {
        this.items=items;
        
        panel=new DockPanel();
        panel.setSpacing(5);
        
        grid = new FlowGrid(8);
        panel.add(grid, DockPanel.CENTER);
        
        initWidget(panel);
    }

    @Override
    protected void onLoad() {
        for (int i=0;i<items.length;i++) {
            MediaItemWidget item = new MediaItemWidget(items[i]);
            item.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    System.out.println("Clicked");
                }
            });
            grid.add(item);
        }
    }
    
}
