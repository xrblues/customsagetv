package test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.impl.CDStackingModel;
import org.jdna.media.impl.MediaFile;
import org.jdna.media.metadata.MediaMetadataUtils;
import org.jdna.metadataupdater.MetadataUpdater;

public class TestRegexp {
	public static void main(String args[]) throws IOException {
		MetadataUpdater.initConfiguration();
		
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
		map.put("Description","Description Test 1");
		map.put("_UserRating", "10/10");
		String mask = "${Description} -- ${_UserRating}!";
		System.out.printf("Orig: %s; New: %s\n", mask, testRegexParamParse(mask, map));
		
		testVOB("test1.VOB");
		testVOB("test1.VOB1");
		
		testActor("Kim Basinger as Stephanie");
	}
	
	private static void testActor(String string) {
		System.out.println("\n");
		Pattern p = Pattern.compile("(.*)\\s+as\\s+(.*)");
		Matcher m =p.matcher(string);
		if (m.find()) {
			for (int i=0;i<=m.groupCount();i++) {
				System.out.printf("[%s]; %s\n", i, m.group(i));
			}
		} else {
			System.out.println("No Match! " + string + "; " + p.pattern());
		}
	}

	private static void testVOB(String name) {
		Pattern p = Pattern.compile("\\.vob$|\\.ifo$|\\.bup$",Pattern.CASE_INSENSITIVE);
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
			lastStart=m.end();
			String key = token.substring(2,token.length()-1);
			String val = (String) map.get(key);
			if (val!=null) {
				sb.append(val);
			}
			//for (int i=0;i<m.groupCount();i++) {
			//	System.out.printf("Group#: %d; GroupText: %s; Start: %d; End: %d\n", i, m.group(i), m.start(), m.end());
			//}
		}
		
		sb.append(s.substring(lastStart));
		
		return sb.toString();
	}

	private static void testCleanTitle(String s) {
		System.out.printf("Title: %s; Cleaned: %s\n", s, MediaMetadataUtils.cleanSearchCriteria(s, true));
	}

	public static void testStack(String s) {
		MediaFile mf = new MediaFile(new File(s).toURI());
		System.out.printf("String: %s; Name; %s, Basename: %s; Stacked: %s\n",s , mf.getTitle(), mf.getBasename(), CDStackingModel.INSTANCE.getStackedTitle(mf));
		
		Pattern p = Pattern.compile(ConfigurationManager.getInstance().getMediaConfiguration().getStackingModelRegex(), Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(mf.getTitle());
		if (m.find()) {
			System.out.printf("Regex: %s; Start Char: %s; Data: %s\n", p.pattern(), m.start(), mf.getTitle().substring(0,m.start()));
		}
	}
}
