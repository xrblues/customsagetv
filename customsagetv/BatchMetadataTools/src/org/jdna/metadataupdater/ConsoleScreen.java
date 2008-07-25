package org.jdna.metadataupdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoMetaDataProvider;
import org.jdna.media.metadata.IVideoSearchResult;

/**
 * IMetaDataUpdaterScreen implemenation that uses a standard text based console.
 * 
 * 
 * 
 * @author seans
 *
 */
public class ConsoleScreen implements IMetaDataUpdaterScreen {
	private static final Logger log = Logger.getLogger(ConsoleScreen.class);
	
	private class Failed {
		public Failed(IMediaResource r, Exception e) {
			this.res=r;
			this.error=e;
		}
		public IMediaResource res;
		public Exception error;
	}
	
	private int processed = 0;
	private int skipped = 0;
	private int updated = 0;
	private List<Failed> failed = new ArrayList<Failed>();
	private int manual = 0;
	
	public void error(String message) {
		System.out.printf("** Error: %s **\n", message);
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

	public void renderResults(String title, List<IVideoSearchResult> results, int max) {
		System.out.printf("\n\n%s\n",title);
		int l = results.size();
		l = Math.min(l, max);
		for (int i=0;i<l;i++) {
			IVideoSearchResult sr = results.get(i);
			System.out.printf("%02d (%s) - %s [%s]\n", i, IVideoSearchResult.SEARCH_TYPE_NAMES_CHAR[sr.getResultType()], sr.getTitle(), sr.getYear());			
		}
		System.out.print("LEGEND: ");
		for (int i=0;i<IVideoSearchResult.SEARCH_TYPE_NAMES_CHAR.length;i++) {
			System.out.printf("%s %s; ", IVideoSearchResult.SEARCH_TYPE_NAMES_CHAR[i], IVideoSearchResult.SEARCH_TYPE_NAMES[i]);
		}
		System.out.println("");
	}

	public void renderStats() {
		System.out.println("\n\nMetaData Stats...");
		System.out.printf("Processed: %d; Updated: %d (%d manual), Skipped: %d; Errors: %d.\n\n", processed, updated, manual, skipped,failed.size());
		if (failed.size()>0) {
			System.out.println("Listing Failed Videos....");
			for (Failed f : failed) {
				System.out.printf("Video: %s\n", f.res.getLocationUri());
				if (f.error!=null) {
					System.out.printf("Error: %s\n", f.error.getMessage());
				}
				System.out.println("");
			}
		}
		
		if (updated>0) {
			System.out.println("\nBe sure to go into SageTV and select\n'Media Center -> Videos -> Options -> Advanced Options -> Refresh Imported Media'\nTo Ensure that your metadata and images get refreshed.\n");
		}
		
	}

	public void renderProviders(List<IVideoMetaDataProvider> providers, String defaultProvider) {
		System.out.println("\n\nInstalled Metadata Providers (*=default");
		for (IVideoMetaDataProvider p : providers) {
			System.out.printf("%1s %-20s %s\n", (p.getId().equals(defaultProvider)?"*":""), p.getId(), p.getName());
		}
	}

	public void renderKnownMovies(List<MovieEntry> allMovies) {
		System.out.printf("\nListing Movies\nU = MetaData Updated/Newer; - = Missing MetaData; + = Has MetaData\n");
		for (MovieEntry me : allMovies) {
			IVideoMetaData md = me.metadata;
			IMediaFile mediaFile = me.file;
			String code = ((md==null) ? "-" : "+");
			if (md!=null && md.isUpdated()) code="U";
			System.out.printf("%s %-40s (%s)\n" , code, mediaFile.getTitle(), mediaFile.getLocationUri());
		}
	}

	public void nofifySkippedFile(IMediaResource r) {
		skipped++;
		System.out.printf("%10s: %s\n","Skip", r.getTitle());
	}

	public void notifyFailedFile(IMediaResource r, Exception e) {
		failed.add(new Failed(r, e));
		System.out.printf("%10s: %s\n", "Failed", r.getTitle());
	}

	public void notifyProcessingFile(IMediaResource r) {
		processed++;
	}

	public void notifyManualUpdate(IMediaResource r, IVideoMetaData md) {
		manual++;
	}

	public void notifyUpdatedFile(IMediaResource r, IVideoMetaData md) {
		updated++;
		if (md==null) {
			System.out.println("Updated: " + r.getTitle());
		} else {
			System.out.printf("%10s: %-30s (%s)\n","Updated", md.getTitle(), r.getName());
		}
	}
}
