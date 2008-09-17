package org.jdna.media.metadata.impl.nielm;

import java.util.Vector;

import net.sf.sageplugins.sageimdb.DbTitleObject;
import net.sf.sageplugins.sageimdb.ImdbWebBackend;
import net.sf.sageplugins.sageimdb.Role;

import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.VideoMetaData;

public class NeilmIMDBMetaDataParser {
	private ImdbWebBackend db = null;
	private DbTitleObject data = null;
	private CastMember[] actors = null;
	
	public NeilmIMDBMetaDataParser(ImdbWebBackend db, DbTitleObject data) {
		this.data = data;
		this.db = db;
	}
	
	public VideoMetaData getMetaData() {
		VideoMetaData md = new VideoMetaData();
		md.setActors(getActors());
		md.setGenres(data.getGenres());
		md.setMPAARating(data.getMPAArating());
		md.setPlot(data.getSummaries());
		md.setProviderId(NielmIMDBMetaDataProvider.PROVIDER_ID);
		md.setProviderDataUrl(data.getImdbUrl());
		md.setReleaseDate(data.getAiringDate());
		md.setRuntime(String.valueOf(data.getDuration()));
		md.setThumbnailUrl(data.getImageURL().toExternalForm());
		md.setTitle(data.getName());
		md.setUserRating(data.getRating());
		md.setYear(data.getYear());
		return md;
	}
	

	public CastMember[] getActors() {
		if (actors==null) {
			Vector<Role> cast = data.getCast();
			if (cast!=null && cast.size()>0) {
				int s = cast.size();
				actors = new CastMember[s]; 
			
				for (int i=0;i<s;i++) {
					Role role = cast.get(i);
					CastMember cm = new CastMember(ICastMember.ACTOR);
					cm.setId(role.getName().getName());
					cm.setName(role.getName().getName());
					cm.setPart(role.getPart());
					actors[i]=cm;
				}
			}
		}
		return actors;
	}
}
