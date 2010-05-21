package org.jdna.bmt.web.client.ui.browser;

import java.util.ArrayList;
import java.util.List;

import org.jdna.bmt.web.client.media.GWTCastMember;
import org.jdna.bmt.web.client.ui.input.InputBuilder;
import org.jdna.bmt.web.client.ui.input.NVP;
import org.jdna.bmt.web.client.ui.layout.Simple2ColFormLayoutPanel;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.bmt.web.client.ui.util.DialogHandler;
import org.jdna.bmt.web.client.util.StringIntAdapterProperty;
import org.jdna.media.metadata.ICastMember;

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
        List<NVP<String>> list = new ArrayList<NVP<String>>();
        list.add(new NVP<String>("Director", String.valueOf(ICastMember.DIRECTOR)));
        list.add(new NVP<String>("Writer", String.valueOf(ICastMember.WRITER)));
        list.add(new NVP<String>("Actor", String.valueOf(ICastMember.ACTOR)));
        list.add(new NVP<String>("Other", String.valueOf(ICastMember.OTHER)));
        p.add("Type", InputBuilder.combo(list).bind(new StringIntAdapterProperty(getData().getTypeProperty())).widget());
        p.add("Name", InputBuilder.textbox().bind(getData().getNameProperty()).widget());
        p.add("Role", InputBuilder.textbox().bind(getData().getPartProperty()).widget());
        return p;
    }
}
