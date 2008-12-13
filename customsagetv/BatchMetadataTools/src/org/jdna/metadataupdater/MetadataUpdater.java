package org.jdna.metadataupdater;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdna.cmdline.CommandLine;
import org.jdna.cmdline.CommandLineArg;
import org.jdna.cmdline.CommandLineProcess;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.MediaResourceFactory;
import org.jdna.media.impl.CDStackingModel;
import org.jdna.media.impl.DVDMediaItem;
import org.jdna.media.impl.MediaFile;
import org.jdna.media.impl.MediaFolder;
import org.jdna.media.impl.MovieResourceFilter;
import org.jdna.media.impl.URIAdapter;
import org.jdna.media.impl.URIAdapterFactory;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MediaMetadataUtils;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;
import org.jdna.metadataupdater.IMetaDataUpdaterScreen.MovieEntry;

import sagex.api.Global;

/**
 * This is an engine that will process one of more directories and files and
 * attempt to find metadata for each entry. The engine can operate in several
 * modes including automatic, stacked, etc.
 * 
 * @author seans
 * 
 */
@CommandLineProcess(acceptExtraArgs = true, description = "Import/Update Movie MetaData from a MetaData Provider.")
public class MetadataUpdater {
	
	public static final Logger log = Logger.getLogger(MetadataUpdater.class);
	/**
	 * This method only needs to be called from the command line. All other
	 * processes should use the MetadataUpdater directly and NOT call the main()
	 * method.
	 * 
	 * @param args
	 *            command line args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		try {

			String title = "Batch MetaData Tools (" + Version.VERSION + ")";
			System.out.println(title);

			// init the configuration before we process, since some command line vars can 
			// override default configuration settings
			initConfiguration();
			
			// process the command line
			CommandLine cl = new CommandLine(title, "java MetadataTool", args);
			cl.process();

			// apply the command line args to this instance.
			MetadataUpdater mdu = new MetadataUpdater();
			try {
				cl.applyToAnnotated(mdu);

				// check for help
				if (cl.hasArg("help") || !cl.hasArgs()) {
					cl.help(mdu);
					return;
				}
			} catch (Exception e) {
				cl.help(mdu, e);
				return;
			}

			mdu.process();
		} catch (Exception e) {
			log.error("Failed to process Video MetaData!", e);
			System.out.println("Processing Failed, see log for details.");
		}
	}

	private String[] files;
	private boolean recurse = false;
	private boolean auto = true;
	private boolean force = false;
	private int displaySize = 10;
	private boolean stack = true;
	private boolean listMovies = false;
	private boolean listProvders = false;
	private boolean update = false;
	private boolean aggressive = true;
	private boolean offline = false;
	private String provider = IMDBMetaDataProvider.PROVIDER_ID;
	private boolean showInfo = false;
	private boolean showProperties = false;
	private boolean refreshSageTV = false;

	// output device for this process
	private IMetaDataUpdaterScreen screen = null;

	// stateful variables for this process
	private boolean abort = false;

	private List<MovieEntry> allMovies = new ArrayList<MovieEntry>();

	/**
	 * This is the entry into the tool. This will process() all files/dirs that
	 * are passed.
	 * 
	 * @throws Exception
	 *             if processing fails for some unknown reason.
	 */
	public void process() throws Exception {
		if (showProperties) {
			PrintWriter pw = new PrintWriter(System.out);
			ConfigurationManager.getInstance().dumpProperties(pw);
			pw.flush();
			pw.close();
			System.out.flush();
			return;
		}
		
		initScreen();

		log.info("Version: " + Version.VERSION);

		if (listProvders) {
			screen.renderProviders(MediaMetadataFactory.getInstance().getMetaDataProviders(), provider);
			return;
		}

		if (files == null || files.length == 0) {
			if (refreshSageTV) {
				try {
					screen.message("Notifying Sage to Refresh Imported Media");
					Global.RunLibraryImportScan(false);
				} catch (Throwable t) {
					// that didn't work
				}
			} else {
				screen.error("Must Pass at least 1 file or directory!");
				return;
			}
		}

		if (listMovies) {
			screen.message("Listing Movies... This make take some time depending on the size of your collection.");
		}

		MediaFolder mf = null;

		for (String f : files) {
			if (abort)
				return;

			File file = new File(f);
			if (!file.exists() && offline == false) {
				screen.error("File Not Found: " + file.getAbsolutePath() + "\nConsider adding --offline=true if you want to create an offline video.");
			}

			if (file.isFile() || isOfflineEnabled()) {
				if (isOfflineEnabled()) {
					log.debug("Offline Media File Created: " + file.toURI().toString());
				}
				IMediaFile mfNew = new MediaFile(file.toURI());
				mfNew.touch();
				processMediaFile(mfNew);
			} else {
				URIAdapter ua = URIAdapterFactory.getAdapter(file.toURI());
				if (DVDMediaItem.isDVD(ua)) {
					IMediaFile dvdFile = (IMediaFile) MediaResourceFactory.getInstance().createResource(ua);
					processMediaFile(dvdFile);
				} else {
					mf = new MediaFolder(ua);
					processMediaFolder(mf);
				}
			}
		}

		if (listMovies) {
			screen.renderKnownMovies(allMovies);
		} else {
			// render the completion stats
			screen.renderStats();
		}
		
		if (refreshSageTV) {
			try {
				screen.message("Notifying Sage to Refresh Imported Media");
				Global.RunLibraryImportScan(false);
			} catch (Throwable t) {
				// that didn't work
			}
		}

		// exit
		quit();
	}

