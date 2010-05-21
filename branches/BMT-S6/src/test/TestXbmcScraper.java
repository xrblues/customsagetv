package test;

import java.io.File;

import org.jdna.media.metadata.impl.xbmc.Expression;
import org.jdna.media.metadata.impl.xbmc.RegExp;
import org.jdna.media.metadata.impl.xbmc.ScraperFunction;
import org.jdna.media.metadata.impl.xbmc.XbmcScraper;
import org.jdna.media.metadata.impl.xbmc.XbmcScraperParser;
import org.jdna.media.metadata.impl.xbmc.XbmcScraperProcessor;

public class TestXbmcScraper {
    public static void main(String args[]) throws Exception {
        XbmcScraperParser parser = new XbmcScraperParser();
        File file = new File("/usr/share/xbmc/system/scrapers/video/imdb.xml");
        XbmcScraper scraper = parser.parseScraper(file);
        
        System.out.printf("Dumping Scraper: %s; Thumb: %s; Content: %s\n", scraper.getName(), scraper.getThumb(), scraper.getContent());
        for (ScraperFunction f: scraper.getFunctions()) {
            System.out.printf("Function: %s; dest: %s; clearbuffers=%s\n",f.getName(), f.getDest(), f.isClearBuffers());
        }
        
        //dumpFunction(scraper.getFunction("GetMovieWriters"));
        dumpFunction(scraper.getFunction("GetMoviePlot"));
        
        // now test executing the scraper
        executeFunction(scraper, "CreateSearchUrl", new String[] {"", "Test Movie"});
        executeFunction(scraper, "GetDetails", null);
    }
    
    private static void executeFunction(XbmcScraper scraper, String funcName, String input[]) {
        XbmcScraperProcessor proc = new XbmcScraperProcessor(scraper);
        System.out.println("Excuting Function: " + funcName);
        System.out.println("Output: " + proc.executeFunction(funcName, input));
    }

    public static void dumpFunction(ScraperFunction func) {
        System.out.printf("------ Begin Function: %s -----------\n", func.getName());
        System.out.printf("      Dest: %s\n", func.getDest());
        for (RegExp r : func.getRegExps()) {
            dumpRegExp("   ", r);
        }
        System.out.printf("------   End Function: %s -----------\n", func.getName());
    }

    private static void dumpRegExp(String indent, RegExp regExp) {
        System.out.printf("%s ----- Begin RegExp -----\n", indent);
        System.out.printf("%s  Input: %s\n", indent, regExp.getInput());
        System.out.printf("%s Output: %s\n", indent, regExp.getOutput());
        System.out.printf("%s   Dest: %s\n", indent, regExp.getDest());
        if (regExp.hasRegExps()) {
            for (RegExp r : regExp.getRegExps()) {
                dumpRegExp("   " + indent, r);
            }
        }
        System.out.printf("%s -- Begin Expression\n", indent);
        Expression exp = regExp.getExpression();
        System.out.printf("%s      clear: %s\n", indent, exp.isClear());
        System.out.printf("%s     repeat: %s\n", indent, exp.isRepeat());
        System.out.printf("%s    noclean: %s\n", indent, exp.getNoClean());
        System.out.printf("%s expression: %s\n", indent, exp.getExpression());
        System.out.printf("%s -- End Expression\n", indent);
        System.out.printf("%s -----   End RegExp -----\n", indent);
    }
}
