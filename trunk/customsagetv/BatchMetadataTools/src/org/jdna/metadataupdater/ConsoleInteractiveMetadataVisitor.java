package org.jdna.metadataupdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;

import sagex.phoenix.Phoenix;
import sagex.phoenix.metadata.IMetadataSearchResult;
import sagex.phoenix.metadata.search.SearchQuery;
import sagex.phoenix.metadata.search.SearchQuery.Field;
import sagex.phoenix.metadata.search.SearchQueryFactory;
import sagex.phoenix.progress.IProgressMonitor;
import sagex.phoenix.progress.ProgressTracker;
import sagex.phoenix.util.Hints;
import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.IMediaResourceVisitor;

public class ConsoleInteractiveMetadataVisitor implements IMediaResourceVisitor {
	private Logger log = Logger.getLogger(ConsoleInteractiveMetadataVisitor.class);

	private enum State {Ignore, Search}
	private State state = State.Search;
	
	private int displaySize;
	private Hints options = null;
	
	public ConsoleInteractiveMetadataVisitor(int displaySize, Hints options) {
		this.options=options;
		if (this.options==null) this.options = Phoenix.getInstance().getMetadataManager().getDefaultMetadataOptions();
		this.displaySize=displaySize;
	}

	public boolean visit(IMediaResource res, IProgressMonitor monitor) {
		state = State.Search;
		while (state == State.Search && !(monitor.isCancelled() || monitor.isDone())) {
			try {
				SearchQuery query = Phoenix.getInstance().getSearchQueryFactory().createQueryFromFilename((IMediaFile) res, options);
				List<IMetadataSearchResult> results = Phoenix.getInstance().getMetadataManager().search(query);
				IMetadataSearchResult result = selectResult((IMediaFile)res, query, results, (ProgressTracker<IMediaFile>) monitor);
				if (state!=State.Ignore && result!=null) {
					Phoenix.getInstance().getMetadataManager().updateMetadata((IMediaFile) res, Phoenix.getInstance().getMetadataManager().getMetdata(result), options);
				}
			} catch (Exception e) {
				log.warn("Interactive Search Failed!", e);
				message("Error: " + e.getMessage());
			}
		}
		return true;
	}
	
    protected IMetadataSearchResult selectResult(IMediaFile mf, SearchQuery query, List<IMetadataSearchResult> results, ProgressTracker<IMediaFile> monitor) {
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
            message(" MediaType: [TV, Movies, Music]");
            for (String s : SearchQueryFactory.getJSONQueryFields()) {
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
                    try {
                        // if the user specified a json query, then process it
                        Phoenix.getInstance().getSearchQueryFactory().updateQueryFromJSON(query,data);
                    } catch (Exception e1) {
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
            
            IMetadataSearchResult sr = results.get(n);
            log.debug("User's Selected Title: " + sr.getTitle());
            return sr;
        }
    }

    private void setState(State state) {
    	this.state=state;
	}

	public void renderResults(String title, List<IMetadataSearchResult> results, int max) {
        System.out.printf("\n\n%s\n", title);
        if (results==null) {
            System.out.println("No Results!");
            return;
        }
        int l = results.size();
        l = Math.min(l, max);
        for (int i = 0; i < l; i++) {
            IMetadataSearchResult sr = results.get(i);
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
