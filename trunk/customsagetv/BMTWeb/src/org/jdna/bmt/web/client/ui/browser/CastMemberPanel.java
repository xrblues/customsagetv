package org.jdna.bmt.web.client.ui.browser;

import java.util.List;

import org.jdna.bmt.web.client.media.GWTCastMember;
import org.jdna.bmt.web.client.media.GWTMediaFile;
import org.jdna.bmt.web.client.media.GWTMediaMetadata;
import org.jdna.bmt.web.client.ui.util.DataDialog;
import org.jdna.media.metadata.ICastMember;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CastMemberPanel extends Composite implements ClickHandler {
    private VerticalPanel panel = new VerticalPanel();
    private GWTMediaFile file;
    public CastMemberPanel(GWTMediaFile file) {
        panel.setHeight("100%");
        panel.setWidth("100%");
        initWidget(panel);
        
        init(file);
    }

    public void init(GWTMediaFile file2) {
        this.file=file2;
        panel.clear();

        if (file==null) return;
        
        GWTMediaMetadata md = file.getMetadata();
        if (md==null) return;
        
        List<ICastMember> cast = md.getCastMembers();
        if (cast==null || cast.size()==0) return;
        
        for (ICastMember c : cast) {
            CastMemberItem cm = new CastMemberItem(c.getName(), null, this);
            cm.setUserData(c);
            panel.add(cm);
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
    }

    @Override
    protected void onDetach() {
        super.onDetach();
    }

    public void onClick(ClickEvent event) {
        DataDialog.showDialog(new CastMemberDialog((GWTCastMember)((CastMemberItem)event.getSource()).getUserData(), this));
    }

    public void refresh() {
        init(file);
    }
}
