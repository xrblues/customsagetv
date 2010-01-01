package org.jdna.media.metadata;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.SearchQuery.Field;
import org.jdna.media.metadata.impl.xbmc.XbmcScraper;
import org.jdna.media.metadata.impl.xbmc.XbmcScraperParser;
import org.jdna.media.metadata.impl.xbmc.XbmcScraperProcessor;

import sagex.api.AiringAPI;
import sagex.api.ShowAPI;
import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.util.PathUtils;

public class TVFileNameUtils {
    private static final Logger log      = Logger.getLogger(TVFileNameUtils.class);

    private List<XbmcScraper>   scrapers = new ArrayList<XbmcScraper>();

    public TVFileNameUtils(File scraperDir) {
        File files[] = scraperDir.listFiles();
        if (files != null) {
            XbmcScraperParser p = new XbmcScraperParser();
            for (File f : files) {
                try {
                    XbmcScraper scrap = p.parseScraper(f);
                    scrapers.add(scrap);
                    log.debug("Loaded TV Filename Scraper: " + scrap.getName());
                } catch (Exception e) {
                    log.error("Failed to process scraper TV file: " + f.getAbsolutePath());
                }
            }
        } else {
            log.warn("No TV Scraper Files: " + scraperDir.getAbsolutePath());
        }
    }

    public SearchQuery createSearchQuery(IMediaResource res) {
        
        // important, or else the filename parser will find %20 in the file
        // names, not good.
        String filenameUri = URLDecoder.decode(PathUtils.getLocation(res));
        log.debug("Using TV Scrapers to find a query for: " + filenameUri);
        SearchQuery q = new SearchQuery();
        q.setMediaType(MediaType.TV);
        String args[] = new String[] { "", filenameUri };
        if (scrapers != null) {
            for (XbmcScraper x : scrapers) {
                // get show name
                XbmcScraperProcessor proc = new XbmcScraperProcessor(x);
                String title = proc.executeFunction("GetShowName", args);
                if (StringUtils.isEmpty(title)) {
                    continue;
                }
                
                // trim it 
                title = title.trim();
                log.debug("Found a showname: " + title + " for " + filenameUri);
                q.set(SearchQuery.Field.RAW_TITLE, title);

                // get the year 
                String year = proc.executeFunction("GetYear", args);
                if (StringUtils.isEmpty(year)) {
                    log.warn("Scraper " + x + " failed to parse year for: " + filenameUri);
                } else {
                    q.set(SearchQuery.Field.YEAR, year.trim());
                }

                // check if title matches an aired date query
                String airedDate = proc.executeFunction("GetAiredDate", args);
                if (!StringUtils.isEmpty(airedDate)) {
                    log.debug("We have a hit for a tv show for: " + filenameUri + "; by aired date: " + airedDate);
                    q.set(SearchQuery.Field.EPISODE_DATE, airedDate);
                    break;
                }

                // continue testing if title has season and episode
                String season = proc.executeFunction("GetSeason", args);
                if (!StringUtils.isEmpty(season)) {
                    String ep = proc.executeFunction("GetEpisode", args);
                    String dp = "";

                    if (StringUtils.isEmpty(ep)) {
                        dp = proc.executeFunction("GetDisc", args);
                        if (StringUtils.isEmpty(dp)) continue;
                    }

                    log.debug("We have a hit for a tv show for: " + filenameUri + " by season: " + season + "; episode/disc: " + ep + "/" + dp);
                    q.set(SearchQuery.Field.SEASON, season);
                    q.set(SearchQuery.Field.EPISODE, ep);
                    q.set(SearchQuery.Field.DISC, dp);
                    break;
                }

                // try to find a sage title/airing
                // TODO: If sage is not enabled, then just do the compressed
                // airing title
                String sageAiringId = proc.executeFunction("GetAiringId", args);
                if (!StringUtils.isEmpty(sageAiringId)) {
                    log.debug("Using sage airing info to find a title/episode for airing: " + sageAiringId);
                    Object airing = AiringAPI.GetAiringForID(NumberUtils.toInt(sageAiringId));
                    if (airing != null) {
                        q.set(SearchQuery.Field.RAW_TITLE, AiringAPI.GetAiringTitle(airing));
                        q.set(SearchQuery.Field.EPISODE_TITLE, ShowAPI.GetShowEpisode(AiringAPI.GetShow(airing)));
                        q.set(Field.YEAR, ShowAPI.GetShowYear(AiringAPI.GetShow(airing)));
                        break;
                    }

                    log.debug("Using Title and Episode title from the commandline");
                    // let's just use the compressed title
                    q.set(SearchQuery.Field.RAW_TITLE, uncompressTitle(title));
                    q.set(SearchQuery.Field.EPISODE_TITLE, uncompressTitle(proc.executeFunction("GetEpisodeTitle", args)));
                    break;
                }
            }
        }
        
        if (StringUtils.isEmpty(q.get(Field.RAW_TITLE))) {
            log.debug("No TV Query for: " + filenameUri);
            return null;
        }

        log.debug("Created TV Query: " + q);
        return q;
    }

    public static String uncompressTitle(String title) {
        if (title == null) return null;
        return title.replaceAll("([A-Z])", " $1").trim();
    }
}
