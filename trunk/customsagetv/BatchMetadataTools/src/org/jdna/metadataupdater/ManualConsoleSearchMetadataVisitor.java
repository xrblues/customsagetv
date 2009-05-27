package org.jdna.metadataupdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.util.AutomaticUpdateMetadataVisitor;

public class ManualConsoleSearchMetadataVisitor extends AutomaticUpdateMetadataVisitor {
    private static final Logger log         = Logger.getLogger(ManualConsoleSearchMetadataVisitor.class);

    private int                 displaySize = 10;
    
    private MetadataUpdater updater = null;

    public ManualConsoleSearchMetadataVisitor(MetadataUpdater updater, String providerId, IMediaMetadataPersistence persistence, PersistenceOptions options, SearchQuery.Type defaultSearchType, IMediaResourceVisitor updatedVisitor, IMediaResourceVisitor notFoundHandler) {
        super(providerId, persistence, options, defaultSearchType, updatedVisitor, notFoundHandler);
        this.updater=updater;
    }

    public ManualConsoleSearchMetadataVisitor(MetadataUpdater updater, String providerId, IMediaMetadataPersistence persistence, PersistenceOptions options, SearchQuery.Type defaultSearchType, IMediaResourceVisitor updatedVisitor, IMediaResourceVisitor notFoundHandler, int displaySize) {
        super(providerId, persistence, options, defaultSearchType, updatedVisitor, notFoundHandler);
        this.displaySize = displaySize;
        this.updater=updater;
    }

    protected void fetchMetaData(IMediaFile file, SearchQuery query) throws Exception {
        // in manual mode, it always passes off to the show list
        handleNotFoundResults(file, query, getSearchResultsForTitle(query));
    }
    
    @Override
    protected void handleNotFoundResults(IMediaFile file, SearchQuery query, List<IMediaSearchResult> results) {
        // Let's prompt for results
        // draw the screen, and let the input handler decide what to do next
        log.debug("Showing Results for: " + query);
        renderResults("Search Results: " + query.get(SearchQuery.Field.TITLE), results, displaySize);

        String data = prompt("[q=quit, n=next (default), ##=use result ##, TITLE=Search TITLE]", "n", "search_results");

        try {
            if ("q".equalsIgnoreCase(data)) {
                log.info("User selected 'q'.  Aboring.");
                updater.exit("User Exited.");
            } else if ("n".equalsIgnoreCase(data)) {
                log.info("User Selected 'n'. Moving next item.");
                getNotFoundVisitor().visit(file);
            } else if (data.startsWith("s:")) {
                String buf = data.replaceFirst("s:", "");
                log.info("User Changed Search Text: " + buf);
                fetchMetaData(file, SearchQuery.copy(query).set(SearchQuery.Field.TITLE, buf));
            } else {
                int n = 0;
                try {
                    n = Integer.parseInt(data);
                    log.info("User selected item: " + n + "; (User's Choice: " + data +")");
                } catch (Exception e) {
                    log.warn("Debug: Failed to parse: " + data + " as a number, using it again as a search.");
                    fetchMetaData(file, SearchQuery.copy(query).set(SearchQuery.Field.TITLE, data));
                    return;
                }
                IMediaSearchResult sr = results.get(n);
                log.debug("User's Selected Title: " + sr.getTitle());
                IMediaMetadata md = getProvider().getMetaData(sr);
                getPersistence().storeMetaData(md, file, getPersistenceOptions());

                // remember the selected title
                ConfigurationManager.getInstance().setMetadataIdForTitle(query.get(SearchQuery.Field.TITLE), sr.getMetadataId());
                if (getUpdatedVisitor() != null) getUpdatedVisitor().visit(file);
            }
        } catch (Exception e) {
            getNotFoundVisitor().visit(file);
            log.error("Failed to manually fetch metadata for media file: " + file.getLocationUri(), e);
            throw new RuntimeException(e);
        }
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

    public void renderResults(String title, List<IMediaSearchResult> results, int max) {
        System.out.printf("\n\n%s\n", title);
        int l = results.size();
        l = Math.min(l, max);
        for (int i = 0; i < l; i++) {
            IMediaSearchResult sr = results.get(i);
            System.out.printf("%02d (%02f) - %s [%s]\n", i, sr.getScore(), sr.getTitle(), sr.getYear());
        }

        System.out.println("");
    }
}
