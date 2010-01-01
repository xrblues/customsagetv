package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class TestMisc {
    public static void main(String args[]) throws Throwable {
        // SearchQueryFactory.getInstance().createMovieQuery("Marley *& Me");
        Pattern yearPattern = Pattern.compile("[^0-9]+([12][0-9]{3})[^0-9]+");
        Pattern yearPattern2 = Pattern.compile("\\(([0-9]{4})\\)");
        Matcher m = yearPattern.matcher("/test/12009/A 300 Movie 2008.avi");
        if (m.find()) {
            System.out.println(m.group(1));
        }
        m = yearPattern2.matcher("/test/12009/A Movie (2209).avi");
        if (m.find()) {
            System.out.println(m.group(1));
        }
    }
}
