package org.jdna.media.util;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.MediaMetadataUtils;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.impl.xbmc.XbmcScraper;
import org.jdna.media.metadata.impl.xbmc.XbmcScraperParser;
import org.jdna.media.metadata.impl.xbmc.XbmcScraperProcessor;

public class FileNameUtils {
    private static final Logger log = Logger.getLogger(FileNameUtils.class);

    private List<XbmcScraper> scrapers = new ArrayList<XbmcScraper>();
    
    public FileNameUtils(File scraperDir) {
        File files[] = scraperDir.listFiles();
        XbmcScraperParser p = new XbmcScraperParser();
        for (File f : files) {
            try {
                XbmcScraper scrap = p.parseScraper(f);
                scrapers.add(scrap);
                log.debug("Loaded Filename Scraper: " + scrap.getName());
            } catch (Exception e) {
                log.error("Failed to process scraper file: " + f.getAbsolutePath());
            }
        }
    }
    
    public SearchQuery createSearchQuery(String filenameUri) {
        // important, or else the filename parser will find %20 in the file names, not good.
        filenameUri = URLDecoder.decode(filenameUri);
        log.debug("Using Scrapers to find a query for: " + filenameUri);
        SearchQuery q = new SearchQuery();
        String args[] = new String[] {"",filenameUri};
        for (XbmcScraper x : scrapers) {
            XbmcScraperProcessor proc = new XbmcScraperProcessor(x);
            String ep = proc.executeFunction("GetEpisode", args);
            if (StringUtils.isEmpty(ep)) continue;
            
            String season = proc.executeFunction("GetSeason", args);
            if (StringUtils.isEmpty(season)) continue;
            
            String title = proc.executeFunction("GetShowName", args);
            if (StringUtils.isEmpty(title)) continue;
            
            log.debug("We have a hit for a tv show for: " + filenameUri);
            q.setType(SearchQuery.Type.TV);
            q.set(SearchQuery.Field.TITLE, MediaMetadataUtils.cleanSearchCriteria(title,false));
            q.set(SearchQuery.Field.SEASON, season);
            q.set(SearchQuery.Field.EPISODE, ep);
            break;
        }
        return q;
    }
}