	private void processMediaFile(IMediaFile mediaFile) {
		try {
			if (abort)
				return;

			// notify that we are processing a file
			screen.notifyProcessingFile(mediaFile);

			log.info("Processing Media File: " + mediaFile.getLocationUri());

			// load existing metadata, and check if it needs updating...
			IMediaMetadata md = mediaFile.getMetadata();

			if (showInfo) {
				screen.showMetadata(mediaFile, md);
			}

			if (listMovies) {
				MovieEntry me = new MovieEntry(mediaFile, md);
				allMovies.add(me);
				return;
			}

			if (force || md == null) {
				fetchMetaData(mediaFile);
			} else if (update) {
				refreshMetaData(mediaFile, md);
			} else {
				log.debug("Nothing to do perhaps pass --force or --update for file: " + mediaFile.getLocationUri());
				screen.nofifySkippedFile(mediaFile);
			}

		} catch (Exception e) {
			log.error("Failed to process media file: " + mediaFile.getLocationUri(), e);
			screen.notifyFailedFile(mediaFile, e);
		}
	}

	private void processMediaFolder(IMediaFolder mf) {
		// apply the stacking model and file to this folder
		mf.setFilter(MovieResourceFilter.INSTANCE);
		if (stack) {
			mf.setStackingModel(CDStackingModel.INSTANCE);
		}

		List<IMediaResource> l = mf.members();

		for (IMediaResource mr : l) {
			if (abort)
				break;

			if (mr.getType() == IMediaFile.TYPE_FILE) {
				processMediaFile((IMediaFile) mr);
			} else if (mr.getType() == IMediaFolder.TYPE_FOLDER) {
				if (recurse) {
					processMediaFolder((IMediaFolder) mr);
				} else {
					log.debug("Skipping Directory (recurse disabled): " + mr.getLocationUri());
				}
			} else {
				log.error("Unknown Media Resourse for: " + mr.getLocationUri() + "; " + mr.getClass().getName());
			}
		}
	}

