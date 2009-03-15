package org.jdna.media.metadata.impl.nielm;

import java.util.Vector;

import net.sf.sageplugins.sageimdb.DbTitleObject;
import net.sf.sageplugins.sageimdb.ImdbWebBackend;
import net.sf.sageplugins.sageimdb.Role;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.MediaArt;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;
import org.jdna.media.metadata.impl.imdb.IMDBMovieMetaDataParser;
import org.jdna.media.metadata.impl.imdb.IMDBUtils;

import sagex.phoenix.fanart.FanartUtil.MediaArtifactType;

public class NeilmIMDBMetaDataParser {
    private static final Logger log         = Logger.getLogger(NeilmIMDBMetaDataParser.class);

    private ImdbWebBackend      db          = null;
    private DbTitleObject       data        = null;
    private CastMember[]        castMembers = null;

    public NeilmIMDBMetaDataParser(ImdbWebBackend db, DbTitleObject data) {
        this.data = data;
        this.db = db;
    }

    public MediaMetadata getMetaData() {
        MediaMetadata md = new MediaMetadata(new MetadataKey[] {
                MetadataKey.CAST_MEMBER_LIST,
                MetadataKey.MEDIA_ART_LIST,
                MetadataKey.DESCRIPTION,
                MetadataKey.GENRE_LIST,
                MetadataKey.MPAA_RATING,
                MetadataKey.MPAA_RATING_DESCRIPTION,
                MetadataKey.POSTER_ART,
                MetadataKey.MEDIA_PROVIDER_DATA_ID,
                MetadataKey.METADATA_PROVIDER_DATA_URL,
                MetadataKey.RELEASE_DATE,
                MetadataKey.RUNNING_TIME,
                MetadataKey.MEDIA_TITLE,
                MetadataKey.USER_RATING,
                MetadataKey.YEAR });

        md.setCastMembers(getCastMembers());
        md.setGenres(data.getGenres());
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
        md.setProviderDataId(new MetadataID(IMDBMetaDataProvider.PROVIDER_ID, IMDBUtils.parseIMDBID(data.getImdbUrl())));
        return md;
    }

    public ICastMember[] getCastMembers() {
        if (castMembers == null) {
            Vector<Role> cast = data.getCast();
            if (cast != null && cast.size() > 0) {
                int s = cast.size();
                castMembers = new CastMember[s];

                for (int i = 0; i < s; i++) {
                    Role role = cast.get(i);
                    CastMember cm = new CastMember(ICastMember.ACTOR);
                    cm.setId(role.getName().getName());
                    cm.setName(role.getName().getName());
                    cm.setPart(role.getPart());
                    castMembers[i] = cm;
                }
            }
        }
        return castMembers;
    }
}
