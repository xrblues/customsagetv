package org.jdna.bmt.web.client.media;

import org.jdna.bmt.web.client.Application;

public class BackMediaFolder extends GWTMediaFolder {
    private GWTMediaFolder decorate;
    public BackMediaFolder() {
    }

    public BackMediaFolder(GWTMediaFolder backTo) {
        super(null, null);
        this.decorate = backTo;
    }
    
    public GWTMediaFolder getBackToFolder() {
        return decorate;
    }

    @Override
    public String getTitle() {
        return Application.messages().backTo(decorate.getTitle());
    }
}
