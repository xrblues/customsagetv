package org.jdna.bmt.web.client.ui.layout;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Simple2ColFormLayoutPanel extends Composite {
    private FlexTable grid;
    private String labelStyle = "form-label";

    private String rowStyleNames[] = new String[] {"Row-Even","Row-Odd"};
    
    public Simple2ColFormLayoutPanel() {
        grid =  new FlexTable();
        grid.setWidth("100%");
        grid.setCellSpacing(0);
        grid.setCellPadding(2);
        
        initWidget(grid);
    }
    
    public void add(Widget label) {
        add(label, null);
    }
    
    public void add(String label, Widget editor) {
        add(new Label(label), editor);
    }
    
    public void add(Label label, Widget editor) {
        label.addStyleName(labelStyle);
        label.setWordWrap(false);
        add((Widget)label, editor);
    }

    public void add(Widget label, Widget editor) {
        int i = grid.getRowCount();
        grid.setWidget(i, 0, label);
        String labelWidth="10%";
        if (editor!=null) {
            grid.setWidget(i, 1, editor);
            grid.getCellFormatter().setHorizontalAlignment(i, 1, HorizontalPanel.ALIGN_RIGHT);
            grid.getCellFormatter().addStyleName(i, 1, "RowCell");
        } else {
            labelWidth="100%";
            grid.getFlexCellFormatter().setColSpan(i, 0, 1);
        }
        
        grid.getCellFormatter().setWidth(i, 0, labelWidth);
        grid.getCellFormatter().setWordWrap(i, 0, false);
        grid.getCellFormatter().addStyleName(i, 0, "RowCell");
        
        grid.getRowFormatter().addStyleName(i, rowStyleNames[i%2]);
    }
    
    public void clear() {
        grid.clear();
        for (int i=grid.getRowCount()-1;i>0;i--) {
            grid.removeRow(i);
        }
    }
    
    public int getRowCount() {
        return grid.getRowCount();
    }
    
    public void addRowStyle(String name) {
        grid.getRowFormatter().addStyleName(grid.getRowCount()-1, name);
    }

    public String getLabelStyle() {
        return labelStyle;
    }
    
    public Label createLabel(String lab) {
        Label label = new Label(lab);
        label.addStyleName(labelStyle);
        label.setWordWrap(false);
        return label;
    }
    
    public FlexTable getFlexTable() {
        return grid;
    }
    
    
    public Widget getLabelWidget(int row) {
    	return grid.getWidget(row, 0);
    }
    
    public void stripe() {
        int s = grid.getRowCount();
        
        for (int i=0, r=0;i<s;i++) {
            if (grid.getRowFormatter().isVisible(i)) {
                grid.getRowFormatter().removeStyleName(i, "Row-Even");
                grid.getRowFormatter().removeStyleName(i, "Row-Odd");
                grid.getRowFormatter().addStyleName(i, rowStyleNames[r%2]);
                r++;
            }
        }
    }
}
