package org.jdna.media.metadata;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.SearchQuery.Field;
import org.jdna.media.metadata.impl.xbmc.XbmcScraper;
import org.jdna.media.metadata.impl.xbmc.XbmcScraperParser;
import org.jdna.media.metadata.impl.xbmc.XbmcScraperProcessor;

import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.util.PathUtils;

public class MovieFileNameUtils {
    private static final Logger log      = Logger.getLogger(MovieFileNameUtils.class);

    private List<XbmcScraper>   scrapers = new ArrayList<XbmcScraper>();

    public MovieFileNameUtils(File scraperDir) {
        File files[] = scraperDir.listFiles();
        if (files != null) {
            XbmcScraperParser p = new XbmcScraperParser();
            for (File f : files) {
                try {
                    XbmcScraper scrap = p.parseScraper(f);
                    scrapers.add(scrap);
                    log.debug("Loaded Movie Filename Scraper: " + scrap.getName());
                } catch (Exception e) {
                    log.error("Failed to process Movie scraper file: " + f.getAbsolutePath());
                }
            }
        } else {
            log.warn("No Movies Scraper Files: " + scraperDir.getAbsolutePath());
        }
    }

    public SearchQuery createSearchQuery(IMediaResource res) {
        // important, or else the filename parser will find %20 in the file
        // names, not good.
        String filenameUri = URLDecoder.decode(PathUtils.getLocation(res));
        log.debug("Using Movie Scrapers to find a query for: " + filenameUri);
        SearchQuery q = new SearchQuery();
        q.setMediaType(MediaType.MOVIE);
        
        String args[] = new String[] { "", filenameUri };
        if (scrapers != null) {
            for (XbmcScraper x : scrapers) {
                XbmcScraperProcessor proc = new XbmcScraperProcessor(x);
                String title = proc.executeFunction("GetTitle", args);
                if (StringUtils.isEmpty(title)) {
                    continue;
                }

                title = title.trim();
                log.debug("Found a Movie: " + title + " for " + filenameUri);
                q.set(Field.RAW_TITLE, title);
                
                // get the year 
                String year = proc.executeFunction("GetYear", args);
                if (StringUtils.isEmpty(year)) {
                    log.warn("Movie Scraper " + x + " failed to parse year for: " + filenameUri);
                } else {
                    q.set(SearchQuery.Field.YEAR, year.trim());
                }
                
                // we have a title, so break
                break;
            }
        }

        // TODO: Test if the Sage Object is a Recording and if it's a Movie

        if (StringUtils.isEmpty(q.get(Field.RAW_TITLE))) {
            log.warn("Failed to parse move title using scrapers for: " + filenameUri + ", will use filename as the movie title.");
            q.set(Field.RAW_TITLE, PathUtils.getBasename(res));
        }
        
        log.debug("Created Movie Query: " + q);
        return q;
    }

    public static String uncompressTitle(String title) {
        if (title == null) return null;
        return title.replaceAll("([A-Z])", " $1").trim();
    }
}
