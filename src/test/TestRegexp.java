package test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdna.media.metadata.MediaMetadataUtils;

public class TestRegexp {
    public static void main(String args[]) throws IOException {
        String reg = "avi|mpg|divx|mkv";
        String s = "avi";

        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(s);

        System.out.printf("S: %s; R: %s; Found: %s\n", s, reg, m.matches());

        testStack("Watched/Running.Scared.PROPER.DVDRip.XviD-DoNE.1.avi");
        testStack("Watched/Running.Scared.PROPER.DVDRip.XviD-DoNE.cd1.avi");
        testStack("Watched/Running.Scared.PROPER.DVDRip.XviD-DoNE.dvd1.avi");
        testStack("Watched/Running.Scared.PROPER.DVDRip.XviD-DoNE.part1.avi");
        testStack("Watched/Running.Scared.PROPER.DVDRip.XviD-DoNE.cd.1.avi");
        testStack("Watched/Running.Scared.PROPER.DVDRip.XviD-DoNE.cd.2.avi");

        testCleanTitle("Running.Scared.PROPER.DVDRip.XviD-DoNE.cd.2.avi");

        Map map = new HashMap();
        map.put("Description", "Description Test 1");
        map.put("_UserRating", "10/10");
        String mask = "${Description} -- ${_UserRating}!";
        System.out.printf("Orig: %s; New: %s\n", mask, testRegexParamParse(mask, map));

        testVOB("test1.VOB");
        testVOB("test1.VOB1");

        testActor("Kim Basinger as Stephanie");

        testIMDBSizeUrl("http://ia.media-imdb.com/images/M/MV5BNDE5NjQzMDkzOF5BMl5BanBnXkFtZTcwODI3ODI3MQ@@._V1._SX100_SY140_.jpg", "10000");

        testRatingParser("Rated PG-13 for intense sequences of sci-fi action violence, brief sexual humor, and language.");
        
        testRegexp("<div id=\"director-info\" class=\"info\">\n" + 
        		"<h5>Director:</h5>\n" + 
        		"<a href=\"/name/nm0751080/\">Chuck Russell</a><br/>\n" + 
        		"</div>\n" + 
        		"\n" + 
        		"<div class=\"info\">\n" + 
        		"", "Director.*?</h5>(.*?)</div>");
        
        testRegexp("<h5>Writers <a href=\"/wga\">(WGA)</a>:</h5>\n" + 
        		"<a href=\"/name/nm0814085/\">Stephen Sommers</a> (story) and<br/><a href=\"/name/nm0355054/\">Jonathan Hales</a> (story) ...<br/><a class=\"tn15more\" href=\"fullcredits#writers\">more</a>\n" + 
        		"</div>\n" + 
        		"\n" + 
        		"<div class=\"info\">\n" + 
        		"", "Writers.*?:</h5>(.*?)</div>");
        
        testRegexp("<Data>\n" + 
        		"<Series>\n" + 
        		"<seriesid>79349</seriesid>\n" + 
        		"<language>en</language>\n" + 
        		"<SeriesName>Dexter</SeriesName>\n" + 
        		"<banner>graphical/79349-g6.jpg</banner>\n" + 
        		"<Overview>Based on Jeff Lindsay\'s novel \"Darkly Dreaming Dexter,\" this new crime thriller tells the story of a strange man named Dexter Morgan. Once an abused and abandoned child, Dexter is now a successful forensics pathologist...but lurking just beneath his charismatic personality is a terrible truth. Dexter has corralled his innate homicidal urges into a closely guarded second career: He hunts down and brutally murders those vicious criminals who have managed to avoid the clutches of the law.</Overview>\n" + 
        		"<FirstAired>2006-10-01</FirstAired>\n" + 
        		"<IMDB_ID>tt0773262</IMDB_ID>\n" + 
        		"<zap2it_id>SH859795</zap2it_id>\n" + 
        		"<id>79349</id>\n" + 
        		"</Series>\n" + 
        		"</Data>\n" + 
        		"", "<seriesid>([0-9]*)</seriesid>[^<]*<language>([^<]*)</language>[^<]*<SeriesName>([^<]*)</SeriesName>");
        
        testRegexp("http://imdb.com/tt/tt1232?x=y&1=2;;;;season=1&episode=2", "(.*);;;;(.*)");
        testRegexp("season=1&episode=2", "&?([^=]+)=([^&$]+)");
        testRegexp("p1,p2", "([^,]+)");
        testRegexp("p1", "([^,]+)");
        
        String regexp = "^(in\\s+the|in\\s+a|i\\s+am|in|the|a|an|i|am),?\\s+(.*)";
        testReplaceRegexp("The Big Chill", regexp, "$2, $1");
        testReplaceRegexp("An Inconvenient Truth", regexp, "$2, $1");
        testReplaceRegexp("I Am Legend", regexp, "$2, $1");
        testReplaceRegexp("I, Robot", regexp, "$2, $1");
        testReplaceRegexp("What The Pick", regexp, "$2, $1");
        testReplaceRegexp("Another Time", regexp, "$2, $1");
        testReplaceRegexp("In A Time Capsule", regexp, "$2, $1");
        
        testRegexp("She's the One (1996)", "(.*)\\s+\\(?([0-9]{4})\\)?");
        testRegexp("She's the One 1996", "(.*)\\s+\\(?([0-9]{4})\\)?");
        testRegexp("Total Recall 2112 (2003)", "(.*)\\s+\\(?([0-9]{4})\\)?");
        testRegexp("Total Recall (en)", "(.*)\\s+\\(?([0-9]{4})\\)?");
        testRegexp("/test/x/y/House 2005-09-13.avi", ".*/(.+) ([0-9]{4}-[0-9]{1,2}-[0-9]{1,2})");
        testRegexp("House 2005-09-13.avi", ".*/(.+) ([0-9]{4}-[0-9]{1,2}-[0-9]{1,2})");
        testRegexp("file:/home/seans/DevelopmentProjects/workspaces/sage/MovieMetadataUpdater/target/sage/Futurama-AFishfulofDollars-2557575-0.avi", ".*/([^-]+)-([^-]+)-([0-9]+)-([0-9]{1,2})\\.");
        
        testReplaceRegexp("03-22-1973", "([0-9]+)-([0-9]+)-([0-9]+)", "$3-$1-$2");
        testReplaceRegexp("dd-22-1973", "([0-9]+)-([0-9]+)-([0-9]+)", "$3-$1-$2");
        
        String runtime="([0-9]+)(\\s+min)?";
        testRegexp("123min", runtime);
        testRegexp("126 min", runtime);
        testRegexp("124 min (canda 125 min)", runtime);
        testRegexp("129", runtime);
        
    }

