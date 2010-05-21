package org.jdna.bmt.web.client.ui.browser;

import org.jdna.bmt.web.client.media.GWTFactoryInfo;

import com.google.gwt.event.shared.GwtEvent;

public class FactoriesReplyEvent extends GwtEvent<FactoriesReplyHandler> {
    public static final GwtEvent.Type<FactoriesReplyHandler> TYPE =  new GwtEvent.Type<FactoriesReplyHandler>();
    private GWTFactoryInfo[] info;
    private GWTFactoryInfo.SourceType sourceType;
    
    public FactoriesReplyEvent(GWTFactoryInfo.SourceType sourceType, GWTFactoryInfo[] info) {
        this.info=info;
        this.sourceType=sourceType;
    }
    
    @Override
    protected void dispatch(FactoriesReplyHandler handler) {
        handler.onFactoriesReply(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<FactoriesReplyHandler> getAssociatedType() {
        return TYPE;
    }

    public GWTFactoryInfo[] getFactoryInf0() {
        return info;
    }
    
    public GWTFactoryInfo.SourceType getSourceType() {
        return sourceType;
    }
}
