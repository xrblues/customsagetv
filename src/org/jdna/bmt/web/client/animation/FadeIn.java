package org.jdna.bmt.web.client.animation;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;

public class FadeIn extends Animation {
    private Widget widget;
    private Style style;
    public FadeIn(Widget w) {
        widget = w;
        style = widget.getElement().getStyle();
    }
    
    @Override
    protected void onUpdate(double progress) {
        style.setOpacity(progress);
    }

    /* (non-Javadoc)
     * @see com.google.gwt.animation.client.Animation#onComplete()
     */
    @Override
    protected void onComplete() {
        super.onComplete();
        style.setOpacity(1.0);
    }

    /* (non-Javadoc)
     * @see com.google.gwt.animation.client.Animation#onStart()
     */
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        style.setOpacity(0.0);
        widget.setVisible(true);
    }
}
