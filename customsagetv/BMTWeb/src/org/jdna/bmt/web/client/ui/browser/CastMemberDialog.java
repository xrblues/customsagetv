package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.media.GWTCastMember;
import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.DialogHandler;

import com.google.gwt.user.client.ui.Widget;

public class CastMemberDialog extends DataDialog<GWTCastMember> implements DialogHandler<GWTCastMember>{
    private CastMemberPanel castPanel = null;
    public CastMemberDialog(GWTCastMember data, CastMemberPanel castMemberPanel) {
        super("Cast Member", data, null);
        this.castPanel=castMemberPanel;
        setHandler(this);
        setCloseOnSave(true);
        initPanels();
    }

    public void onCancel() {
        hide();
    }

    public void onSave(GWTCastMember data) {
        hide();
        castPanel.refresh();
    }

    /* (non-Javadoc)
     * @see org.jdna.bmt.web.client.ui.util.DataDialog#getBodyWidget()
     */
    @Override
    protected Widget getBodyWidget() {
        Simple2ColFormLayoutPanel p = new Simple2ColFormLayoutPanel();
        p.add("Name", InputBuilder.textbox().bind(getData().getNameProperty()).widget());
        p.add("Role", InputBuilder.textbox().bind(getData().getRoleProperty()).widget());
        return p;
    }
}