    private static void testReplaceRegexp(String in, String search, String replace) {
        System.out.println("");
        System.out.println("     In: " + in);
        System.out.println(" Search: " + search);
        System.out.println("Replace: " + replace);
        String result = null;
        Pattern p = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(in);
        result = m.replaceFirst(replace);
        System.out.println(" Result: " + result);
    }

    private static void testRegexp(String string, String string2) {
        System.out.println("Looging for: " + string2 + " in " + string);
        Pattern p = Pattern.compile(string2, Pattern.MULTILINE + Pattern.CASE_INSENSITIVE + Pattern.DOTALL);
        Matcher m = p.matcher(string);
        boolean found = false;
        while (m.find()) {
            found=true;
            System.out.println("Found it.");
            for (int i=0;i<=m.groupCount();i++) {
                System.out.printf("[%s]: %s\n",i , m.group(i));
            }
        }
        if (!found) {
            System.out.println("Not Found!!");
        }
    }

    private static void testRatingParser(String str) {
        Pattern p = Pattern.compile("Rated\\s+([^ ]+).*");
        Matcher m = p.matcher(str);
        System.out.println("MPAA Description: " + str);
        if (m.find()) {
            System.out.println("MPAA Rating: " + m.group(1));
        } else {
            System.out.println("MPAA Rating Not Found in: " + str);
        }
    }

    private static void testIMDBSizeUrl(String url, String size) {
        System.out.println(" Url: " + url);
        System.out.println("Size: " + size);
        url = url.replaceFirst("\\_SX[0-9]+\\_SY[0-9]+\\_", "_SX" + size + "_SY" + size + "_");
        System.out.println(" Url: " + url);
    }

    private static void testActor(String string) {
        System.out.println("\n");
        Pattern p = Pattern.compile("(.*)\\s+as\\s+(.*)");
        Matcher m = p.matcher(string);
        if (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                System.out.printf("[%s]; %s\n", i, m.group(i));
            }
        } else {
            System.out.println("No Match! " + string + "; " + p.pattern());
        }
    }

    private static void testVOB(String name) {
        Pattern p = Pattern.compile("\\.vob$|\\.ifo$|\\.bup$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(name);
        System.out.printf("%s; %s; matched: %s", name, p.pattern(), m.find());
    }

    private static String testRegexParamParse(String s, Map map) {
        Pattern p = Pattern.compile("(\\$\\{[_\\.a-zA-Z]+\\})");
        Matcher m = p.matcher(s);
        StringBuffer sb = new StringBuffer();

        int lastStart = 0;
        while (m.find()) {
            String token = m.group(0);
            sb.append(s.substring(lastStart, m.start()));
            lastStart = m.end();
            String key = token.substring(2, token.length() - 1);
            String val = (String) map.get(key);
            if (val != null) {
                sb.append(val);
            }
            // for (int i=0;i<m.groupCount();i++) {
            // System.out.printf("Group#: %d; GroupText: %s; Start: %d; End:
            // %d\n", i, m.group(i), m.start(), m.end());
            // }
        }

        sb.append(s.substring(lastStart));

        return sb.toString();
    }

    private static void testCleanTitle(String s) {
        System.out.printf("Title: %s; Cleaned: %s\n", s, MediaMetadataUtils.cleanSearchCriteria(s, true));
    }

    public static void testStack(String s) {
        //FileMediaFile mf = new FileMediaFile(new File(s));
        //System.out.printf("String: %s; Name; %s, Basename: %s; Stacked: %s\n", s, mf.getTitle(), mf.getBasename(), new CDStackingModel().getStackedTitle(mf));

        //Pattern p = Pattern.compile(GroupProxy.get(MediaConfiguration.class).getStackingModelRegex(), Pattern.CASE_INSENSITIVE);
        //Matcher m = p.matcher(mf.getTitle());
        //if (m.find()) {
        //    System.out.printf("Regex: %s; Start Char: %s; Data: %s\n", p.pattern(), m.start(), mf.getTitle().substring(0, m.start()));
        //}
    }
}
