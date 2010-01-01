package org.jdna.media.metadata;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.SearchQuery.Field;

import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.util.PathUtils;

public class SearchQueryFactory {
    
    private static SearchQueryFactory instance = new SearchQueryFactory();
    private static TVFileNameUtils tvFilenameUtils = new TVFileNameUtils(new File("scrapers/xbmc/tvfilenames"));
    private static MovieFileNameUtils movieFilenameUtils = new MovieFileNameUtils(new File("scrapers/xbmc/moviefilenames"));
    private static FileMatcherManager titles = new FileMatcherManager(new File("scrapers/MediaTitles.xml"));

    // private static Pattern[] yearPatterns = new Pattern[] {Pattern.compile("\\(([0-9]{4})\\)"), Pattern.compile("[^0-9]+([12][0-9]{3})[^0-9]+")};
    private Logger log = Logger.getLogger(SearchQueryFactory.class);

    public static SearchQueryFactory getInstance() {
        return instance;
    }
    
    public SearchQuery createTVQuery(String title) {
        return new SearchQuery(MediaType.TV, title);
    }
    
    public SearchQuery createMovieQuery(String title) {
        return new SearchQuery(MediaType.MOVIE, title);
    }

    public SearchQuery createQuery(IMediaResource resource, MediaType searchType) {
        SearchQuery q = createQuery(resource);
        
        if (searchType!=null) {
            q.setMediaType(searchType);
        }
        
        return q;
    }
    
    public SearchQuery createQuery(IMediaResource resource) {
        SearchQuery q = null;
        
        // try to create TV query
        try {
            q = tvFilenameUtils.createSearchQuery(resource);
        } catch (Exception e) {
            log.warn("TV Title scrapers failed!", e);
        }
        
        // if no query, then try a TV query
        if (q==null) {
            try {
                q = movieFilenameUtils.createSearchQuery(resource);
            } catch (Exception e) {
                log.warn("Movie Title scrapers failed!", e);
            }
        }
        
        // TODO: Support Music Queries

        // fill in the query information from the configured titles
        File f = PathUtils.getFirstFile((IMediaFile)resource);
        if (f!=null) {
            FileMatcher match = titles.getMatcher(f.getAbsolutePath());
            if (match!=null) {
                if (q==null) {
                    q = new SearchQuery();
                    if (match.getSeries()!=null || (match.getMetadata()!=null && match.getMetadata().getName().contains("tv"))) {
                        q.setMediaType(MediaType.TV);
                    } else {
                        q.setMediaType(MediaType.MOVIE);
                    }
                }
                
                if (!StringUtils.isEmpty(match.getTitle())) {
                    q.set(Field.RAW_TITLE, match.getTitle());
                }
                
                if (!StringUtils.isEmpty(match.getYear())) {
                    q.set(Field.YEAR, match.getYear());
                }
                
                if (match.getSeries()!=null) {
                    q.set(Field.SERIES_ID, match.getSeries().getName() + ":" + match.getSeries().getValue());
                }
                
                if (match.getMetadata()!=null) {
                    q.set(Field.METADATA_ID, match.getMetadata().getName() + ":" + match.getMetadata().getValue());
                }
            }
        }
        
        if (q!=null) {
            if (f!=null) {
                q.set(Field.FILE, f.getAbsolutePath());
            }
        }

        if (q==null) {
            log.warn("Failed to create Search Query for: " + resource);
        } else {
            // add in a cleaned title
            q.set(Field.CLEAN_TITLE, MediaMetadataUtils.cleanSearchCriteria(q.get(Field.RAW_TITLE), false));
        }
        
        return q;
    }
}
