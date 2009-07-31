package org.jdna.media.metadata.impl.nielm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import net.sf.sageplugins.sageimdb.DbTitleObject;
import net.sf.sageplugins.sageimdb.ImdbWebBackend;
import net.sf.sageplugins.sageimdb.Role;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.MediaArt;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;
import org.jdna.media.metadata.impl.imdb.IMDBMovieMetaDataParser;
import org.jdna.media.metadata.impl.imdb.IMDBUtils;

import sagex.phoenix.fanart.MediaArtifactType;

public class NeilmIMDBMetaDataParser {
    private static final Logger log         = Logger.getLogger(NeilmIMDBMetaDataParser.class);

    private ImdbWebBackend      db          = null;
    private DbTitleObject       data        = null;
    private List<ICastMember>    castMembers = null;

    public NeilmIMDBMetaDataParser(ImdbWebBackend db, DbTitleObject data) {
        this.data = data;
        this.db = db;
    }

    public MediaMetadata getMetaData() {
        MediaMetadata md = new MediaMetadata();

        md.getCastMembers().addAll(getCastMembers());
        if (data.getGenres()!=null) {
            md.getGenres().addAll(Arrays.asList(data.getGenres()));
        }
        md.setMPAARating(data.getMPAArating());
        md.set(MetadataKey.MPAA_RATING_DESCRIPTION, IMDBMovieMetaDataParser.parseMPAARating(data.getMPAArating()));
        md.setDescription(data.getSummaries());
        md.setReleaseDate(data.getAiringDate());
        try {
            md.setRuntime(String.valueOf(data.getDuration()));
        } catch (Exception e) {
            log.warn("Failed to call getDuration() in NielmIMDBParser.");
        }
        if (data.getImageURL()!=null) {
            MediaArt ma = new MediaArt();
            ma.setType(MediaArtifactType.POSTER);
            ma.setDownloadUrl(data.getImageURL().toExternalForm());
            md.addMediaArt(ma);
        }
        md.setMediaTitle(data.getName());
        md.setUserRating(data.getRating());
        md.setYear(data.getYear());

        md.setProviderId(NielmIMDBMetaDataProvider.PROVIDER_ID);
        md.setProviderDataUrl(data.getImdbUrl());
        md.setProviderDataId(MetadataAPI.createMetadataIDString(IMDBMetaDataProvider.PROVIDER_ID, IMDBUtils.parseIMDBID(data.getImdbUrl())));
        return md;
    }

    public List<ICastMember> getCastMembers() {
        if (castMembers == null) {
            Vector<Role> cast = data.getCast();
            if (cast != null && cast.size() > 0) {
                int s = cast.size();
                castMembers = new ArrayList<ICastMember>(s);

                for (int i = 0; i < s; i++) {
                    Role role = cast.get(i);
                    CastMember cm = new CastMember(ICastMember.ACTOR);
                    cm.setId(role.getName().getName());
                    cm.setName(role.getName().getName());
                    cm.setPart(role.getPart());
                    castMembers.add(cm);
                }
            }
        }
        if (castMembers==null) castMembers = Collections.EMPTY_LIST;
        return castMembers;
    }
}
