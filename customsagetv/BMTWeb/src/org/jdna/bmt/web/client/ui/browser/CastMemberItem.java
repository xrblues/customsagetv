package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.ui.util.SideMenuItem;

import sagex.phoenix.metadata.ICastMember;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CastMemberItem extends SideMenuItem<ICastMember> {
    private String title;
    private String role;
    
	public CastMemberItem(String title, String label, String iconUrl, ClickHandler onclick) {
        super(label, iconUrl, onclick);
        this.title=title;
    }

	public CastMemberItem(String title, String label, String role, String iconUrl, ClickHandler onclick) {
        super(label, iconUrl, onclick);
        this.title=title;
        this.role=role;
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
        if (role!=null) {
            l2 = new Label(role);
        } else {
            l2 = new Label(title);
        }
            
        if (l2!=null) {
            l2.setStyleName("CastMember-Title2");
            vp.add(l2);
        }
        
        return vp;
    }
}
