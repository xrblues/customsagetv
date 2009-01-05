package org.jdna.media.metadata.impl.dvdprof;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdna.media.metadata.CastMember;
import org.jdna.media.metadata.ICastMember;
import org.jdna.media.metadata.IMediaArt;
import org.jdna.media.metadata.MediaArt;
import org.jdna.media.metadata.MediaMetadata;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.url.CookieHandler;
import org.jdna.url.URLSaxParser;
import org.jdna.url.UrlUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DVDProfMetaDataParser extends URLSaxParser {
    private static final int    READING           = 0;
    private static final int    YEAR              = 1;
    private static final int    DIRECTORS         = 2;
    private static final int    RATING            = 3;
    private static final int    RELEASE           = 4;
    private static final int    RUNNINGTIME       = 6;
    private static final int    ASPECTRATIO       = 7;
    private static final int    SRP               = 8;
    private static final int    PLOT              = 9;
    private static final int    GENRES            = 10;
    private static final int    STUDIO            = 11;
    private static final int    ACTORS            = 12;
    private static final int    TITLE             = 14;
    private static final int    DONE              = 99;

    private static final String YEAR_TOKEN        = "Production\\s+Year:";
    private static final String DIRECTOR_TOKEN    = "Direction:";
    private static final String RATING_TOKEN      = "Rating:";
    private static final String RELEASE_TOKEN     = "DVD\\s+Release:";
    private static final String RUNNINGTIME_TOKEN = "Running\\s+Time:";
    private static final String ASPECTRATIO_TOKEN = "Video\\s+Formats:";
    private static final String SRP_TOKEN         = "SRP:";
    private static final String GENRES_TOKEN      = "Genres";
    private static final String STUDIO_TOKEN      = "Studios";
    private static final String ACTORS_TOKEN      = "Actors";

    private int                 state             = READING;

    private MediaMetadata       metadata          = new MediaMetadata(new MetadataKey[] {
            MetadataKey.ASPECT_RATIO,
            MetadataKey.COMPANY,
            MetadataKey.DESCRIPTION,
            MetadataKey.GENRE_LIST,
            MetadataKey.MEDIA_ART_LIST,
            MetadataKey.MPAA_RATING,
            MetadataKey.POSTER_ART,
            MetadataKey.PROVIDER_DATA_URL,
            MetadataKey.PROVIDER_ID,
            MetadataKey.RELEASE_DATE,
            MetadataKey.RUNNING_TIME,
            MetadataKey.TITLE,
            MetadataKey.USER_RATING,
            MetadataKey.YEAR                     });

    private String              baseUrl;
    private String              charbuf;

    public DVDProfMetaDataParser(String dataUrl, CookieHandler handler) throws Exception {
        super(dataUrl);
        metadata.setProviderId(DVDProfMetaDataProvider.PROVIDER_ID);
        metadata.setProviderDataUrl(dataUrl);
        this.baseUrl = UrlUtil.getBaseUrl(dataUrl);

        // force a parse of the metadata.
        parse(handler);

        if (metadata.getYear() == null) {
            throw new Exception("Could not parse: " + metadata.getProviderDataUrl() + " as a valid DVD Profiler URL!");
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (state == DONE) return;

        super.characters(ch, start, length);

        charbuf = getCharacters(ch, start, length);
        if (charbuf == null || charbuf.trim().length() == 0) return;

        if (state == YEAR) {
            metadata.setYear(charbuf);
            state = READING;
            return;
        } else if (state == DIRECTORS) {
            Pattern p = Pattern.compile("([^:]+):\\s+(.+)");
            Matcher m = p.matcher(charbuf);
            if (m.find()) {
                String role = m.group(2);
                if (role != null) {
                    if (role.equalsIgnoreCase("director")) {
                        CastMember cm = new CastMember(ICastMember.DIRECTOR);
                        cm.setName(m.group(1));
                        metadata.addCastMember(cm);
                    } else if (role.equalsIgnoreCase("writer")) {
                        CastMember cm = new CastMember(ICastMember.WRITER);
                        cm.setName(m.group(1));
                        metadata.addCastMember(cm);
                    } else {
                        CastMember cm = new CastMember(ICastMember.OTHER);
                        cm.setName(m.group(1));
                        cm.setPart(m.group(2));
                        metadata.addCastMember(cm);
                    }
                }
            }
            return;
        } else if (state == TITLE) {
            metadata.setTitle(charbuf);
            state = READING;
            return;
        } else if (state == RATING) {
            metadata.setMPAARating(charbuf);
            state = READING;
            return;
        } else if (state == RELEASE) {
            metadata.setReleaseDate(charbuf);
            state = READING;
            return;
        } else if (state == RUNNINGTIME) {
            metadata.setRuntime(charbuf);
            state = READING;
            return;
        } else if (state == ASPECTRATIO) {
            metadata.setAspectRatio(charbuf);
            state = READING;
            return;
        } else if (state == SRP) {
            // plot follows the SRP and it's the only way for us to identify it
            state = PLOT;
            return;
        } else if (state == PLOT) {
            if (metadata.getDescription() == null) {
                metadata.setDescription(charbuf);
            } else {
                metadata.setDescription(metadata.getDescription() + ("\n" + charbuf));
            }
            // keep the state == plot until we read td
            state = PLOT;
            return;
        } else if (state == GENRES) {
            metadata.addGenre(charbuf);
            return;
        } else if (state == STUDIO) {
            metadata.setCompany(charbuf);
            state = READING;
            return;
        } else if (state == ACTORS) {
            Pattern p = Pattern.compile("(.*)\\s+as\\s+(.*)");
            Matcher m = p.matcher(charbuf);
            if (m.find()) {
                CastMember cm = new CastMember(ICastMember.ACTOR);
                cm.setName(m.group(1));
                cm.setPart(m.group(2));
                metadata.addCastMember(cm);
            }
            return;
        }

        if (state == READING) {
            if (charbuf.matches(YEAR_TOKEN)) {
                state = YEAR;
            } else if (charbuf.matches(DIRECTOR_TOKEN)) {
                state = DIRECTORS;
            } else if (charbuf.matches(RATING_TOKEN)) {
                state = RATING;
            } else if (charbuf.matches(RELEASE_TOKEN)) {
                state = RELEASE;
            } else if (charbuf.matches(RUNNINGTIME_TOKEN)) {
                state = RUNNINGTIME;
            } else if (charbuf.matches(ASPECTRATIO_TOKEN)) {
                state = ASPECTRATIO;
            } else if (charbuf.matches(SRP_TOKEN)) {
                state = SRP;
            } else if (charbuf.matches(GENRES_TOKEN)) {
                state = GENRES;
            } else if (charbuf.matches(STUDIO_TOKEN)) {
                state = STUDIO;
            } else if (charbuf.matches(ACTORS_TOKEN)) {
                state = ACTORS;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {

        if (state == DIRECTORS && isTag("td", localName)) state = READING;

        // end the plot once we reach the span after the text
        if (state == PLOT && metadata.getDescription() != null && isTag("span", localName)) state = READING;

        // end genres after the table
        if (state == GENRES && isTag("table", localName)) state = READING;

        // end actors after the table
        if (state == ACTORS && isTag("table", localName)) state = DONE;

    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
        if (metadata.getPoster() == null && metadata.getDescription() != null && isTag("img", localName)) {
            String src = atts.getValue("src");
            MediaArt ma = new MediaArt();
            ma.setProviderId(metadata.getProviderId());
            ma.setDownloadUrl(UrlUtil.joinUrlPath(UrlUtil.getDomainUrl(baseUrl), src));
            ma.setType(IMediaArt.POSTER);
            ma.setLabel(null);
            metadata.addMediaArt(ma);
        }

        if (state == READING && isTag("td", localName) && "styletitle".equals(attr(atts, "class"))) {
            // found title
            state = TITLE;
        }
    }

    public MediaMetadata getMetaData() {
        return metadata;
    }
}
