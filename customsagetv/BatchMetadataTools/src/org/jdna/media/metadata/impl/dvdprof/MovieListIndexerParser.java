package org.jdna.media.metadata.impl.dvdprof;

import org.apache.log4j.Logger;
import org.jdna.url.URLSaxParser;
import org.jdna.url.UrlUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MovieListIndexerParser extends URLSaxParser {
	private static final Logger log = Logger.getLogger(MovieListIndexerParser.class);
	
	private class Entry {
		private String name;
		private String date;
		private String url;
	}
	
	private static final int STARTING=0;
	private static final int PARSE_DVD_TITLE=1;
	private static final int PARSE_DVD_RELEASE=2;
	private static final int ENDED=99;
	
	private int state = STARTING;
	
	private String baseUrl = null;
	private Entry entry = null;
	private String charbuf;
	
	public MovieListIndexerParser(String url) {
		super(url);
		
		baseUrl = UrlUtil.getBaseUrl(url);
	}

	
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);

		if (state==STARTING || state==ENDED) return;
		
		charbuf = getCharacters(ch, start, length);
		if (charbuf==null) return;
		if (state==PARSE_DVD_TITLE) {
			entry.name=charbuf;
			state=PARSE_DVD_RELEASE;
		} else if (state==PARSE_DVD_RELEASE) {
			entry.date=charbuf;
			state=STARTING;

			try {
				processEntry(entry);
			} catch (Exception e) {
				log.error("Failed to index entry: " + entry.name + ";" + entry.url, e);
			}
			entry=null;
		}
	}

	private void processEntry(Entry entry2) throws Exception {
		MovieIndex.getInstance().addMovie(entry2.name, entry2.date, baseUrl +"/"+ entry2.url);
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes atts) throws SAXException {

		if (state==STARTING && isTag("a", localName)) {
			String target = atts.getValue("target");
			log.debug("Target: " + target);
			if ("entry".equalsIgnoreCase(target)) {
				log.debug("Got a Movie");
				entry = new Entry();
				entry.url = atts.getValue("href");
				state=PARSE_DVD_TITLE;
			}
		}
	}
	
	

}
