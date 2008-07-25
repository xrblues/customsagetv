package org.jdna.media.metadata.impl.nielm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import net.sf.sageplugins.sageimdb.DbTitleObject;
import net.sf.sageplugins.sageimdb.ImdbWebBackend;
import net.sf.sageplugins.sageimdb.Role;

import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IVideoMetaData;

public class NeilmIMDBMetaData implements IVideoMetaData {
	private ImdbWebBackend db = null;
	private DbTitleObject data = null;
	private List<ICastMember> actors = null;
	
	public NeilmIMDBMetaData(ImdbWebBackend db, DbTitleObject data) {
		this.data = data;
		this.db = db;
	}

	public List<ICastMember> getActors() {
		if (actors==null) {
			actors = new ArrayList<ICastMember>();
			Vector<Role> cast = data.getCast();
			for (Role r: cast) {
				actors.add(new NielmActorRole(ICastMember.ACTOR, db, r));
			}
		}
		return actors;
	}

	public String getAspectRatio() {
		return null;
	}

	public String getCompany() {
		return null;
	}

	public List<ICastMember> getDirectors() {
		return null;
	}

	public List<String> getGenres() {
		return Arrays.asList(data.getGenres());
	}

	public String getMPAARating() {
		return data.getMPAArating();
	}

	public String getPlot() {
		return data.getSummaries();
	}

	public String getProviderDataUrl() {
		return data.getImdbUrl();
	}

	public String getReleaseDate() {
		return data.getAiringDate();
	}

	public String getRuntime() {
		return String.valueOf(data.getDuration());
	}

	public String getThumbnailUrl() {
		return data.getImageURL().toExternalForm();
	}

	public String getTitle() {
		return data.getName();
	}

	public String getUserRating() {
		return data.getRating();
	}

	public List<ICastMember> getWriters() {
		return null;
	}

	public String getYear() {
		return data.getYear();
	}

	public boolean isUpdated() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getProviderId() {
		return NielmIMDBMetaDataProvider.PROVIDER_ID;
	}

}
