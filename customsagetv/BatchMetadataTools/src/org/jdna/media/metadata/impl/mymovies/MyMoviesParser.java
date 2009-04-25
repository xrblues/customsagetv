package org.jdna.media.metadata.impl.mymovies;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.MediaArt;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sagex.phoenix.fanart.FanartUtil.MediaArtifactType;

public class MyMoviesParser {
    private static final Logger          log         = Logger.getLogger(MyMoviesParser.class);

    private String                       id          = null;

    private MyMoviesMetadataProvider provider    = null;
    private MyMoviesXmlFile               movieFile     = null;
    private Element                      node        = null;

    private List<ICastMember>            castMembers = new ArrayList<ICastMember>();               ;
    private List<String>                 genres      = new ArrayList<String>();

    private MediaMetadata                metadata;

    public MyMoviesParser(String id) throws Exception {
        this.id = id;
        this.provider = (MyMoviesMetadataProvider) MediaMetadataFactory.getInstance().getProvider(MyMoviesMetadataProvider.PROVIDER_ID);
        this.movieFile = provider.getMyMoviesXmlFile();
        this.node = movieFile.findMovieById(id);
    }

    public MediaMetadata getMetaData() {
        metadata = new MediaMetadata(new MetadataKey[] {
                MetadataKey.ASPECT_RATIO,
                MetadataKey.COMPANY,
                MetadataKey.DESCRIPTION,
                MetadataKey.GENRE_LIST,
                MetadataKey.MEDIA_ART_LIST,
                MetadataKey.MPAA_RATING,
                MetadataKey.POSTER_ART,
                MetadataKey.MEDIA_PROVIDER_DATA_ID,
                MetadataKey.METADATA_PROVIDER_ID,
                MetadataKey.METADATA_PROVIDER_DATA_URL,
                MetadataKey.RELEASE_DATE,
                MetadataKey.RUNNING_TIME,
                MetadataKey.MEDIA_TITLE,
                MetadataKey.USER_RATING,
                MetadataKey.YEAR });

        if (castMembers != null) {
            metadata.setCastMembers(getCastMembers().toArray(new CastMember[castMembers.size()]));
        }
        metadata.setAspectRatio(getAspectRatio());
        metadata.setCompany(getCompany());
        metadata.setGenres(getGenres().toArray(new String[genres.size()]));
        metadata.setMPAARating(getMPAARating());
        metadata.setDescription(getPlot());
        metadata.setReleaseDate(getReleaseDate());
        metadata.setRuntime(getRuntime());

        IMediaArt ma = getMediaArtImage("f");
        if (ma != null) {
            metadata.addMediaArt(ma);
        }

        metadata.setMediaTitle(getTitle());
        metadata.setUserRating(getUserRating());
        metadata.setYear(getYear());

        metadata.setProviderId(MyMoviesMetadataProvider.PROVIDER_ID);
        metadata.setProviderDataId(new MetadataID(MyMoviesMetadataProvider.PROVIDER_ID, getProviderUrl()));
        metadata.setProviderDataUrl(id);
        return metadata;
    }

    private List<ICastMember> getCastMembers() {
        if (castMembers == null) {
            castMembers = getActors();

            // add in others
            NodeList nl = node.getElementsByTagName("Credit");
            for (int i = 0; i < nl.getLength(); i++) {
                Element e = (Element) nl.item(i);
                String credType = e.getAttribute("CreditType");
                CastMember cm = new CastMember();
                cm.setName(String.format("%s %s", e.getAttribute("FirstName"), e.getAttribute("LastName")));
                cm.setPart(credType);
                if ("Direction".equals(credType)) {
                    cm.setType(ICastMember.DIRECTOR);
                } else if ("Writing".equals(credType)) {
                    cm.setType(ICastMember.WRITER);
                } else {
                    cm.setType(ICastMember.OTHER);
                }
                castMembers.add(cm);
            }
        }
        return castMembers;
    }

    // TOOD: People
    public List<ICastMember> getActors() {
        List<ICastMember> actors = new ArrayList<ICastMember>();
        if (actors.size() == 0) {
            NodeList nl = node.getElementsByTagName("Actor");
            for (int i = 0; i < nl.getLength(); i++) {
                Element e = (Element) nl.item(i);
                CastMember cm = new CastMember(ICastMember.ACTOR);
                cm.setName(String.format("%s %s", e.getAttribute("FirstName"), e.getAttribute("LastName")));
                cm.setPart(e.getAttribute("Role"));
                actors.add(cm);
            }
        }
        return actors;
    }

    
    public String getAspectRatio() {
        return MyMoviesXmlFile.getElementValue(node, "AspectRatio");
    }

    // TODO: Studios
    public String getCompany() {
        return MyMoviesXmlFile.getElementValue(node, "Studio");
    }

    // TODO: Genres
    public List<String> getGenres() {
        if (genres.size() == 0) {
            NodeList nl = node.getElementsByTagName("Genre");
            for (int i = 0; i < nl.getLength(); i++) {
                Element e = (Element) nl.item(i);
                genres.add(e.getTextContent());
            }
        }
        return genres;
    }

    public String getMPAARating() {
        // TODO: Find Rating
        return MyMoviesXmlFile.getElementValue(node, "Rating");
    }

    public String getPlot() {
        return StringUtils.removeHtml(MyMoviesXmlFile.getElementValue(node, "Description"));
    }

    public String getProviderUrl() {
        return id;
    }

    public String getProviderId() {
        return MyMoviesMetadataProvider.PROVIDER_ID;
    }

    public String getReleaseDate() {
        return MyMoviesXmlFile.getElementValue(node, "ReleaseDate");
    }

    public String getRuntime() {
        return MyMoviesXmlFile.getElementValue(node, "RunningTime");
    }

    // TODO: Find Cover
    public IMediaArt getMediaArtImage(String type) {
        File f = new File(provider.getImagesDir(), id + type + ".jpg");
        if (!f.exists()) {
            log.warn("Missing Cover for Movie: " + id + "; " + getTitle());
        } else {
            try {
                String uri = f.toURI().toURL().toExternalForm();
                MediaArt ma = new MediaArt();
                ma.setProviderId(getProviderId());
                ma.setDownloadUrl(uri);
                ma.setType(MediaArtifactType.POSTER);
                ma.setLabel("f".equals(type) ? "Front" : "Back");
                return ma;
            } catch (MalformedURLException e) {
                log.error("Failed to create url for thumbnail on movie: " + id + "; " + getTitle());
            }
        }
        return null;
    }

    public String getTitle() {
        return StringUtils.removeHtml(MyMoviesXmlFile.getElementValue(node, "LocalTitle"));
    }

    public String getUserRating() {
        // Didn't find user rating in profile information
        return null;
    }

    public String getYear() {
        return MyMoviesXmlFile.getElementValue(node, "ProductionYear");
    }
}
