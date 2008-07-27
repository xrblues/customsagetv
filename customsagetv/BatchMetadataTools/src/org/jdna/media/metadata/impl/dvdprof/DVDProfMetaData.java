package org.jdna.media.metadata.impl.dvdprof;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.url.CookieHandler;
import org.jdna.url.URLSaxParser;
import org.jdna.url.UrlUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DVDProfMetaData extends URLSaxParser implements IVideoMetaData {
	private static final int READING=0;
	private static final int YEAR=1;
	private static final int DIRECTORS=2;
	private static final int RATING=3;
	private static final int RELEASE=4;
	private static final int RUNNINGTIME=6;
	private static final int ASPECTRATIO=7;
	private static final int SRP=8;
	private static final int PLOT=9;
	private static final int GENRES=10;
	private static final int STUDIO=11;
	private static final int ACTORS=12;
	private static final int DONE=99;
	
	private static final String YEAR_TOKEN = "Production\\s+Year:";
	private static final String DIRECTOR_TOKEN = "Direction:";
	private static final String RATING_TOKEN = "Rating:";
	private static final String RELEASE_TOKEN = "DVD\\s+Release:";
	private static final String RUNNINGTIME_TOKEN = "Running\\s+Time:";
	private static final String ASPECTRATIO_TOKEN = "Video\\s+Formats:";
	private static final String SRP_TOKEN = "SRP:";
	private static final String GENRES_TOKEN = "Genres";
	private static final String STUDIO_TOKEN = "Studios";
	private static final String ACTORS_TOKEN = "Actors";
	
	private int state = READING;
	
	private String providerUrl = null;
	private List<ICastMember> actors = new ArrayList<ICastMember>();;
	private String aspectRatio;
	private String studio;
	private List<ICastMember> directors =new ArrayList<ICastMember>();
	private List<String> genres = new ArrayList<String>();
	private String rating;
	private String plot;
	private String releaseDate;
	private String runTime;
	private String thumbnailUr;
	private String title;
	private String userRating;
	private List<ICastMember> writers = new ArrayList<ICastMember>();
	private String year;
	private String charbuf;
	private List<ICastMember> otherCast = new ArrayList<ICastMember>();
	private String baseUrl = null;

	public DVDProfMetaData(DVDProfSearchResult result, CookieHandler handler) throws Exception {
		super(result.getDataUrl());
		this.providerUrl = result.getDataUrl();
		this.title = result.getTitle();
		this.baseUrl = UrlUtil.getBaseUrl(result.getDataUrl());
		
		
		// force a parse of the metadata.
		parse(handler);
		if (this.year==null) {
			throw new Exception("Could not parse: " + this.providerUrl + " as a valid DVD Profiler URL!");
		}
	}

	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (state==DONE) return;
		
		super.characters(ch, start, length);
		
		charbuf = getCharacters(ch, start, length);
		if (charbuf==null || charbuf.trim().length()==0) return;
		
		if (state == YEAR) {
			year=charbuf;
			state=READING;
			return;
		} else if (state == DIRECTORS) {
			Pattern p = Pattern.compile("([^:]+):\\s+(.+)");
			Matcher m = p.matcher(charbuf);
			if (m.find()) {
				String role = m.group(2);
				if (role!=null) {
					if (role.equalsIgnoreCase("director")) {
						DVDProfCastMember cm = new DVDProfCastMember();
						cm.setName(m.group(1));
						cm.setType(ICastMember.DIRECTOR);
						getDirectors().add(cm);
					} else if (role.equalsIgnoreCase("writer")) { 
						DVDProfCastMember cm = new DVDProfCastMember();
						cm.setName(m.group(1));
						cm.setType(ICastMember.WRITER);
						getWriters().add(cm);
					} else {
						DVDProfCastMember cm = new DVDProfCastMember();
						cm.setName(m.group(1));
						cm.setType(ICastMember.OTHER);
						cm.setPart(m.group(2));
						getOtherCast().add(cm);
					}
				}
			}
			return;
		} else if (state==RATING) {
			rating = charbuf;
			state=READING;
			return;
		} else if (state==RELEASE) {
			releaseDate = charbuf;
			state=READING;
			return;
		} else if (state==RUNNINGTIME) {
			runTime = charbuf;
			state=READING;
			return;
		} else if (state==ASPECTRATIO) {
			aspectRatio = charbuf;
			state=READING;
			return;
		} else if (state==SRP) {
			// plot follows the SRP and it's the only way for us to identify it
			state=PLOT;
			return;
		} else if (state==PLOT) {
			if (plot==null) {
				plot = charbuf;
			} else {
				plot += ("\n" + charbuf); 
			}
			// keep the state == plot until we read td
			state=PLOT;
			return;
		} else if (state==GENRES) {
			genres.add(charbuf);
			return;
		} else if (state==STUDIO) {
			studio=charbuf;
			state=READING;
			return;
		} else if (state==ACTORS) {
			Pattern p = Pattern.compile("(.*)\\s+as\\s+(.*)");
			Matcher m =p.matcher(charbuf);
			if (m.find()) {
				DVDProfCastMember cm = new DVDProfCastMember();
				cm.setType(ICastMember.ACTOR);
				cm.setName(m.group(1));
				cm.setPart(m.group(2));
				actors.add(cm);
			}
			return;
		}
		
		if (state==READING) {
			if (charbuf.matches(YEAR_TOKEN)) {
				state=YEAR;
			} else if (charbuf.matches(DIRECTOR_TOKEN)) {
				state=DIRECTORS;
			} else if (charbuf.matches(RATING_TOKEN)) {
				state=RATING;
			} else if (charbuf.matches(RELEASE_TOKEN)) {
				state=RELEASE;
			} else if (charbuf.matches(RUNNINGTIME_TOKEN)) {
				state=RUNNINGTIME;
			} else if (charbuf.matches(ASPECTRATIO_TOKEN)) {
				state=ASPECTRATIO;
			} else if (charbuf.matches(SRP_TOKEN)) {
				state=SRP;
			} else if (charbuf.matches(GENRES_TOKEN)) {
				state=GENRES;
			} else if (charbuf.matches(STUDIO_TOKEN)) {
				state=STUDIO;
			} else if (charbuf.matches(ACTORS_TOKEN)) {
				state=ACTORS;
			}
		}
	}

	private List<ICastMember> getOtherCast() {
		return otherCast ;
	}


	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		
		if (state==DIRECTORS && isTag("td", localName)) state=READING;
		
		// end the plot once we reach the span after the text
		if (state==PLOT && plot!=null && isTag("span", localName)) state=READING;
	
		// end genres after the table
		if (state==GENRES && isTag("table", localName)) state=READING;

		// end actors after the table
		if (state==ACTORS && isTag("table", localName)) state=DONE;
		
	}

	@Override
	public void startElement(String uri, String localName, String name,	Attributes atts) throws SAXException {
		if (thumbnailUr==null && plot!=null && isTag("img", localName)) {
			String src = atts.getValue("src");
			thumbnailUr = UrlUtil.joinUrlPath(UrlUtil.getDomainUrl(baseUrl), src); 
		}
	}

	public List<ICastMember> getActors() {
		return actors;
	}

	public String getAspectRatio() {
		return aspectRatio;
	}
		
	public String getCompany() {
		return studio;
	}

	public List<ICastMember> getDirectors() {
		return directors;
	}

	public List<String> getGenres() {
		return genres;
	}

	public String getMPAARating() {
		return rating;
	}

	public String getPlot() {
		return plot;
	}

	public String getProviderDataUrl() {
		return providerUrl;
	}

	public String getProviderId() {
		return DVDProfMetaDataProvider.PROVIDER_ID;
	}

	public String getReleaseDate() {
		return releaseDate;
	}

	public String getRuntime() {
		return runTime;
	}

	public String getThumbnailUrl() {
		return thumbnailUr;
	}

	public String getTitle() {
		return title;
	}

	public String getUserRating() {
		return userRating;
	}

	public List<ICastMember> getWriters() {
		return writers;
	}

	public String getYear() {
		return year;
	}

	public boolean isUpdated() {
		return false;
	}
}