	private void refreshMetaData(IMediaFile file, IMediaMetadata md) throws Exception {
		log.debug("Refreshing MetaData for: " + file.getLocationUri());
		// if we have a dataProviderUrl and id, then refresh the metadata, or
		// if we only have title, then call the searchMetaData() using the title
		// from the existing metadata
		if (md.getProviderDataUrl() != null && md.getProviderId() != null) {
			IMediaMetadataProvider provider = MediaMetadataFactory.getInstance().getProvider(md.getProviderId());
			if (provider == null) {
				throw new Exception("Provider Not Registered: " + md.getProviderId());
			}

			IMediaMetadata updated = provider.getMetaData(md.getProviderDataUrl());
			exportMetaData(updated, file);
		} else {
			log.debug("Skipping: " + file.getLocationUri() + "; MetaData does not contain providerId or providerDataUrl");
			// TODO: refresh based on metadata title
			screen.nofifySkippedFile(file);
		}
	}

	private void fetchMetaData(IMediaFile file) throws Exception {
		String name = MediaMetadataUtils.cleanSearchCriteria(file.getTitle(), false);
		fetchMetaData(file, name);
	}

	private List<IMediaSearchResult> getSearchResultsForTitle(String name) throws Exception {
		List<IMediaSearchResult> results = MediaMetadataFactory.getInstance().search(provider, IMediaMetadataProvider.SEARCH_TITLE, name);
		log.debug(String.format("Searched for: %s; Good: %s", name, isGoodSearch(results)));
		return results;
	}

	public static boolean isGoodSearch(List<IMediaSearchResult> results) {
		return (results.size() > 0 && (results.get(0).getResultType() == IMediaSearchResult.RESULT_TYPE_POPULAR_MATCH || results.get(0).getResultType() == IMediaSearchResult.RESULT_TYPE_EXACT_MATCH));
	}

	private void fetchMetaData(IMediaFile file, String name) throws Exception {

		List<IMediaSearchResult> results = getSearchResultsForTitle(name);
		if (!isGoodSearch(results)) {
			log.debug("Not very sucessful with the search for: " + name);
			if (aggressive) {
				String oldName = name;
				String newName = MediaMetadataUtils.cleanSearchCriteria(name, true);
				if (!oldName.equals(newName)) {
					log.debug("We'll try again using: " + newName);

				}
				List<IMediaSearchResult> newResults = getSearchResultsForTitle(newName);
				if (isGoodSearch(newResults)) {
					log.debug("Our other search returned better results.. We'll use these.");
					results = newResults;
				}
			}
		}

		if (auto && isGoodSearch(results)) {
			exportMetaData(MediaMetadataFactory.getInstance().getMetaData(results.get(0)), file);
			return;
		}

		// Let's prompt for results
		// draw the screen, and let the input handler decide what to do next
		screen.renderResults("Search Results: " + name, results, displaySize);

		String data = screen.prompt("[q=quit, n=next (default), ##=use result ##, TITLE=Search TITLE]", "n", "search_results");

		if ("q".equalsIgnoreCase(data)) {
			screen.nofifySkippedFile(file);
			quit();
		} else if ("n".equalsIgnoreCase(data)) {
			screen.nofifySkippedFile(file);
		} else if (data.startsWith("s:")) {
			String buf = data.replaceFirst("s:", "");
			fetchMetaData(file, buf);
		} else {
			int n = 0;
			try {
				try {
					n = Integer.parseInt(data);
				} catch (Exception e) {
					log.warn("Debug: Failed to parse: " + data + " as a number, using it again as a search.");
					fetchMetaData(file, data);
					return;
				}
				IMediaSearchResult sr = results.get(n);
				IMediaMetadata md = MediaMetadataFactory.getInstance().getMetaData(sr);
				exportMetaData(md, file);
				screen.notifyManualUpdate(file, md);
			} catch (RuntimeException e) {
				log.error("Failed while fetching metadata for movie: " + name, e);
				screen.error("Failed to get/export Metadata for movie: " + name);
			}
		}
	}

