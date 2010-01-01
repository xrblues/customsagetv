package org.jdna.metadataupdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Field;
import org.jdna.media.metadata.impl.sage.SageProperty;
import org.jdna.process.InteractiveMetadataProcessor;
import org.jdna.process.MetadataItem;

import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.progress.ProgressTracker;
import sagex.phoenix.vfs.IMediaFile;
import sagex.remote.json.JSONException;
import sagex.remote.json.JSONObject;

public class ConsoleInteractiveMetadataProcessor extends InteractiveMetadataProcessor {
    private Logger log         = Logger.getLogger(this.getClass());

    private int    displaySize = 10;
    private static Map<String, SearchQuery.Field> mappedFields = new HashMap<String, Field>();
    
    static {
        mappedFields.put(SageProperty.DISPLAY_TITLE.sageKey, Field.QUERY);
        mappedFields.put(SageProperty.DISC.sageKey, Field.DISC);
        mappedFields.put("EpisodeDate",Field.EPISODE_DATE);
        mappedFields.put(SageProperty.EPISODE_NUMBER.sageKey,Field.EPISODE);
        mappedFields.put(SageProperty.EPISODE_TITLE.sageKey, Field.EPISODE_TITLE);
        mappedFields.put(SageProperty.SEASON_NUMBER.sageKey, Field.SEASON);
        mappedFields.put("Series",Field.SERIES_ID);
        mappedFields.put(SageProperty.YEAR.sageKey, Field.YEAR);
    }

    public ConsoleInteractiveMetadataProcessor(MediaType forceType, Map<MediaType, IMediaMetadataProvider> providers, IMediaMetadataPersistence persistence, PersistenceOptions options, int displaySize) {
        super(forceType, providers, persistence, options);
        this.displaySize = displaySize;
    }

    @Override
    protected IMediaSearchResult selectResult(IMediaFile mf, SearchQuery query, List<IMediaSearchResult> results, ProgressTracker<MetadataItem> monitor) {
        // Let's prompt for results
        // draw the screen, and let the input handler decide what to do next
        log.debug("Showing Results for: " + query);
        renderResults("Search Results: " + query.get(SearchQuery.Field.QUERY), results, displaySize);

        String data = prompt("[h=help, q=quit, n=next (default), ##=use result ##, TITLE=Search TITLE]", "n", "search_results");

        if ("q".equalsIgnoreCase(data)) {
            log.info("User selected 'q'.  Aboring.");
            monitor.setTaskName("User Cancelled");
            monitor.setCancelled(true);
            setState(State.Ignore);
            return null;
        } else if ("h".equalsIgnoreCase(data)) {
            message("");
            message("");
            message("Help");
            message("Enter a search result # and press Enter, if you see your search result in the list.");
            message("or");
            message("Enter a new search title and press Enter.");
            message("or");
            message("If you want to create an advanced query, then specify the query as...");
            message("{Field: 'value', Field: 'Value', Field: 'value', ...}");
            message("Where Field is one of the following Field names...");
            message(" " + SageProperty.MEDIA_TYPE.sageKey +": [TV, Movies, Music]");
            for (String s : mappedFields.keySet()) {
                message(" " + s);
            }
            message("For example to search for The Wicker Man in 1973, you could use the search query");
            message("{Title: 'The Wicker Man', Year: '1973'}");
            message("");
            message("If you wish to quit the application, then press q, or press n to move to the next item without updating.");
            message("");
            return null;
        } else if ("n".equalsIgnoreCase(data)) {
            log.info("User Selected 'n'. Moving next item.");
            setState(State.Ignore);
            return null;
        } else if (data.startsWith("s:")) {
            String buf = data.replaceFirst("s:", "");
            log.info("User Changed Search Text: " + buf);
            query.set(Field.QUERY, buf);
            setState(State.Search);
            return null;
        } else {
            int n = 0;
            try {
                n = Integer.parseInt(data);
                log.info("User selected item: " + n + "; (User's Choice: " + data + ")");
            } catch (Exception e) {
                if (data.startsWith("{")) {
                    log.debug("Processing JSON Query: " + data);
                    // if the user specified a json query, then process it
                    try {
                        JSONObject jo = new JSONObject(data);
                        for (Iterator i = jo.keys(); i.hasNext();) {
                            String k = (String) i.next();
                            String v = jo.getString(k);
                            if (SageProperty.MEDIA_TYPE.sageKey.equalsIgnoreCase(k)) {
                                query.setMediaType(MediaType.toMediaType(v));
                            } else {
                                SearchQuery.Field f = mappedFields.get(k);
                                if (f==null) {
                                    throw new JSONException("Invalid Field: " + k);
                                }
                                log.debug("Setting Query Option: " + f + " = " + v);
                                query.set(f, v);
                            }
                        }
                    } catch (JSONException e1) {
                        monitor.setTaskName("Failed to process query string: " + data + "; Message: " + e1.getMessage());
                    }
                    setState(State.Search);
                    return null;
                } else {
                    log.debug("Failed to parse: " + data + " as a number, using it again as a search.");
                    query.set(Field.QUERY, data);
                    setState(State.Search);
                    return null;
                }
            }

            if (n>results.size()) {
                monitor.setTaskName("Number out of range: " + n);
                return null;
            }
            
            IMediaSearchResult sr = results.get(n);
            log.debug("User's Selected Title: " + sr.getTitle());
            // TODO: remember the selected title
            // ConfigurationManager.getInstance().setMetadataIdForTitle(query.get(SearchQuery.Field.QUERY), sr.getMetadataId());
            return sr;
        }
    }

    public void renderResults(String title, List<IMediaSearchResult> results, int max) {
        System.out.printf("\n\n%s\n", title);
        if (results==null) {
            System.out.println("No Results!");
            return;
        }
        int l = results.size();
        l = Math.min(l, max);
        for (int i = 0; i < l; i++) {
            IMediaSearchResult sr = results.get(i);
            System.out.printf("%2d (%1.3f - %s) - %s (%s)\n", i, sr.getScore(), sr.getProviderId(), sr.getTitle(), sr.getYear());
        }

        System.out.println("");
    }

    public void message(String message) {
        System.out.printf("%s\n", message);
    }

    public String prompt(String message, String defValue, String promptId) {
        System.out.printf("\n%s\n> ", message);
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        String resp = defValue;
        try {
            resp = r.readLine();
        } catch (IOException e) {
            log.error("Failed to get input, using default: " + defValue, e);
        }

        return resp;
    }
}
