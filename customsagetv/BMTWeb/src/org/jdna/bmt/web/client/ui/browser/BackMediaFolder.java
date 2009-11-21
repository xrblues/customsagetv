package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.Application;

public class BackMediaFolder extends MediaFolder {
    private MediaFolder decorate;
    public BackMediaFolder() {
    }

    public BackMediaFolder(MediaFolder backTo) {
        super(null, null);
        this.decorate = backTo;
    }
    
    public MediaFolder getBackToFolder() {
        return decorate;
    }

    @Override
    public String getTitle() {
        return Application.messages().backTo(decorate.getTitle());
    }
}
