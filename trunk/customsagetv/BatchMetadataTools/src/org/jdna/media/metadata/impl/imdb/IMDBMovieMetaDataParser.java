package org.jdna.media.metadata.impl.imdb;

import java.util.ArrayList;
import java.util.List;

import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.VideoMetaData;
import org.jdna.url.URLSaxParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * State Machine parser that will crawl the Title Information page and extract out the MetaData for a given Movie.
 * 
 * @author seans
 *
 */
public class IMDBMovieMetaDataParser extends URLSaxParser {
	public static final String USER_RATING_MATCH = "User Rating:";
	private static final String DIRECTOR_MATCH = "Director:";
	private static final String WRITER_MATCH = "Writers";
	private static final String RELEASE_DATE_MATCH = "Release Date:";
	private static final String GENRE_MATCH = "Genre:";
	private static final String PLOT_MATCH = "Plot:";
	private static final String MPAA_MATCH = "MPAA";
	private static final String RUNTIME_MATCH = "Runtime:";
	private static final String ASPECT_RATION_MATCH = "Aspect Ratio:";
	private static final String COMPANY_MATCH = "Company:";
	private static final Object CAST_MATCH = "Cast";
	
	
	private VideoMetaData metadata = null;
	private List<String> genres;
	private List<CastMember> cast = new ArrayList<CastMember>();
	
	private static final int LOOKING = 0;
	private static final int TITLE = 1;
	private static final int POSTER = 2;
	private static final int USER_RATING = 3;
	private static final int DIRECTORS = 4;
	private static final int WRITERS = 5;
	private static final int RELEASE_DATE = 6;
	private static final int GENRE = 7;
	private static final int PLOT = 8;
	private static final int MPAA = 9;
	private static final int RUNTIME = 10;
	private static final int ASPECT_RATION = 11;
	private static final int COMPANY = 12;
	private static final int CAST = 14;
	private static final int ENDED = 99;

	private int state = LOOKING;
	
	private static final int SS_NONE=1;
	private static final int SS_GENRE_TEXT=2;
	private static final int SS_CAST_NAME = 3;
	private static final int SS_CAST_CHAR = 4;
	private int subState = SS_NONE;

	private static final int HEADER_ON = 1;
	private static final int HEADER_OFF = 0;
	private static int headerState = HEADER_OFF;

	
	private CastMember curCastMember = null;
	
	private String charbuf = null; 
	private String curTag = null;
	
	public IMDBMovieMetaDataParser(String url) {
		super(url);
		metadata = new VideoMetaData();
		metadata.setProviderDataUrl(url);
		metadata.setProviderId(IMDBMetaDataProvider.PROVIDER_ID);
	}
	
