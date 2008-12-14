package org.jdna.media.metadata.impl.themoviedb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.MediaMetadata;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TheMovieDBItemParser {
	private static final Logger log = Logger.getLogger(TheMovieDBItemParser.class);
	public static final String ITEM_URL = "http://api.themoviedb.org/2.0/Movie.getInfo?id=%s&api_key=";
	public static final String IMDB_ITEM_URL = "http://api.themoviedb.org/2.0/Movie.imdbLookup?imdb_id=%s&api_key=";
	
	private String url;
	private MediaMetadata md=null;
	private Map<String, String> covers = new HashMap<String, String>();
	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private List<ICastMember> cast = new ArrayList<ICastMember>();
	
	private String theMovieDBID;
	
	public TheMovieDBItemParser(String providerDataUrl) {
		this.url=providerDataUrl;
	}
	
	public IMediaMetadata getMetadata() {
		if (md==null) {
			try {
				// parse and fill
				DocumentBuilder parser = factory.newDocumentBuilder();
				Document doc = parser.parse(url+TheMovieDBMetadataProvider.getApiKey());
				
				
				md = new MediaMetadata();
				NodeList nl = doc.getElementsByTagName("movie");
				if (nl==null || nl.getLength()==0) {
					throw new Exception("Movie Node not found!");
				}
				
				if (nl.getLength()>1) {
					log.warn("Found more than 1 movie node.  Using the first.");
				}
				
				Element movie = (Element) nl.item(0);
				theMovieDBID = getElementValue(movie, "id");
				md.setActors(getActors(movie));
				md.setAspectRatio(null);
				md.setCompany(null);
				md.setDirectors(getDirectors(movie));
				md.setGenres(getGenres(movie));
				md.setMPAARating(null);
				md.setPlot(getElementValue(movie, "short_overview"));
				md.setProviderDataUrl(url);
				md.setProviderId(TheMovieDBMetadataProvider.PROVIDER_ID);
				md.setReleaseDate(getElementValue(movie, "release"));
				md.setRuntime(convertTimeToMillissecondsForSage(getElementValue(movie, "runtime")));
				md.setThumbnailUrl(getThumbnailUrl(movie));
				md.setTitle(getElementValue(movie, "title"));
				md.setUserRating(getElementValue(movie, "rating"));
				md.setWriters(getWriters(movie));
				md.setYear(parseYear(md.getReleaseDate()));
			} catch (Exception e) {
				log.error("Failed while parsing: " + url, e);
				md = null;
			}
		}
		return md;
	}

	private ICastMember[] getWriters(Element movie) {
		return getPeople(movie, ICastMember.WRITER);
	}

	private ICastMember[] getDirectors(Element movie) {
		return getPeople(movie, ICastMember.DIRECTOR);
	}

	private ICastMember[] getActors(Element movie) {
		return getPeople(movie, ICastMember.ACTOR);
	}
	
	private ICastMember[] getPeople(Element movie, int type) {
		List<ICastMember> p = getPeople(movie);
		List<ICastMember> l = new ArrayList<ICastMember>();
		for (ICastMember cm : p) {
			if (cm.getType()==type) {
				l.add(cm);
			}
		}
		return l.toArray(new ICastMember[l.size()]);
	}

	private List<ICastMember> getPeople(Element movie) {
		if (cast.size()>0) return cast;
		
		NodeList nl = movie.getElementsByTagName("person");
		for (int i=0;i<nl.getLength();i++) {
			Element e = (Element) nl.item(i);
			CastMember cm = new CastMember();
			String role = e.getAttribute("job");
			if ("director".equals(role)) {
				cm.setType(ICastMember.DIRECTOR);
			} else if ("writer".equals(role) || "screenplay".equals(role)) {
				cm.setType(ICastMember.WRITER);
			} else if ("actor".equals(role)) {
				cm.setType(ICastMember.ACTOR);
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
		for (int i=0;i<nl.getLength();i++) {
			Element e = (Element) nl.item(i);
			l.add(getElementValue(e, "name"));
		}
		return l.toArray(new String[l.size()]);
	}

	private String getThumbnailUrl(Element movie) {
		try {
			Map<String, String> map = getCovers(movie);
			String thumb = map.get("original");
			if (thumb==null && map.size()>0) {
				thumb = map.get(map.keySet().iterator().next());
			}
			return thumb;
		} catch (Exception e) {
			return null;
		}
	}

	private Map<String, String> getCovers(Element movie) {
		if (covers.size()>0) return covers;
		
		NodeList nl = movie.getElementsByTagName("poster");
		for (int i=0;i<nl.getLength();i++) {
			Element e = (Element) nl.item(i);
			covers.put(e.getAttribute("size"), e.getTextContent().trim());
		}
		return covers;
	}

	private String parseYear(String releaseDate) {
		try {
			return releaseDate.substring(0, releaseDate.indexOf("-"));
		} catch (Exception e) {
			return null;
		}
	}

	private String convertTimeToMillissecondsForSage(String time) {
		long t = 0;
		try {
			t = Long.parseLong(time);
			t = t * 60 * 1000;
		} catch (Exception e) {
		}
		return String.valueOf(t);
	}

	public static String getElementValue(Element el, String tag) {
		NodeList nl = el.getElementsByTagName(tag);
		if (nl.getLength()>0) {
			Node n = nl.item(0);
			return n.getTextContent().trim();
		}
		return null;
	}

	public String getTheMovieDBID() {
		return theMovieDBID;
	}
}
