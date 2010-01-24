package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.media.GWTMediaFolder;

import com.google.gwt.user.client.ui.Widget;

public interface BrowserView {
    public GWTMediaFolder getFolder();
    public void setDisplay(Widget w);
    public void setActionsVisible(boolean b);
    public void back();
}
