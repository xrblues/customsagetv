package test;

import java.io.File;

import org.jdna.media.metadata.impl.xbmc.XbmcMovieProcessor;
import org.jdna.media.metadata.impl.xbmc.XbmcScraper;
import org.jdna.media.metadata.impl.xbmc.XbmcScraperParser;
import org.jdna.media.metadata.impl.xbmc.XbmcUrl;

public class TestXmbcMovieProcessor {
    public static void main(String args[]) throws Exception {
        String url = new File("imdb/find-terminator.html").toURI().toURL().toExternalForm();
        XbmcUrl u = new XbmcUrl(url);
    
        XbmcScraperParser parser = new XbmcScraperParser();
        File file = new File("/usr/share/xbmc/system/scrapers/video/imdb.xml");
        XbmcScraper scraper = parser.parseScraper(file);
        
        XbmcMovieProcessor mp = new XbmcMovieProcessor(scraper);
        
        String settings = mp.getDefaultSettings();
        System.out.println("--------- BEGIN Settings XML ---------------");
        System.out.println("Settings: " + settings);
        System.out.println("--------- End Settings XML ---------------");
        
        XbmcUrl searchUrl = mp.getSearchUrl("Scorpian King", "2002");
        System.out.println("--------- BEGIN Search URL ---------------");
        System.out.println("Search URL: " + searchUrl.toExternalForm());
        System.out.println("--------- End Search URL ---------------");
        
        String xml = mp.getSearchReulsts(u);
        // String xml = mp.getSearchReulsts(searchUrl);
        System.out.println("--------- BEGIN Search Results XML ---------------");
        System.out.println(xml);
        System.out.println("--------- END Search Results XML ---------------");
        
        String url2 = new File("imdb/The Scorpion King (2002).html").toURI().toURL().toExternalForm();
        
        //XbmcUrl u2 = new XbmcUrl(url2);
        XbmcUrl u2 = new XbmcUrl("http://www.imdb.com/title/tt0277296/");

        String details = mp.getDetails(u2,"tt0277296");
        System.out.println("--------- BEGIN Details Results XML ---------------");
        System.out.println(details);
        System.out.println("--------- END Details Results XML ---------------");
    }
}
