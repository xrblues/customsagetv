package org.jdna.media.metadata.impl.dvdprof;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.VideoMetaData;
import org.jdna.url.CookieHandler;
import org.jdna.url.URLSaxParser;
import org.jdna.url.UrlUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DVDProfMetaDataParser extends URLSaxParser {
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
	private static final int TITLE=14;
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
	
	private List<CastMember> actors = new ArrayList<CastMember>();;
	private List<CastMember> directors =new ArrayList<CastMember>();
	private List<String> genres = new ArrayList<String>();
	private List<CastMember> writers = new ArrayList<CastMember>();
	private List<CastMember> otherCast = new ArrayList<CastMember>();

	private VideoMetaData metadata = new VideoMetaData();
	private String baseUrl;
	private String charbuf;
	
	public DVDProfMetaDataParser(String dataUrl, CookieHandler handler) throws Exception {
		super(dataUrl);
		metadata.setProviderId(DVDProfMetaDataProvider.PROVIDER_ID);
		metadata.setProviderDataUrl(dataUrl);
		this.baseUrl = UrlUtil.getBaseUrl(dataUrl);
		
		// force a parse of the metadata.
		parse(handler);
		
		if (metadata.getYear()==null) {
			throw new Exception("Could not parse: " + metadata.getProviderDataUrl() + " as a valid DVD Profiler URL!");
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
			metadata.setYear(charbuf);
			state=READING;
			return;
		} else if (state == DIRECTORS) {
			Pattern p = Pattern.compile("([^:]+):\\s+(.+)");
			Matcher m = p.matcher(charbuf);
			if (m.find()) {
				String role = m.group(2);
				if (role!=null) {
					if (role.equalsIgnoreCase("director")) {
						CastMember cm = new CastMember(ICastMember.DIRECTOR);
						cm.setName(m.group(1));
						directors.add(cm);
					} else if (role.equalsIgnoreCase("writer")) { 
						CastMember cm = new CastMember(ICastMember.WRITER);
						cm.setName(m.group(1));
						writers.add(cm);
					} else {
						CastMember cm = new CastMember(ICastMember.OTHER);
						cm.setName(m.group(1));
						cm.setPart(m.group(2));
						otherCast.add(cm);
					}
				}
			}
			return;
		} else if (state==TITLE) {
			metadata.setTitle(charbuf);
			state=READING;
			return;
		} else if (state==RATING) {
			metadata.setMPAARating(charbuf);
			state=READING;
			return;
		} else if (state==RELEASE) {
			metadata.setReleaseDate(charbuf);
			state=READING;
			return;
		} else if (state==RUNNINGTIME) {
			metadata.setRuntime(charbuf);
			state=READING;
			return;
		} else if (state==ASPECTRATIO) {
			metadata.setAspectRatio(charbuf);
			state=READING;
			return;
		} else if (state==SRP) {
			// plot follows the SRP and it's the only way for us to identify it
			state=PLOT;
			return;
		} else if (state==PLOT) {
			if (metadata.getPlot()==null) {
				metadata.setPlot(charbuf);
			} else {
				metadata.setPlot(metadata.getPlot() + ("\n" + charbuf)); 
			}
			// keep the state == plot until we read td
			state=PLOT;
			return;
		} else if (state==GENRES) {
			genres.add(charbuf);
			return;
		} else if (state==STUDIO) {
			metadata.setCompany(charbuf);
			state=READING;
			return;
		} else if (state==ACTORS) {
			Pattern p = Pattern.compile("(.*)\\s+as\\s+(.*)");
			Matcher m =p.matcher(charbuf);
			if (m.find()) {
				CastMember cm = new CastMember(ICastMember.ACTOR);
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


	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		
		if (state==DIRECTORS && isTag("td", localName)) state=READING;
		
		// end the plot once we reach the span after the text
		if (state==PLOT && metadata.getPlot()!=null && isTag("span", localName)) state=READING;
	
		// end genres after the table
		if (state==GENRES && isTag("table", localName)) state=READING;

		// end actors after the table
		if (state==ACTORS && isTag("table", localName)) state=DONE;
		
	}

	@Override
	public void startElement(String uri, String localName, String name,	Attributes atts) throws SAXException {
		if (metadata.getThumbnailUrl()==null && metadata.getPlot()!=null && isTag("img", localName)) {
			String src = atts.getValue("src");
			metadata.setThumbnailUrl(UrlUtil.joinUrlPath(UrlUtil.getDomainUrl(baseUrl), src)); 
		}
		
		if (state==READING && isTag("td", localName) && "styletitle".equals(attr(atts, "class"))) {
			// found title
			state=TITLE;
		}
	}
	
	public VideoMetaData getMetaData() {
		metadata.setGenres(genres.toArray(new String[genres.size()]));
		metadata.setActors(actors.toArray(new CastMember[actors.size()]));
		metadata.setDirectors(directors.toArray(new CastMember[directors.size()]));
		metadata.setWriters(writers.toArray(new CastMember[writers.size()]));
		return metadata;
	}
}
