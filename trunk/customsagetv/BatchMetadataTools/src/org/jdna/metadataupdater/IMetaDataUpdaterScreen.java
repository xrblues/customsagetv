package org.jdna.metadataupdater;

import java.util.List;

import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoMetaDataProvider;
import org.jdna.media.metadata.IVideoSearchResult;

/**
 * Abstract way of interacting with a display device.  Using this, the same tool can use difference "Screens" in order to enable the same tool to work in different mods.
 * 
 * It was created so that all the work done in the MetadataUpdater tool could be reused without modifications in a web environment, or in a Sage UI environment.
 * 
 * The notifyXXX() methods can be used by the screen to track updates, etc.  Or they can be ignored totally.
 * 
 * renderXXX() methods are used to draw a screen.
 * s
 * @author seans
 *
 */
public interface IMetaDataUpdaterScreen {
	/**
	 * Simple Structre to hold a media file and its metadata.  Used by the renderMovies() method
	 * 
	 * @author seans
	 *
	 */
	public class MovieEntry {
		public MovieEntry(IMediaFile mediaFile, IVideoMetaData md) {
			this.file=mediaFile;
			this.metadata=md;
		}
		public IMediaFile file;
		public IVideoMetaData metadata;
	}
	

	/**
	 * Send an error to the Screen.
	 * 
	 * @param message
	 */
	public void error(String message);
	
	/**
	 * Send a general message to the screen.  Screens should not block while showing the message.
	 * @param message
	 */
	public void message(String message);
	
	/**
	 * Get user input from the Screen.
	 * 
	 * A prompt must block until it has data.
	 * 
	 * @param message Message to show the user
	 * @param defValue default value if the user sends back an empty response
	 * @param promptId the prompt id of this prompt, in the event that there are multiple prompts being used.
	 * @return user responded data
	 */
	public String prompt(String message, String defValue, String promptId);
	
	
	/**
	 * Notify the screen that a file is being processed.
	 * @param r
	 */
	public void notifyProcessingFile(IMediaResource r);
	
	/**
	 * Notify the screen that a file failed to process.
	 * @param r
	 * @param e
	 */
	public void notifyFailedFile(IMediaResource r, Exception e);
	
	/**
	 * Notify the screen that file has been skipped.
	 * @param r
	 */
	public void nofifySkippedFile(IMediaResource r);
	
	/**
	 * Notify the screen that a file has been updated. 
	 * @param r
	 * @param md
	 */
	public void notifyUpdatedFile(IMediaResource r, IVideoMetaData md);
	
	/**
	 * Notify the screen that the file required manual intervention in order to be updated.
	 * @param r
	 * @param md
	 */
	public void notifyManualUpdate(IMediaResource r, IVideoMetaData md);
	
	/**
	 * Render a screen showing the curent list of installed metadata providers.
	 * 
	 * @param providers List of providers
	 * @param defaultProvider id of the default provider being used.
	 */
	public void renderProviders(List<IVideoMetaDataProvider> providers, String defaultProvider);
	
	/**
	 * Render a screen showing a list of known movies that the tool would find.
	 * 
	 * @param allMovies List of MovieEntry objects
	 */
	public void renderKnownMovies(List<MovieEntry> allMovies);
	
	/**
	 * Render a screen of search results for a given movie.
	 * 
	 * @param title Screen Title
	 * @param results List of IVideoSearchResult objects
	 * @param max maximum # of results to show
	 */
	public void renderResults(String title, List<IVideoSearchResult> results, int max);
	
	/**
	 * Once process is done, renderStats() will be called to render information to the user about the processing.
	 */
	public void renderStats();
}
