package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.ui.util.SideMenuItem;
import org.jdna.media.metadata.ICastMember;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CastMemberItem extends SideMenuItem<ICastMember> {

    public CastMemberItem(String label, String iconUrl, ClickHandler onclick) {
        super(label, iconUrl, onclick);
    }

    /* (non-Javadoc)
     * @see org.jdna.bmt.web.client.ui.util.SideMenuItem#createLabelWidget()
     */
    @Override
    protected Widget createLabelWidget() {
        VerticalPanel vp = new VerticalPanel();
        vp.setWidth("100%");
        ICastMember cm = getUserData();
        
        Label l1 = new Label(cm.getName());
        l1.setWordWrap(false);
        l1.setStyleName("CastMember-Title1");
        vp.add(l1);
        
        Label l2 = null;
        if (cm.getType() == ICastMember.ACTOR) {
            if (cm.getPart()!=null) {
                l2 = new Label(cm.getPart());
            } else {
                l2 = new Label("Actor");
            }
        }
        
        if (cm.getType() == ICastMember.DIRECTOR) {
            l2 = new Label("Director");
        }
        
        if (cm.getType() == ICastMember.WRITER) {
            l2 = new Label("Writer");
        }

        if (cm.getType() == ICastMember.OTHER) {
            l2 = new Label("Other");
        }
        
        if (l2!=null) {
            l2.setStyleName("CastMember-Title2");
            vp.add(l2);
        }
        
        return vp;
    }
}
