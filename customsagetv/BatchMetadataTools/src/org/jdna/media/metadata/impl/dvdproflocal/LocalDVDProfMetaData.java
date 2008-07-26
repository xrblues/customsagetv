package org.jdna.media.metadata.impl.dvdproflocal;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.impl.dvdprof.DVDProfCastMember;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LocalDVDProfMetaData implements IVideoMetaData {
	private static final Logger log = Logger.getLogger(LocalDVDProfMetaData.class);
	
	private String id = null;
	
	private LocalDVDProfMetaDataProvider provider = null;
	private DVDProfXmlFile dvdFile = null;
	private Element node = null;
	
	private List<ICastMember> actors = new ArrayList<ICastMember>();;
	private List<ICastMember> directors =new ArrayList<ICastMember>();
	private List<String> genres = new ArrayList<String>();
	private List<ICastMember> writers = new ArrayList<ICastMember>();
	
	public LocalDVDProfMetaData(String id) throws Exception {
		this.id=id;
		this.provider=LocalDVDProfMetaDataProvider.getInstance();
		this.dvdFile = provider.getDvdProfilerXmlFile();
		this.node = dvdFile.findMovieById(id);
	}

	public List<ICastMember> getActors() {
		if (actors.size()==0) {
			NodeList nl = node.getElementsByTagName("Actor");
			for (int i=0;i<nl.getLength();i++) {
				Element e = (Element) nl.item(i);
				DVDProfCastMember cm = new DVDProfCastMember();
				cm.setType(ICastMember.ACTOR);
				cm.setName(String.format("%s %s", e.getAttribute("FirstName"), e.getAttribute("LastName")));
				cm.setPart(e.getAttribute("Role"));
				actors.add(cm);
			}
		}
		return actors;
	}

	public String getAspectRatio() {
		return DVDProfXmlFile.getElementValue(node, "FormatAspectRatio");
	}

	public String getCompany() {
		return DVDProfXmlFile.getElementValue(node, "Studio");
	}

	public List<ICastMember> getDirectors() {
		if (directors.size()==0) {
			NodeList nl = node.getElementsByTagName("Credit");
			for (int i=0;i<nl.getLength();i++) {
				Element e = (Element) nl.item(i);
				String credType = e.getAttribute("CreditType");
				if ("Direction".equals(credType)) {
					DVDProfCastMember cm = new DVDProfCastMember();
					cm.setType(ICastMember.DIRECTOR);
					cm.setName(String.format("%s %s", e.getAttribute("FirstName"), e.getAttribute("LastName")));
					cm.setPart(credType);
					directors.add(cm);
				}
			}
		}
		return directors;
	}

	public List<String> getGenres() {
		if (genres.size()==0) {
			NodeList nl = node.getElementsByTagName("Genre");
			for (int i=0;i<nl.getLength();i++) {
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
		return DVDProfXmlFile.getElementValue(node, "Overview");
	}

	public String getProviderDataUrl() {
		return id;
	}

	public String getProviderId() {
		return LocalDVDProfMetaDataProvider.PROVIDER_ID;
	}

	public String getReleaseDate() {
		return DVDProfXmlFile.getElementValue(node, "Released");
	}

	public String getRuntime() {
		return DVDProfXmlFile.getElementValue(node, "RunningTime");
	}

	public String getThumbnailUrl() {
		File f = new File(provider.getImagesDir(), id + "f.jpg");
		if (!f.exists()) {
			log.warn("Missing Front Cover for Movie: " + id + "; " + getTitle());
		} else {
			try {
				return f.toURI().toURL().toExternalForm();
			} catch (MalformedURLException e) {
				log.error("Failed to create url for thumbnail on movie: " + id + "; " + getTitle());
			}
		}
		return null;
	}

	public String getTitle() {
		return DVDProfXmlFile.getElementValue(node, "Title");
	}

	public String getUserRating() {
		// Didn't find user rating in profile information
		return null;
	}

	public List<ICastMember> getWriters() {
		if (writers.size()==0) {
			NodeList nl = node.getElementsByTagName("Credit");
			for (int i=0;i<nl.getLength();i++) {
				Element e = (Element) nl.item(i);
				String credType = e.getAttribute("CreditType");
				if ("Writing".equals(credType)) {
					DVDProfCastMember cm = new DVDProfCastMember();
					cm.setType(ICastMember.WRITER);
					cm.setName(String.format("%s %s", e.getAttribute("FirstName"), e.getAttribute("LastName")));
					cm.setPart(credType);
					writers.add(cm);
				}
			}
		}
		return writers;
	}

	public String getYear() {
		return DVDProfXmlFile.getElementValue(node, "ProductionYear");
	}

	public boolean isUpdated() {
		// TODO Auto-generated method stub
		return false;
	}

}
