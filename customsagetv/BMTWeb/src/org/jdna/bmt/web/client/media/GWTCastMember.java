package org.jdna.bmt.web.client.media;

import java.io.Serializable;

import org.jdna.bmt.web.client.util.Property;

import sagex.phoenix.metadata.ICastMember;

public class GWTCastMember implements ICastMember, Serializable {
    private static final long serialVersionUID = 1L;
    private Property<String> role = new Property<String>();
    private Property<String> name = new Property<String>();

    public GWTCastMember() {
    }

    public GWTCastMember(ICastMember copy) {
        this.role.set(copy.getRole());
        this.name.set(copy.getName());
    }
    
    public String getName() {
        return name.get();
    }

	@Override
	public String getRole() {
		return role.get();
	}
	
	public Property<String> getRoleProperty() {
		return role;
	}
	
	public Property<String> getNameProperty() {
		return name;
	}
}
