package org.jdna.media.metadata.impl.dvdproflocal;

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
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sagex.phoenix.fanart.FanartUtil.MediaArtifactType;

public class LocalDVDProfParser {
    private static final Logger          log         = Logger.getLogger(LocalDVDProfParser.class);

    private String                       id          = null;

    private LocalDVDProfMetaDataProvider provider    = null;
    private DVDProfXmlFile               dvdFile     = null;
    private Element                      node        = null;

    private List<ICastMember>            castMembers = null;
    private List<String>                 genres      = new ArrayList<String>();

    private MediaMetadata                metadata;

    public LocalDVDProfParser(String id) throws Exception {
        this.id = id;
        this.provider = (LocalDVDProfMetaDataProvider) MediaMetadataFactory.getInstance().getProvider(LocalDVDProfMetaDataProvider.PROVIDER_ID);
        this.dvdFile = provider.getDvdProfilerXmlFile();
        this.node = dvdFile.findMovieById(id);
    }

    public MediaMetadata getMetaData() {
        metadata = new MediaMetadata();
        metadata.setCastMembers(getCastMembers().toArray(new CastMember[castMembers.size()]));
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
        
        // if use back cover is enabled 
        //ma = getMediaArtImage("b");
        // if (ma != null) {
        //     metadata.addMediaArt(ma);
        // }

        metadata.setMediaTitle(getTitle());
        metadata.setUserRating(getUserRating());
        metadata.setYear(getYear());

        metadata.setProviderId(LocalDVDProfMetaDataProvider.PROVIDER_ID);
        metadata.setProviderDataId(new MetadataID(LocalDVDProfMetaDataProvider.PROVIDER_ID, getProviderUrl()));
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
                log.debug("Adding Cast: " + cm);
                castMembers.add(cm);
            }
        }
        return castMembers;
    }

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
                log.debug("Adding Actor: " + cm);
            }
        } else {
            log.debug("No Actors!");
        }
        return actors;
    }

    public String getAspectRatio() {
        return DVDProfXmlFile.getElementValue(node, "FormatAspectRatio");
    }

    public String getCompany() {
        return DVDProfXmlFile.getElementValue(node, "Studio");
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

    public String getMPAARating() {
        return DVDProfXmlFile.getElementValue(node, "Rating");
    }

    public String getPlot() {
        return StringUtils.removeHtml(DVDProfXmlFile.getElementValue(node, "Overview"));
    }

    public String getProviderUrl() {
        return id;
    }

    public String getProviderId() {
        return LocalDVDProfMetaDataProvider.PROVIDER_ID;
    }

    public String getReleaseDate() {
        return DVDProfXmlFile.getElementValue(node, "Released");
    }

    public String getRuntime() {
        return MetadataUtil.convertTimeToMillissecondsForSage(DVDProfXmlFile.getElementValue(node, "RunningTime"));
    }

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
        return StringUtils.removeHtml(DVDProfXmlFile.getElementValue(node, "Title"));
    }

    public String getUserRating() {
        // Didn't find user rating in profile information
        return null;
    }

    public String getYear() {
        return DVDProfXmlFile.getElementValue(node, "ProductionYear");
    }
}