	private void exportMetaData(IMediaMetadata md, IMediaFile mediaFile) throws Exception {
		mediaFile.updateMetadata(md, ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().isOverwriteThumbnails());
		screen.notifyUpdatedFile(mediaFile, md);
	}

	/**
	 * Attempts to load the configuration properties from the following
	 * locations.... -Dmetadataupdater.properties=file or
	 * ./metedataupdater.properties
	 * 
	 * @throws IOException
	 */
	public static void initConfiguration() throws IOException {
		log.debug("Attempting to load metadataupdater from default locations....");
		ConfigurationManager.getInstance();
	}

	private void initScreen() throws Exception {
		String scr = ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().getScreenClass();
		screen = (IMetaDataUpdaterScreen) Class.forName(scr).newInstance();
		log.info("Using Screen Engine: " + scr);
	}

	/**
	 * set the files/dirs to process.
	 * 
	 * @param files
	 */
	@CommandLineArg(name = "EXTRAARGS", description = "Internal Arg that collects files passed on the command line. do not set.")
	public void setFiles(String files[]) {
		this.files = files;
	}

	/**
	 * enable/disable directory recursion
	 * 
	 * @param b
	 */
	@CommandLineArg(name = "recurse", description = "Recursively process sub directories. (default false)")
	public void setRecurse(boolean b) {
		this.recurse = b;
	}

	/**
	 * enable/disable automatic updating. if set to false, then it prompt() for
	 * each movie entry.
	 * 
	 * @param b
	 */
	@CommandLineArg(name = "auto", description = "Automatically update meta data if there are exact matches, 1 match, or popular matches.  Otherwise will skip/prompt. (default true)")
	public void setAutomaticUpdate(boolean b) {
		this.auto = b;
	}

	/**
	 * if set to true, it will ignore exising metadata and fetch new information
	 * even it doesn't need to.
	 * 
	 * @param b
	 */
	@CommandLineArg(name = "force", description = "Force download of metadata, even if metadata exists and has not been updated. Does not overwrite thumbnails. (default false)")
	public void setFetchAlways(boolean b) {
		this.force = b;
	}

	@CommandLineArg(name = "forceThumbnail", description = "Force download/overwrite of thumbnails. (default false)")
	public void setForceThumbnailOverwrite(boolean b) {
		// this is a hack, for now...
		ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().setOverwriteThumbnails(true);
	}

	@CommandLineArg(name = "reindex", description = "Tells Metadata providers that require indexing (ie, DVD Profiler) to rebuild it's index. (default false)")
	public void setReindex(boolean b) {
		ConfigurationManager.getInstance().getDVDProfilerConfiguration().setForceRebuild(true);
		ConfigurationManager.getInstance().getDVDProfilerLocalConfiguration().setForceRebuild(true);
	}

	/**
	 * used by the renderResult() to limit the # of results to show.
	 * 
	 * @param size
	 */
	@CommandLineArg(name = "displaySize", description = "# of search results to display on the screen. (default 10)")
	public void setDisplaySize(String size) {
		this.displaySize = Integer.parseInt(size);
	}

	/**
	 * if true, then a movie entry can consist of multiple parts. Useful where
	 * you have movies split across multiple cd images. if movies are stacked,
	 * then metadata is fetched once, which leads to better efficency.
	 * Persistence engines should account for stacked media and write the
	 * metadata for each part of the stacked entry, as the Sage Persistence
	 * engine does.
	 * 
	 * @param b
	 */
	@CommandLineArg(name = "stack", description = "Stack/Group multi cd movies. (default true)")
	public void setStack(boolean b) {
		this.stack = b;
	}

	/**
	 * Just list the movies that it would find given the setFiles(...)
	 * 
	 * @param b
	 */
	@CommandLineArg(name = "listMovies", description = "Just list the movies it would find. (default false)")
	public void setListMovies(boolean b) {
		this.listMovies = b;
	}

