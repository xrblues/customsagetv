package org.jdna.bmt.web.client.animation;

import com.google.gwt.user.client.ui.Widget;

public class Effects {
    public static Widget fadeIn(Widget w, int duration) {
        FadeIn eff = new FadeIn(w);
        eff.run(duration);
        return w;
    }
}
