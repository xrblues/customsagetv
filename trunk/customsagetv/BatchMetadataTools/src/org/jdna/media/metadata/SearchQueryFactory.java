package org.jdna.media.metadata;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.SearchQuery.Field;
import org.jdna.media.metadata.impl.sage.SageProperty;

import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.util.PathUtils;
import sagex.remote.json.JSONException;
import sagex.remote.json.JSONObject;

public class SearchQueryFactory {
    
    private static SearchQueryFactory instance = new SearchQueryFactory();
    private static TVFileNameUtils tvFilenameUtils = new TVFileNameUtils(new File("scrapers/xbmc/tvfilenames"));
    private static MovieFileNameUtils movieFilenameUtils = new MovieFileNameUtils(new File("scrapers/xbmc/moviefilenames"));
    private static FileMatcherManager titles = new FileMatcherManager(new File("scrapers/MediaTitles.xml"));

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
                }
                
                q.setMediaType(match.getMediaType());
                
                if (!StringUtils.isEmpty(match.getTitle())) {
                    q.set(Field.RAW_TITLE, match.getTitle());
                }
                
                if (!StringUtils.isEmpty(match.getYear())) {
                    q.set(Field.YEAR, match.getYear());
                }
                
                if (match.getMetadata()!=null) {
                    q.set(Field.PROVIDER, match.getMetadata().getName());
                    q.set(Field.ID, match.getMetadata().getValue());
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

    private static Map<String, SearchQuery.Field> mappedFields = new HashMap<String, Field>();
    
    static {
        mappedFields.put(SageProperty.DISPLAY_TITLE.sageKey.toLowerCase(), Field.RAW_TITLE);
        mappedFields.put(SageProperty.DISC.sageKey.toLowerCase(), Field.DISC);
        mappedFields.put("EpisodeDate".toLowerCase(),Field.EPISODE_DATE);
        mappedFields.put(SageProperty.EPISODE_NUMBER.sageKey.toLowerCase(),Field.EPISODE);
        mappedFields.put(SageProperty.EPISODE_TITLE.sageKey.toLowerCase(), Field.EPISODE_TITLE);
        mappedFields.put(SageProperty.SEASON_NUMBER.sageKey.toLowerCase(), Field.SEASON);
        mappedFields.put("ID".toLowerCase(),Field.ID);
        mappedFields.put(SageProperty.YEAR.sageKey.toLowerCase(), Field.YEAR);
    }
    
    public static Set<String> getJSONQueryFields() {
        return mappedFields.keySet();
    }

    public void updateQueryFromJSON(SearchQuery query, String data) throws Exception {
        JSONObject jo = new JSONObject(data);
        for (Iterator i = jo.keys(); i.hasNext();) {
            String k = (String) i.next();
            String v = jo.getString(k);
            if (SageProperty.MEDIA_TYPE.sageKey.equalsIgnoreCase(k)) {
                query.setMediaType(MediaType.toMediaType(v));
            } else {
                SearchQuery.Field f = mappedFields.get(k.toLowerCase());
                if (f==null) {
                    throw new JSONException("Invalid Field: " + k);
                }
                log.debug("Setting Query Field via json args: " + f + " = " + v);
                query.set(f, v);
            }
        }
    }
    

}
