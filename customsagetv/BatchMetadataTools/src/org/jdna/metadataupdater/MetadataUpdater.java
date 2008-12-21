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
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.MediaResourceFactory;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.impl.composite.CompositeMetadataConfiguration;
import org.jdna.media.metadata.impl.composite.CompositeMetadataProvider;
import org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider;
import org.jdna.media.util.AutomaticUpdateMetadataVisitor;
import org.jdna.media.util.CollectorResourceVisitor;
import org.jdna.media.util.CompositeResourceVisitor;
import org.jdna.media.util.CountResourceVisitor;
import org.jdna.media.util.MissingMetadataVisitor;
import org.jdna.media.util.RefreshMetadataVisitor;

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
	private boolean force = false;
	private int displaySize = 10;
	private boolean listMovies = false;
	private boolean listProvders = false;
	private boolean refresh = false;
	private boolean aggressive = true;
	private boolean offline = false;
	private boolean showMetadata = false;
	private boolean showProperties = false;
	private boolean refreshSageTV = false;
	private boolean prompt = true;
	private String provider = IMDBMetaDataProvider.PROVIDER_ID;

	/**
	 * This is the entry into the tool. This will process() all files/dirs that
	 * are passed.
	 * 
	 * @throws Exception
	 *             if processing fails for some unknown reason.
	 */
	public void process() throws Exception {
		// dump our properties
		if (showProperties) {
			PrintWriter pw = new PrintWriter(System.out);
			ConfigurationManager.getInstance().dumpProperties(pw);
			pw.flush();
			pw.close();
			System.out.flush();
			return;
		}
		
		// show the known metadata providers
		if (listProvders) {
			renderProviders(MediaMetadataFactory.getInstance().getMetaDataProviders(), provider);
			return;
		}
		
		// get the parent folder for processing
		IMediaFolder parentFolder = null;
		List<IMediaResource> resources = new ArrayList<IMediaResource>();
		for (String f : files) {
			File file = new File(f);
			if (!file.exists() && offline == false) {
				System.out.println("File Not Found: " + file.getAbsolutePath() + "\nConsider adding --offline=true if you want to create an offline video.");
			} else {
				resources.add(MediaResourceFactory.getInstance().createResource(file.toURI()));
			}
		}
		
		// put all the videos/folders in a virtual top level folder for processing
		if (resources.size()==1 && resources.get(0).getType()==IMediaFolder.TYPE_FOLDER) {
			parentFolder = (IMediaFolder) resources.get(0);
		} else {
			parentFolder = MediaResourceFactory.getInstance().createVirtualFolder("Videos", resources);
		}
		
		// if there are no parent folders to process, the just do nothing...
		if (parentFolder.members().size()==0) {
			System.out.println("No Files to process.");
		} else {
			// check if we are just listing movies
			if (listMovies) {
				parentFolder.accept(new ListMovieVisitor(false), recurse);
				return;
			}
			
			// we are showing info for these...
			if (showMetadata) {
				parentFolder.accept(new ListMovieVisitor(true), recurse);
				return;
			}

			
			// update/refresh existing metadata
			boolean overwriteThumbnails = ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().isOverwriteThumbnails();
			CollectorResourceVisitor handled = new CollectorResourceVisitor();
			CollectorResourceVisitor skipped = new CollectorResourceVisitor();
			if (refresh) {
				parentFolder.accept(new RefreshMetadataVisitor(overwriteThumbnails, handled, skipped));
				return;
			}
			
			// update refresh/missing metadate (unless force is used, and if so, then do all)
			IMediaResourceVisitor updated = new IMediaResourceVisitor() {
				public void visit(IMediaResource resource) {
					System.out.printf("Updated: %s; %s\n", resource.getMetadata().getTitle(), resource.getLocationUri() );
				}
			};
			CollectorResourceVisitor notfound = new CollectorResourceVisitor();
			CountResourceVisitor updatedCount = new CountResourceVisitor();
			CountResourceVisitor skippedCount = new CountResourceVisitor();
			AutomaticUpdateMetadataVisitor autoUpdater = new AutomaticUpdateMetadataVisitor(provider, aggressive, overwriteThumbnails, new CompositeResourceVisitor(updated, updatedCount), notfound);
			IMediaResourceVisitor up2dateVisitor = new IMediaResourceVisitor() {
				public void visit(IMediaResource resource) {
					System.out.println("Skipping: " + resource.getLocationUri());
				}
			};
			if (force) {
				// do all videos... no matter what
				parentFolder.accept(autoUpdater, recurse);
			} else {
				// do only vidoes that are missing metadat
				parentFolder.accept(new MissingMetadataVisitor(autoUpdater, new CompositeResourceVisitor(up2dateVisitor, skippedCount)), recurse);
			}

			// lastly, if the user wants, let's prompt for any that could not be found.
			if (prompt && notfound.getCollection().size()>0) {
				// now process all the files that autoupdater could not process automatically
				IMediaFolder mf = MediaResourceFactory.getInstance().createVirtualFolder("manualUpdate", notfound.getCollection());
				mf.accept(new ManualConsoleSearchMetadataVisitor(provider, aggressive, overwriteThumbnails, updated, skipped), false);
			} else if (notfound.getCollection().size()>0) {
				// dump out the skipped entries that were not updated
				System.out.println("\nThe Following Media Entries could not be updated.");
				for (IMediaResource r : notfound.getCollection()) {
					System.out.println(r.getLocationUri());
				}
			}
			
			// Render stats
			System.out.println("\n\nMetaData Stats...");
			System.out.printf("Auto Update: %d; Auto Skip: %d; Require Attention:%d; Manual Skipped: %d; \n\n", updatedCount.getCount(), skippedCount.getCount(), notfound.getCollection().size(), skipped.getCollection().size());
		}

		// check if we need to refresh sage tv
		if (refreshSageTV) {
			try {
				System.out.println("Notifying Sage to Refresh Imported Media");
				Global.RunLibraryImportScan(false);
			} catch (Throwable t) {
			}
		}
	}

	
	public void renderProviders(List<IMediaMetadataProvider> providers, String defaultProvider) {
		System.out.println("\n\nInstalled Metadata Providers (*=default");
		for (IMediaMetadataProvider p : providers) {
			System.out.printf("%1s %-20s %s\n", (p.getInfo().getId().equals(defaultProvider)?"*":""), p.getInfo().getId(), p.getInfo().getName());
		}
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
		
		List<CompositeMetadataConfiguration> otherProviders = ConfigurationManager.getInstance().getCompositeMetadataConfiguration();
		for (CompositeMetadataConfiguration c : otherProviders) {
			CompositeMetadataProvider p = new CompositeMetadataProvider(c);
			MediaMetadataFactory.getInstance().addMetaDataProvider(p);
		}
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
	 * if true, then it will attempt to refresh metadata for entries that already have metadata by using the providerDataUrl for the media file.
	 * 
	 * @param b
	 */
	@CommandLineArg(name = "update", description = "Update Missing AND Updated Metadata. (default false)")
	public void setUpdate(boolean b) {
		this.refresh = b;
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
		this.showMetadata = b;
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
	 * Dump Current Configuration Properties
	 * 
	 * @param s
	 */
	@CommandLineArg(name = "prompt", description = "When a media file cannot be updated, then prompt to enter a title to search on. (default true)")
	public void setPrompt(boolean b) {
		this.prompt = b;
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

	public String[] getFiles() {
		return files;
	}
}
