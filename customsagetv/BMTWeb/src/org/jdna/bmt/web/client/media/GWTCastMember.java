package org.jdna.bmt.web.client.media;

import java.io.Serializable;

import org.jdna.media.metadata.ICastMember;

public class GWTCastMember implements ICastMember, Serializable {
    private static final long serialVersionUID = 1L;
    private int type;
    private String providerUrl;
    private String part;
    private String name;
    private String id;

    public GWTCastMember() {
    }

    public GWTCastMember(ICastMember copy) {
        this.type= copy.getType();
        this.providerUrl=copy.getProviderDataUrl();
        this.part=copy.getPart();
        this.name=copy.getName();
        this.id=copy.getId();
    }
    
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPart() {
        return part;
    }

    public String getProviderDataUrl() {
        return providerUrl;
    }

    public int getType() {
        return type;
    }

}
