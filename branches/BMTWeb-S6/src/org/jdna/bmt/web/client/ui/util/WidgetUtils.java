package org.jdna.bmt.web.client.ui.util;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class WidgetUtils {
    public static Widget getWidgetForId(Panel panel, String id) {
        for (Widget w : panel) {
            if (id.equals(w.getElement().getId())) {
                return w;
            }
         }
        return null;
    }
}
