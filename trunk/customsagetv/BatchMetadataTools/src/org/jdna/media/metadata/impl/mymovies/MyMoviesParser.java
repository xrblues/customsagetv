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
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.util.DOMUtils;
import org.jdna.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sagex.phoenix.fanart.MediaArtifactType;

public class MyMoviesParser {
    private static final Logger      log         = Logger.getLogger(MyMoviesParser.class);

    private String                   id          = null;

    private MyMoviesMetadataProvider provider    = null;
    private MyMoviesXmlFile          movieFile   = null;
    private Element                  node        = null;

    private List<ICastMember>        castMembers = new ArrayList<ICastMember>();
    private List<String>             genres      = new ArrayList<String>();

    private MediaMetadata            metadata;

    public MyMoviesParser(String id) throws Exception {
        this.id = id;
        this.provider = (MyMoviesMetadataProvider) MediaMetadataFactory.getInstance().getProvider(MyMoviesMetadataProvider.PROVIDER_ID);
        this.movieFile = provider.getMyMoviesXmlFile();
        this.node = movieFile.findMovieById(id);
    }

    public MediaMetadata getMetaData() {
        metadata = new MediaMetadata();

        metadata.setCastMembers(getCastMembers().toArray(new CastMember[castMembers.size()]));

        metadata.setAspectRatio(getAspectRatio());
        metadata.setCompany(getCompany());
        metadata.setGenres(getGenres().toArray(new String[genres.size()]));
        updateMPAARating(metadata);
        metadata.setDescription(getPlot());
        metadata.setReleaseDate(getReleaseDate());
        metadata.setRuntime(getRuntime());

        IMediaArt ma = getMediaArtImage("f");
        if (ma != null) {
            metadata.addMediaArt(ma);
        }

        metadata.setMediaTitle(getTitle());
        metadata.setYear(getYear());

        metadata.setProviderId(MyMoviesMetadataProvider.PROVIDER_ID);
        metadata.setProviderDataId(new MetadataID(MyMoviesMetadataProvider.PROVIDER_ID, getProviderUrl()));
        metadata.setProviderDataUrl(id);
        return metadata;
    }

    private List<ICastMember> getCastMembers() {
        // add in others
        NodeList nl = node.getElementsByTagName("Person");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            String credType = DOMUtils.getElementValue(e, "Type");
            CastMember cm = new CastMember();
            cm.setName(DOMUtils.getElementValue(e, "Name"));
            cm.setPart(DOMUtils.getElementValue(e, "Role"));
            if ("Director".equals(credType)) {
                cm.setType(ICastMember.DIRECTOR);
            } else if ("Writer".equals(credType)) {
                cm.setType(ICastMember.WRITER);
            } else if ("Actor".equals(credType)) {
                cm.setType(ICastMember.ACTOR);
            } else {
                cm.setType(ICastMember.OTHER);
            }
            castMembers.add(cm);
        }
        return castMembers;
    }

    public String getAspectRatio() {
        return MyMoviesXmlFile.getElementValue(node, "AspectRatio");
    }

    public String getCompany() {
        return MyMoviesXmlFile.getElementValue(node, "Studio");
    }

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

    public void updateMPAARating(MediaMetadata md) {
        NodeList nl = node.getElementsByTagName("ParentalRating");
        Element el = null;
        if (nl.getLength() > 0) {
            el = (Element) nl.item(0);
        }

        md.set(MetadataKey.MPAA_RATING, DOMUtils.getElementValue(el, "Value"));
        md.set(MetadataKey.MPAA_RATING_DESCRIPTION, DOMUtils.getElementValue(el, "Description"));
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
        return MetadataUtil.convertTimeToMillissecondsForSage(MyMoviesXmlFile.getElementValue(node, "RunningTime"));
    }

    public IMediaArt getMediaArtImage(String type) {
        Element el = DOMUtils.getElementByTagName(node, "Covers");
        if (el != null) {
            try {
                File f = new File(DOMUtils.getElementValue(el, "Front"));
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

    public String getYear() {
        return MyMoviesXmlFile.getElementValue(node, "ProductionYear");
    }
}