	public VideoMetaData getMetatData() {
		return metadata;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (state==ENDED) return;
		
		charbuf = getCharacters(ch, start, length);
		if (charbuf==null) return;
		
		// sometimes the parser does not ignore whitespace, so account for it here.
		charbuf = charbuf.trim();
		if (charbuf.length()==0) return;
		
		if (state == TITLE) {
			// parse the title and year in the form "title (year)"
			int brac = charbuf.indexOf("(");
			if (brac!=-1) {
				metadata.setTitle(charbuf.substring(0,brac));
				metadata.setYear(charbuf.substring(brac+1, brac+5));
			} else {
				metadata.setTitle(charbuf);
				metadata.setYear("Unknown");
			}
			// reset the state
			state = LOOKING;
			return;
		}
		
		if (state==LOOKING && USER_RATING_MATCH.equals(charbuf)) {
			state = USER_RATING;
			return;
		}
		
		if (state == USER_RATING) {
			metadata.setUserRating(charbuf);
			state = LOOKING;
			return;
		}
		
		if (state==LOOKING && DIRECTOR_MATCH.equals(charbuf)) {
			state = DIRECTORS;
			cast = new ArrayList<CastMember>();
			return;
		}
		
		if (state==DIRECTORS) {
			getCurCastMember().setName(charbuf);
			cast.add(getCurCastMember());
			curCastMember = null;
			return;
		}
		
		if (state==LOOKING && isTag("H5", curTag) && WRITER_MATCH.equals(charbuf)) {
			state = WRITERS;
			cast = new ArrayList<CastMember>();
			return;
		}
		
		if (state==WRITERS && curCastMember!=null) {
			curCastMember.setName(charbuf);
			cast.add(curCastMember);
			curCastMember=null;
			return;
		}
		
		if (state==LOOKING && RELEASE_DATE_MATCH.equals(charbuf)) {
			state = RELEASE_DATE;
			return;
		}
		
		if (state==RELEASE_DATE) {
			metadata.setReleaseDate(charbuf);
			state=LOOKING;
			return;
		}
		
		if (state==LOOKING && GENRE_MATCH.equals(charbuf)) {
			state=GENRE;
			genres = new ArrayList<String>();
			return;
		}
		
		if (state==GENRE && subState == SS_GENRE_TEXT) {
			genres.add(charbuf);
			subState=SS_NONE;
			return;
		}
		
		if (state==LOOKING && PLOT_MATCH.equals(charbuf)) {
			state = PLOT;
			return;
		}
		
		if (state==PLOT) {
			metadata.setPlot(charbuf);
			state=LOOKING;
			return;
		}
		
		if (state == LOOKING && headerState == HEADER_ON && MPAA_MATCH.equals(charbuf)) {
			state = MPAA;
			return;
		}
		
		if (state==MPAA && headerState == HEADER_OFF) {
			metadata.setMPAARating(charbuf);
			state = LOOKING;
			return;
		}
		
		if (state==LOOKING && headerState == HEADER_ON && RUNTIME_MATCH.equals(charbuf)) {
			state = RUNTIME;
			return;
		}
		
		if (state == RUNTIME) {
			metadata.setRuntime(charbuf);
			state = LOOKING;
			return;
		}
		
		if (state==LOOKING && headerState == HEADER_ON && ASPECT_RATION_MATCH.equals(charbuf)) {
			state=ASPECT_RATION;
			return;
		}
		
		if (state==ASPECT_RATION) {
			metadata.setAspectRatio(charbuf);
			state=LOOKING;
			return;
		}
		
		if (state==LOOKING && headerState == HEADER_ON && COMPANY_MATCH.equals(charbuf)) {
			state=COMPANY;
			return;
		}
		
		if (state==COMPANY) {
			metadata.setCompany(charbuf);
			state=LOOKING;
			return;
		}
		
		if (state==LOOKING && isTag("h3", curTag) && CAST_MATCH.equals(charbuf)) {
			curCastMember = null;
			state = CAST;
			subState = SS_NONE;
			return;
		}
		

		if (state==CAST && subState==SS_CAST_NAME) {
			getCurCastMember().setName(charbuf);
			subState=SS_NONE;
			return;
		}


		if (state==CAST && subState==SS_CAST_CHAR) {
			getCurCastMember().setPart(charbuf);
			cast.add(getCurCastMember());
			curCastMember = null;
			subState=SS_NONE;
			return;
		}
		
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		if (state==ENDED) return;
	
		if (( state==DIRECTORS || state==WRITERS || state==GENRE) && isTag("DIV", localName)) {
			if (state==DIRECTORS) {
				if (cast!=null && cast.size()>0) {
					metadata.setDirectors(cast.toArray(new CastMember[cast.size()]));
					cast.clear();
				}
			} else if (state==WRITERS) {
				if (cast!=null && cast.size()>0) {
					metadata.setWriters(cast.toArray(new CastMember[cast.size()]));
					cast.clear();
				}
			} else if (state==GENRE) {
				if (genres!=null && genres.size()>0) {
					metadata.setGenres(genres.toArray(new String[genres.size()]));
					genres.clear();
				}
			}
			state=LOOKING;
			subState=SS_NONE;
		}
		
		if (state==CAST && isTag("table", localName)) {
			state=LOOKING;
			subState=SS_NONE;
			if (cast!=null && cast.size()>0) {
				metadata.setActors(cast.toArray(new CastMember[cast.size()]));
				cast.clear();
			}
		}

		if (isTag("h5", localName)) headerState = HEADER_OFF;
	}

	@Override
	public void startElement(String uri, String localName, String name,	Attributes atts) throws SAXException {
		if (state==ENDED) return;
		
		// store the current tagname for later use
		curTag = localName;
	
		if (isTag("h5", localName)) headerState = HEADER_ON;
		
		if (state==LOOKING && isTag("A", localName) && "poster".equals(atts.getValue("name"))) {
			// next parse state is the poster (thumbnail)
			state = POSTER;
			return;
		}
		
		if (state==LOOKING && isTag("TITLE", localName)) {
			state = TITLE;
			return;
		}
		
		if (state==POSTER && isTag("IMG", localName)) {
			// we now have the image for the poster
			metadata.setThumbnailUrl(atts.getValue("src"));
			
			// reset state
			state = LOOKING;
			return;
		}
		
		if (state==DIRECTORS && isTag("A", localName)) {
			getCurCastMember().setProviderDataUrl(atts.getValue("href"));
			return;
		}
		
		if (state==WRITERS && isTag("A", localName) && atts.getValue("href").contains("/name/")) {
			getCurCastMember().setProviderDataUrl(atts.getValue("href"));
			return;
		}
		
		if (state==GENRE && isTag("A", localName) && attrContains(atts, "href", "/Genres/")) {
			subState = SS_GENRE_TEXT;
			return;
		}
		
		if (state==CAST && isTag("td", localName) && hasAttr(atts, "class", "nm")) {
			subState = SS_CAST_NAME;
			return;
		}

		if (state==CAST && isTag("td", localName) && hasAttr(atts, "class", "char")) {
			subState = SS_CAST_CHAR;
			return;
		}
		
		if (state==CAST && subState==SS_CAST_NAME && isTag("A", localName)) {
			getCurCastMember().setProviderDataUrl(attr(atts, "href"));
			return;
		}
	}
	
	private boolean hasAttr(Attributes atts, String aName, String aValue) {
		return aValue.equals(attr(atts, aName));
	}

	public CastMember getCurCastMember() {
		if (curCastMember==null) {
			curCastMember = new CastMember();
			if (state==DIRECTORS) {
				curCastMember.setType(ICastMember.DIRECTOR);
			} else if (state==WRITERS) {
				curCastMember.setType(ICastMember.WRITER);
			} else if (state==CAST) {
				curCastMember.setType(ICastMember.ACTOR);
			} else {
				curCastMember.setType(ICastMember.OTHER);
			}
		}
		return curCastMember;
	}
}
