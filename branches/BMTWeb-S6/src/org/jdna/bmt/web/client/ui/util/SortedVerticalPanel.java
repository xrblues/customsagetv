package org.jdna.bmt.web.client.ui.util;

import java.util.Comparator;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SortedVerticalPanel<T> extends Composite {
    private VerticalPanel panel = new VerticalPanel();
    private Comparator<T> comparator=null;
    
    public SortedVerticalPanel(Comparator<T> comparator) {
        initWidget(panel);
        this.comparator=comparator;
    }
    
    @SuppressWarnings("unchecked")
    public void add(Widget widget) {
        int s = panel.getWidgetCount();
        int index=-1;
        for (int i=0;i<s;i++) {
            Widget w = panel.getWidget(i);
            if (comparator.compare((T)widget, (T)w)<0) {
                index=i;
                break;
            }
        }
        if (index==-1) {
            panel.add(widget);
        } else {
            panel.insert((Widget) widget, index);
        }
    }
}
