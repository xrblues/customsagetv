package org.jdna.media.metadata;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.IMediaResource;
import org.jdna.media.util.FileNameUtils;

public class SearchQueryFactory {
    private static final Logger log = Logger.getLogger(SearchQueryFactory.class);
    
    private static SearchQueryFactory instance = new SearchQueryFactory();
    private static FileNameUtils filenameUtils = new FileNameUtils(new File("scrapers/xbmc/tvfilenames"));
    
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
        String name = MediaMetadataUtils.cleanSearchCriteria(resource.getTitle(), false);

        try {
            SearchQuery tvQ = filenameUtils.createSearchQuery(resource.getLocationUri());
            if (tvQ!=null && !StringUtils.isEmpty(tvQ.get(SearchQuery.Field.TITLE))) {
                return tvQ;
            }
        } catch (Exception e) {
            log.error("Scraper parser failed!", e);
        }
        
        // else reqular move query
        SearchQuery q = new SearchQuery(SearchQuery.Type.MOVIE, name);
        
        if (searchType!=null) {
            q.setType(searchType);
        }
        return q;
    }
    
    public SearchQuery createQuery(IMediaResource resource) {
        String name = MediaMetadataUtils.cleanSearchCriteria(resource.getTitle(), false);
        
        try {
            SearchQuery tvQ = filenameUtils.createSearchQuery(resource.getLocationUri());
            if (tvQ!=null && !StringUtils.isEmpty(tvQ.get(SearchQuery.Field.TITLE))) {
                return tvQ;
            }
        } catch (Exception e) {
            log.error("Scraper parser failed!", e);
        }
        
        // else reqular move query
        SearchQuery q = new SearchQuery(SearchQuery.Type.MOVIE, name);
        return q;
    }
}
