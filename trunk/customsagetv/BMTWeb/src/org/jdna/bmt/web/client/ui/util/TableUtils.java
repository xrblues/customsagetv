package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.user.client.ui.FlexTable;

public class TableUtils {
    private static String rowStyleNames[] = new String[] {"Row-Even","Row-Odd"};

    public static void stripe(FlexTable grid) {
        int s = grid.getRowCount();
        
        for (int i=0, r=0; i<s; i++) {
            if (grid.getRowFormatter().isVisible(i)) {
                grid.getRowFormatter().removeStyleName(i, "Row-Even");
                grid.getRowFormatter().removeStyleName(i, "Row-Odd");
                grid.getRowFormatter().addStyleName(i, rowStyleNames[r%2]);
                r++;
            }
        }
    }
}