	/**
	 * List the know metadata provides that are installed.
	 * 
	 * @param b
	 */
	@CommandLineArg(name = "listProviders", description = "Just list the installed metadata providers. (default false)")
	public void setListProviders(boolean b) {
		this.listProvders = b;
	}

	/**
	 * if true, then it will attempt to refresh metadata for any entries where
	 * the metadata is newer than the file.
	 * 
	 * @param b
	 */
	@CommandLineArg(name = "update", description = "Update Missing AND Updated Metadata. (default false)")
	public void setUpdate(boolean b) {
		this.update = b;
	}

	/**
	 * enable/disable agressive searching. Agressive searching leads to more
	 * searches because the engine will attempt to seach multiple times to find
	 * the best match.
	 * 
	 * @param b
	 */
	@CommandLineArg(name = "agressive", description = "If the first search doesn't return clean results, then try additional searches. (default true)")
	public void setAggressive(boolean b) {
		this.aggressive = b;
	}

	/**
	 * enable/disable offline video support. Offline videos are empty video
	 * files with metadata and thumbnails. A user would create these if the
	 * video did exist in his/her collection but they are currently offline.
	 * 
	 * @param b
	 */
	@CommandLineArg(name = "offline", description = "if true, then the passed file(s) will be treated as offline files. (default false)")
	public void setOffline(boolean b) {
		this.offline = b;
	}

	/**
	 * Set a new default metadata provider.
	 * 
	 * @param s
	 */
	@CommandLineArg(name = "provider", description = "Set the metadata provider. (default imdb)")
	public void setMetadataProvicer(String s) {
		this.provider = s;
	}

	/**
	 * Show Metadata Information for a given movie
	 * 
	 * @param s
	 */
	@CommandLineArg(name = "metadata", description = "Show metadata for movie. (default false)")
	public void setShowMetadata(boolean b) {
		this.showInfo = b;
	}

	/**
	 * Dump Current Configuration Properties
	 * 
	 * @param s
	 */
	@CommandLineArg(name = "showProperties", description = "Show the current configuration. (default false)")
	public void setShowProperties(boolean b) {
		this.showProperties = b;
	}

	/**
	 * Set a configuration property
	 * 
	 * @param s
	 */
	@CommandLineArg(name = "setProperty", description = "Sets a Property (--setProperty=name:value) (use --showProperties to get property list)")
	public void setPropertu(String propAndVal) {
		Pattern p = Pattern.compile("([^:]+):(.*)");
		Matcher m = p.matcher(propAndVal);
		if (m.find()) {
			String n = m.group(1);
			String v = m.group(2);
			if (n==null || v==null) {
				throw new RuntimeException("Malformed Property: " + propAndVal);
			}
			ConfigurationManager.getInstance().setProperty(n, v);
		} else {
			throw new RuntimeException("Invalid Property: " + propAndVal);
		}
	}

	/**
	 * Notify SageTV to refresh it's media
	 * 
	 * @param s
	 */
	@CommandLineArg(name = "refreshSageTV", description = "Notify SageTV to refresh it's Media Library. (default false)")
	public void setRefreshSageTV(boolean b) {
		this.refreshSageTV = b;
	}

	/**
	 * TODO: Handle quit in a better way.
	 */
	private void quit() {
		abort = true;
	}

	public String[] getFiles() {
		return files;
	}

	public boolean isRecurseEnabled() {
		return recurse;
	}

	public boolean isAutoEnabled() {
		return auto;
	}

	public boolean isForceEnabled() {
		return force;
	}

	public int getDisplaySize() {
		return displaySize;
	}

	public boolean isStackEnabled() {
		return stack;
	}

	public boolean isUpdateEnabled() {
		return update;
	}

	public boolean isAggressiveEnabled() {
		return aggressive;
	}

	public boolean isOfflineEnabled() {
		return offline;
	}

}
