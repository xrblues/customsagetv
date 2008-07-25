package org.jdna.media.metadata.impl.nielm;

import net.sf.sageplugins.sageimdb.DbPersonObject;
import net.sf.sageplugins.sageimdb.ImdbWebBackend;
import net.sf.sageplugins.sageimdb.Role;

import org.jdna.media.metadata.ICastMember;

public class NielmActorRole implements ICastMember {
	private Role role = null;
	private ImdbWebBackend db =null;
	private DbPersonObject data = null;
	private int type;

	public NielmActorRole(int type, ImdbWebBackend db, Role r) {
		this.role = r;
		this.db=db;
	}

	public String getId() {
		return role.getName().getName();
	}

	public String getName() {
		return role.getName().getName();
	}

	public String getPart() {
		return role.getPart();
	}

	public int getType() {
		return type;
	}

	public String getProviderDataUrl() {
		return null;
	}
}
