package org.jdna.bmt.web.client.media;

import java.io.Serializable;

import org.jdna.bmt.web.client.util.Property;
import org.jdna.media.metadata.ICastMember;

public class GWTCastMember implements ICastMember, Serializable {
    private static final long serialVersionUID = 1L;
    private Property<Integer> type = new Property<Integer>();
    private Property<String> providerUrl = new Property<String>();
    private Property<String> part = new Property<String>();
    private Property<String> name = new Property<String>();
    private Property<String> id= new Property<String>();

    public GWTCastMember() {
    }

    public GWTCastMember(ICastMember copy) {
        this.type.set(copy.getType());
        this.providerUrl.set(copy.getProviderDataUrl());
        this.part.set(copy.getPart());
        this.name.set(copy.getName());
        this.id.set(copy.getId());
    }
    
    public String getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getPart() {
        return part.get();
    }

    public String getProviderDataUrl() {
        return providerUrl.get();
    }

    public int getType() {
        return type.get();
    }

    public Property<Integer> getTypeProperty() {
        return type;
    }

    public Property<String> getNameProperty() {
        return name;
    }
    
    public Property<String> getPartProperty() {
        return part;
    }
}
