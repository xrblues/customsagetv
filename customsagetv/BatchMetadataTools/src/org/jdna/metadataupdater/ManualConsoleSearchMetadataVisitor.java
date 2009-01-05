package org.jdna.metadataupdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.util.AutomaticUpdateMetadataVisitor;

public class ManualConsoleSearchMetadataVisitor extends AutomaticUpdateMetadataVisitor {
    private static final Logger log         = Logger.getLogger(ManualConsoleSearchMetadataVisitor.class);

    private int                 displaySize = 10;

    public ManualConsoleSearchMetadataVisitor(String providerId, boolean aggressive, long options, IMediaResourceVisitor updatedVisitor, IMediaResourceVisitor notFoundHandler) {
        super(providerId, aggressive, options, updatedVisitor, notFoundHandler);
    }

    public ManualConsoleSearchMetadataVisitor(String providerId, boolean aggressive, long options, IMediaResourceVisitor updatedVisitor, IMediaResourceVisitor notFoundHandler, int displaySize) {
        super(providerId, aggressive, options, updatedVisitor, notFoundHandler);
        this.displaySize = displaySize;
    }

    protected void fetchMetaData(IMediaFile file, String name) throws Exception {
        // in manual mode, it always passes off to the show list
        handleNotFoundResults(file, name, getSearchResultsForTitle(name));
    }
    
    @Override
    protected void handleNotFoundResults(IMediaFile file, String title, List<IMediaSearchResult> results) {
        // Let's prompt for results
        // draw the screen, and let the input handler decide what to do next
        renderResults("Search Results: " + title, results, displaySize);

        String data = prompt("[q=quit, n=next (default), ##=use result ##, TITLE=Search TITLE]", "n", "search_results");

        try {
            if ("q".equalsIgnoreCase(data)) {
                throw new RuntimeException("Aborting at user request.");
            } else if ("n".equalsIgnoreCase(data)) {
                getNotFoundVisitor().visit(file);
            } else if (data.startsWith("s:")) {
                String buf = data.replaceFirst("s:", "");
                fetchMetaData(file, buf);
            } else {
                int n = 0;
                try {
                    n = Integer.parseInt(data);
                } catch (Exception e) {
                    log.warn("Debug: Failed to parse: " + data + " as a number, using it again as a search.");
                    fetchMetaData(file, data);
                    return;
                }
                IMediaSearchResult sr = results.get(n);
                IMediaMetadata md = getProvider().getMetaData(sr);
                file.updateMetadata(md, getPersistenceOptions());
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
            System.out.printf("%02d (%s) - %s [%s]\n", i, IMediaSearchResult.SEARCH_TYPE_NAMES_CHAR[sr.getResultType()], sr.getTitle(), sr.getYear());
        }
        System.out.print("LEGEND: ");
        for (int i = 0; i < IMediaSearchResult.SEARCH_TYPE_NAMES_CHAR.length; i++) {
            System.out.printf("%s %s; ", IMediaSearchResult.SEARCH_TYPE_NAMES_CHAR[i], IMediaSearchResult.SEARCH_TYPE_NAMES[i]);
        }
        System.out.println("");
    }
}
