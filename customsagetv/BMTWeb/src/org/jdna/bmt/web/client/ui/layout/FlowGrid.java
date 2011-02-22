package org.jdna.bmt.web.client.ui.layout;

import java.util.Iterator;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class FlowGrid extends Composite implements HasWidgets {
    private int widgetCount=0;
    private int cols = 0;
    private FlexTable grid = new FlexTable();
    
    public FlowGrid() {
        initWidget(grid);
    }

    public FlowGrid(int cols) {
        this.cols=cols;
        initWidget(grid);
    }
    
    public void setCols(int cols) {
        this.cols = cols;
    }

    public void add(String text) {
        add(new HTML(text));
    }
    
    public void add(Widget w) {
        int col = widgetCount % (cols);
        int row = (int)((float)widgetCount / (float)cols);
        
        grid.setWidget(row, col, w);
        grid.getCellFormatter().setAlignment(row, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
        widgetCount++;
    }

    public void clear() {
        grid.clear();
        widgetCount=0;
    }

    public Iterator<Widget> iterator() {
        return grid.iterator();
    }

    public boolean remove(Widget w) {
        return grid.remove(w);
    }
}
