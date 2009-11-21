package org.jdna.media.metadata;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaFile.ContentType;
import org.jdna.media.metadata.SearchQuery.Field;
import org.jdna.media.util.FileNameUtils;

public class SearchQueryFactory {
    private static final Logger log = Logger.getLogger(SearchQueryFactory.class);
    
    private static SearchQueryFactory instance = new SearchQueryFactory();
    private static FileNameUtils filenameUtils = new FileNameUtils(new File("scrapers/xbmc/tvfilenames"));

    private static Pattern[] yearPatterns = new Pattern[] {Pattern.compile("\\(([0-9]{4})\\)"), Pattern.compile("[^0-9]+([12][0-9]{3})[^0-9]+")};

    public static SearchQueryFactory getInstance() {
        return instance;
    }
    
    public SearchQuery createTVQuery(String title) {
        return new SearchQuery(SearchQuery.Type.TV, title);
    }
    
    public SearchQuery createMovieQuery(String title) {
        return new SearchQuery(SearchQuery.Type.MOVIE, title);
    }

    public SearchQuery createQuery(IMediaResource resource, SearchQuery.Type searchType) {
        SearchQuery q = createQuery(resource);
        
        if (searchType!=null) {
            q.setType(searchType);
        }
        
        return q;
    }
    
    public SearchQuery createQuery(IMediaResource resource) {
        String name = MediaMetadataUtils.cleanSearchCriteria(resource.getTitle(), false);
        String year = parseYear(resource.getLocation().toString());

        SearchQuery q = null;
        try {
            SearchQuery tvQ = createQuery(resource.getLocation().toString());
            if (tvQ!=null && !StringUtils.isEmpty(tvQ.get(SearchQuery.Field.TITLE))) {
                return q = tvQ;
            }
        } catch (Exception e) {
            log.error("Scraper parser failed!", e);
        }
        
        if (q==null) {
            // else reqular move query
            q = new SearchQuery(SearchQuery.Type.MOVIE, name);
        }
        
        if (q!=null && year!=null) {
            q.set(Field.YEAR, year);
        }
        
        return q;
    }
    
    private static String parseYear(String uri) {
        if (uri==null) return null;
        for (Pattern p : yearPatterns) {
            Matcher m = p.matcher(uri);
            if (m.find()) {
                return m.group(1);
            }
        }
        return null;
    }

    public static SearchQuery createQuery(String uri) {
        SearchQuery q =  filenameUtils.createSearchQuery(uri);
        if (q!=null) {
            String year = parseYear(uri);
            if (year !=null) {
                q.set(Field.YEAR, year);
            }
        }
        return q;
    }
}
