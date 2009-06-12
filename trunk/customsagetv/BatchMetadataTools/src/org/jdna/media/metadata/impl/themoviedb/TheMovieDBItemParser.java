package org.jdna.media.metadata.impl.themoviedb;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.MediaArt;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.url.IUrl;
import org.jdna.url.UrlFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sagex.phoenix.fanart.MediaArtifactType;

public class TheMovieDBItemParser {
    private static final Logger    log           = Logger.getLogger(TheMovieDBItemParser.class);
    public static final String     ITEM_URL      = "http://api.themoviedb.org/2.0/Movie.getInfo?id=%s&api_key=";
    public static final String     IMDB_ITEM_URL = "http://api.themoviedb.org/2.0/Movie.imdbLookup?imdb_id=%s&api_key=";

    private String origUrl = null;
    private IUrl                 url;
    private MediaMetadata          md            = null;
    private DocumentBuilderFactory factory       = DocumentBuilderFactory.newInstance();
    private List<ICastMember>      cast          = new ArrayList<ICastMember>();

    private String                 theMovieDBID;

    public TheMovieDBItemParser(String providerDataUrl) {
        this.origUrl = providerDataUrl;
        this.url = UrlFactory.newUrl(providerDataUrl + TheMovieDBMetadataProvider.getApiKey());
    }

    public IMediaMetadata getMetadata() {
        if (md == null) {
            try {
                // parse and fill
                DocumentBuilder parser = factory.newDocumentBuilder();
                log.debug("Parsing TheMovieDB url: " + url + TheMovieDBMetadataProvider.getApiKey());
                Document doc = parser.parse(url.getInputStream(null, true));
                
                md = new MediaMetadata();

                NodeList nl = doc.getElementsByTagName("movie");
                if (nl == null || nl.getLength() == 0) {
                    throw new Exception("Movie Node not found!");
                }

                if (nl.getLength() > 1) {
                    log.warn("Found more than 1 movie node.  Using the first.");
                }

                Element movie = (Element) nl.item(0);
                theMovieDBID = getElementValue(movie, "id");
                List<ICastMember> cast = getPeople(movie);
                if (cast != null) {
                    md.setCastMembers(cast.toArray(new ICastMember[cast.size()]));
                }
                md.setGenres(getGenres(movie));
                md.setDescription(getElementValue(movie, "short_overview"));
                md.setReleaseDate(getElementValue(movie, "release"));
                md.setRuntime(MetadataUtil.convertTimeToMillissecondsForSage(getElementValue(movie, "runtime")));
                addPosters(md, movie);
                addBackgrounds(md, movie);
                addBanners(md, movie);
                md.setMediaTitle(getElementValue(movie, "title"));
                if (StringUtils.isEmpty(md.getMediaTitle())) {
                    throw new RuntimeException("The MovieDB Result didn't contain a title. Url: " + url + TheMovieDBMetadataProvider.getApiKey());
                }
                md.setUserRating(getElementValue(movie, "rating"));
                md.setYear(parseYear(md.getReleaseDate()));

                md.setProviderDataUrl(origUrl);
                md.setProviderId(TheMovieDBMetadataProvider.PROVIDER_ID);
                md.setProviderDataId(new MetadataID("themoviedb", theMovieDBID));
            } catch (Exception e) {
                log.error("Failed while parsing: " + url, e);
                md = null;
            }
        }
        return md;
    }

    private List<ICastMember> getPeople(Element movie) {
        if (cast.size() > 0) return cast;

        NodeList nl = movie.getElementsByTagName("person");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            CastMember cm = new CastMember();
            String role = e.getAttribute("job");
            if ("director".equals(role)) {
                cm.setType(ICastMember.DIRECTOR);
            } else if ("writer".equals(role) || "screenplay".equals(role)) {
                cm.setType(ICastMember.WRITER);
            } else if ("actor".equals(role)) {
                cm.setType(ICastMember.ACTOR);
                cm.setPart(getElementValue(e, "role"));
            } else {
                cm.setType(ICastMember.OTHER);
                cm.setPart(role);
            }

            cm.setName(getElementValue(e, "name"));
            cm.setProviderDataUrl(getElementValue(e, "url"));
            cast.add(cm);
        }
        return cast;
    }

    private String[] getGenres(Element movie) {
        List<String> l = new ArrayList<String>();

        NodeList nl = movie.getElementsByTagName("category");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            l.add(getElementValue(e, "name"));
        }
        return l.toArray(new String[l.size()]);
    }

    private void addBanners(MediaMetadata md, Element movie) {
        NodeList nl = movie.getElementsByTagName("banner");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            String maUrl = e.getTextContent().trim();
            if (isValidMediaArt(maUrl)) {
                MediaArt ma = new MediaArt();
                ma.setType(MediaArtifactType.BANNER);
                ma.setProviderId(TheMovieDBMetadataProvider.PROVIDER_ID);
                ma.setDownloadUrl(maUrl);
                ma.setLabel(e.getAttribute("size"));
                md.addMediaArt(ma);
            }
        }
    }

    private void addPosters(MediaMetadata md, Element movie) {
        NodeList nl = movie.getElementsByTagName("poster");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            String maUrl = e.getTextContent().trim();
            if (isValidMediaArt(maUrl)) {
                MediaArt ma = new MediaArt();
                ma.setType(MediaArtifactType.POSTER);
                ma.setProviderId(TheMovieDBMetadataProvider.PROVIDER_ID);
                ma.setDownloadUrl(maUrl);
                ma.setLabel(e.getAttribute("size"));
                md.addMediaArt(ma);
            }
        }
    }

    private void addBackgrounds(MediaMetadata md, Element movie) {
        NodeList nl = movie.getElementsByTagName("backdrop");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            
            String maUrl = e.getTextContent().trim();
            if (isValidMediaArt(maUrl)) {
                MediaArt ma = new MediaArt();
                ma.setType(MediaArtifactType.BACKGROUND);
                ma.setProviderId(TheMovieDBMetadataProvider.PROVIDER_ID);
                ma.setDownloadUrl(maUrl);
                ma.setLabel(e.getAttribute("size"));
                md.addMediaArt(ma);
            }
        }
    }
    
    public boolean isValidMediaArt(String name) {
        if (name==null) return false;
        Pattern p = Pattern.compile("(_thumb|_mid|_poster|_cover).jpg$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(name);
        return !m.find();
    }
    
    private String parseYear(String releaseDate) {
        try {
            return releaseDate.substring(0, releaseDate.indexOf("-"));
        } catch (Exception e) {
            return null;
        }
    }

    public static String getElementValue(Element el, String tag) {
        NodeList nl = el.getElementsByTagName(tag);
        if (nl.getLength() > 0) {
            Node n = nl.item(0);
            return n.getTextContent().trim();
        }
        return null;
    }

    public String getTheMovieDBID() {
        return theMovieDBID;
    }
}
