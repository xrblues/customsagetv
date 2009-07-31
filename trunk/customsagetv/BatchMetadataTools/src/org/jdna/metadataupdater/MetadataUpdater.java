package org.jdna.metadataupdater;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jdna.cmdline.CommandLine;
import org.jdna.cmdline.CommandLineArg;
import org.jdna.cmdline.CommandLineProcess;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.FileMediaFolder;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.VirtualMediaFolder;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.impl.sage.CentralFanartPersistence;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesPersistence;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesWithCentralFanartPersistence;
import org.jdna.media.util.AutomaticUpdateMetadataVisitor;
import org.jdna.media.util.MissingMetadataVisitor;
import org.jdna.media.util.RefreshMetadataVisitor;
import org.jdna.util.LoggerConfiguration;
import org.jdna.util.ProgressTracker;
import org.jdna.util.ProgressTracker.FailedItem;

import sagex.SageAPI;
import sagex.api.Global;
import sagex.api.WidgetAPI;
import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.IConfigurationElement;
import sagex.phoenix.configuration.IConfigurationMetadataVisitor;
import sagex.phoenix.configuration.proxy.GroupProxy;

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
    public static final Logger APPLOG = Logger.getLogger(MetadataUpdater.class.getName() + ".APPLOG");
    private static final Logger log = Logger.getLogger(MetadataUpdater.class);

    private MetadataUpdaterConfiguration config = null;
    private MetadataConfiguration metadataConfig = null;
    private BMTSageAPIProvider proxyAPI = null;

    private String[] files;
    private boolean  listMovies     = false;
    private boolean  listProvders   = false;
    private boolean  offline        = false;
    private boolean  showMetadata   = false;
    private boolean  showProperties = false;
    private boolean  prompt         = true;
    private boolean showSupportedMetadata = false;

    private PersistenceOptions options = null;
    private IMediaMetadataPersistence persistence = null;

    private boolean tvSearch;
    private String reportType;
    private boolean touch = false;

    
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
        new MetadataUpdater().run(args);
    }

    public void run(String args[]) throws Exception {
        LoggerConfiguration.configure();
        upgrade();

        // set the SageAPI provider to be ourself, so that we can 
        // intercept all Sage Commands
        proxyAPI = new BMTSageAPIProvider();
        SageAPI.setProvider(proxyAPI);
        
        // init config objects
        config = GroupProxy.get(MetadataUpdaterConfiguration.class);
        metadataConfig = GroupProxy.get(MetadataConfiguration.class);
        options = new PersistenceOptions();
        persistence = new SageTVPropertiesWithCentralFanartPersistence();
        
        // add bmt metadata configuration
        //Phoenix.getInstance().getConfigurationMetadataManager().addMetadata(new Direcot);
        
        try {
            String title = "Batch MetaData Tools (" + Version.VERSION + ")";
            System.out.println(title);

            logMetadataEnvironment();

            // process the command line
            CommandLine cl = new CommandLine(title, "java MetadataTool", args);
            cl.process();

            // apply the command line args to this instance.
            try {
                cl.applyToAnnotated(this);

                // check for help
                if (cl.hasArg("help") || args==null || args.length==0) {
                    cl.help(this);
                    return;
                }
            } catch (Exception e) {
                cl.help(this, e);
                return;
            }

            this.process();
            
        } catch (Exception e) {
            log.error("Failed to process Video MetaData!", e);
            System.out.println("Processing Failed, see log for details.");
        }
    }

    public void logMetadataEnvironment() {
        log.debug("========= BEGIN BATCH METADATA TOOLS ENVIRONMENT ==============");
        log.debug("   BMT Version:  " + Version.VERSION);
        log.debug(" Sagex Version:  " + sagex.api.Version.GetVersion());
        log.debug("  Java Version:  " + System.getProperty("java.version"));
        log.debug("Java Classpath:  " + System.getProperty("java.class.path"));
        
        int removed=0;
        String metadataPattern = "metadata-updater-([a-zA-Z0-9-_\\.]+).jar";
        String sagexPattern = "sagex.api-([a-zA-Z0-9-_\\.]+).jar";
        removed += cleanJars(new File("JARs"), metadataPattern);
        removed += cleanJars(new File("libs"), metadataPattern);
        removed += cleanJars(new File("JARs"), sagexPattern);
        removed += cleanJars(new File("libs"), sagexPattern);
        if (removed>0) {
            System.out.println("System is being shutdown so that old jar libraries can be removed.");
            System.exit(1);
        }
        log.debug("========= END BATCH METADATA TOOLS ENVIRONMENT ==============");
    }
    
    public static int cleanJars(File libDir, String pattern) {
        int removed = 0;
        Pattern p = Pattern.compile(pattern);
        if (libDir.exists()) {
            for (File f : libDir.listFiles()) {
                Matcher m = p.matcher(f.getName());
                if (m.find()) {
                    System.out.println("Removing Jar: " + f.getName());
                    if (!f.delete()) f.deleteOnExit();
                    removed++;
                }
            }
        }
        return removed;
    }

    /**
     * This is the entry into the tool. This will process() all files/dirs that
     * are passed.
     * 
     * @throws Exception
     *             if processing fails for some unknown reason.
     */
    public void process() throws Exception {
        String provider = metadataConfig.getDefaultProviderId();
        log.debug("Using Providers: " + provider);
        
        if (!config.isAutomaticUpdate()) {
            System.out.println("** Automatic Updating Disabled ***");
        }
        
        // dump our properties
        if (showProperties) {
            PrintWriter pw = new PrintWriter(System.out);
            Phoenix.getInstance().getConfigurationMetadataManager().getMetadata().visit(new IConfigurationMetadataVisitor() {
                public void accept(IConfigurationElement el) {
                    if (el.getElementType() == IConfigurationElement.GROUP) {
                        System.out.println("# -- Group: " + el.getLabel());
                    }
                    if (el.getElementType() == IConfigurationElement.FIELD) {
                        System.out.printf("# %s\n", (el.getDescription()==null?el.getLabel():el.getDescription()));
                        System.out.printf("%s=%s\n\n", el.getId(), phoenix.api.GetProperty(el.getId()));
                    }
                }
            });
            pw.flush();
            pw.close();
            System.out.flush();
            return;
        }
        
        if (showSupportedMetadata) {
            System.out.println("Supported Metadata Fields");
            MetadataKey values[] = Arrays.copyOf(MetadataKey.values(), MetadataKey.values().length);
            Arrays.sort(values, new Comparator<MetadataKey>() {
                public int compare(MetadataKey o1, MetadataKey o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });
            for (MetadataKey k : values) {
                System.out.printf("%20s : %s\n", k.getId(), k.getDescription());
            }
            return;
        }

        // show the known metadata providers
        if (listProvders) {
            renderProviders(MediaMetadataFactory.getInstance().getMetaDataProviders(), provider);
            return;
        }
        
        if (reportType!=null) {
            System.out.println("Requesting a Fanart report: " + reportType + " on the SageTV Server.");
            try {
                String result = (String) WidgetAPI.EvaluateExpression("phoenix_api_RunPhoenixDiagnostics(\""+ reportType +"\")");
                if (result==null) {
                    throw new Exception("Failed to execute the report on the server.");
                }
                System.out.println(result);
            } catch (Throwable e) {
                System.out.println("Report Failed.  Most likely cause is that the sagex.api Remote APIs are not installed on the server.");
            }
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
                resources.add(FileMediaFolder.createResource(file));
            }
        }

        // put all the videos/folders in a virtual top level folder for
        // processing
        if (resources.size() == 1 && resources.get(0).getType() == IMediaResource.Type.Folder) {
            parentFolder = (IMediaFolder) resources.get(0);
        } else {
            parentFolder = new VirtualMediaFolder("bmt:/root/videos");
            ((VirtualMediaFolder)parentFolder).addResources(resources);
        }

        // if there are no parent folders to process, the just do nothing...
        if (parentFolder.members().size() == 0) {
            System.out.println("No Files to process.");
        } else {
            // check if we are just listing movies
            if (listMovies) {
                parentFolder.accept(new ListMovieVisitor(persistence, false), config.isRecurseFolders());
                return;
            }

            // we are showing info for these...
            if (showMetadata) {
                parentFolder.accept(new ListMovieVisitor(persistence, true), config.isRecurseFolders());
                return;
            }
            
            // we are just touching files...
            if (touch) {
                parentFolder.touch();
                System.out.println("Touched File(s)");
                return;
            }
            
            // collectors and counters for automatic updating
            if (!config.isProcessMissingMetadataOnly()) {
                parentFolder.accept(new RefreshMetadataVisitor(persistence, options, new ProgressTracker<IMediaFile>()));
                return;
            }

            SearchQuery.Type searchType=null;
            if (isTVSearch()) {
                log.debug("Forcing Search TV Search");
                searchType=SearchQuery.Type.TV;
            }
            
            ProgressTracker<IMediaFile> automaticProgress = new ConsoleProgressTracker(persistence);
            ProgressTracker<IMediaFile> manualProgress = new ConsoleProgressTracker(persistence);
            
            // Main visitor for automatic updating
            AutomaticUpdateMetadataVisitor autoUpdater = new AutomaticUpdateMetadataVisitor(provider, persistence, options, searchType, automaticProgress);
            
            // Main visitor for manual interactive searching
            ManualConsoleSearchMetadataVisitor manualUpdater = new ManualConsoleSearchMetadataVisitor(this, provider, persistence, options, searchType, manualProgress, config.getSearchResultDisplaySize());

            // Main visitor that only does files that a missing metadata
            MissingMetadataVisitor missingMetadata = new MissingMetadataVisitor(persistence, config.isAutomaticUpdate() ? autoUpdater : manualUpdater);
            
            if (options.isOverwriteFanart() || options.isOverwriteMetadata()) {
                // do all videos... no matter what
                parentFolder.accept(config.isAutomaticUpdate() ? autoUpdater : manualUpdater, config.isRecurseFolders());
            } else {
                // do only vidoes that are missing metadat
                parentFolder.accept(missingMetadata, config.isRecurseFolders());
            }

            // lastly, if the user wants, let's prompt for any that could not be
            // found.
            if (config.isAutomaticUpdate() && prompt && automaticProgress.getFailedItems().size() > 0) {
                // now process all the files that autoupdater could not process
                // automatically
                VirtualMediaFolder mf = new VirtualMediaFolder("bmt://actions/manualUpdate");
                for (FailedItem<IMediaFile> f : automaticProgress.getFailedItems()) {
                    mf.addMember(f.getItem());
                }
                mf.accept(manualUpdater, false);
            } else if (automaticProgress.getFailedItems().size() > 0) {
                // dump out the skipped entries that were not automatically updated
                System.out.println("\nThe Following Media Entries could not be updated.");
                for (FailedItem<IMediaFile> f : automaticProgress.getFailedItems()) {
                    System.out.printf("Failed: %s; Message: %s\n", f.getItem().getLocation(), f.getMessage());
                }
            }

            if (config.isRememberSelectedSearches()) {
                try {
                    ConfigurationManager.getInstance().saveTitleMappings();
                } catch (IOException e) {
                    log.error("Failed to store/create the title mappings file!", e);
                }
            } else {
                log.warn("Manual Searched will not be saved, since /metadataUpdater/rememberSelectedSearches=false");
            }
            
            Phoenix.getInstance().getConfigurationManager().save();
            
            // Render stats
            System.out.println("\n\nMetaData Stats...");
            System.out.printf("Auto Updated: %d; Auto Failed: %d; Manual Updated:%d; Manual Skipped: %d; \n\n", automaticProgress.getSuccessfulItems().size(), automaticProgress.getFailedItems().size(), manualProgress.getSuccessfulItems().size(), manualProgress.getFailedItems().size());
        }

        // check if we need to refresh sage tv
        if (config.isRefreshSageTV()) {
            try {
                System.out.println("Notifying Sage to Refresh Imported Media");
                proxyAPI.enableRemoteAPI(true);
                Global.RunLibraryImportScan(false);
            } catch (Throwable t) {
                log.error("Failed while calling RunLibraryImportScan()", t);
            }
        }
    }

    public void renderProviders(List<IMediaMetadataProvider> providers, String defaultProvider) {
        System.out.println("\n\nInstalled Metadata Providers (*=default");
        for (IMediaMetadataProvider p : providers) {
            System.out.printf("%1s %-20s\n", (p.getInfo().getId().equals(defaultProvider) ? "*" : ""), p.getInfo().getId());
            System.out.println("   - " +  p.getInfo().getName());
            System.out.println("   - " + p.getInfo().getDescription() + "\n");
        }
    }

    public void upgrade() {
        deleteFile(new File("scrapers/xbmc/video/tvcom.xml"));
        deleteFile(new File("scrapers/xbmc/video/tvcom.png"));
        deleteFile(new File("scrapers/xbmc/video/tvdb.xml"));
        deleteFile(new File("scrapers/xbmc/video/tvdb.png"));
    }
    
    private void deleteFile(File f) {
        if (f.exists()) {
            log.debug("Deleted File: " + f.getAbsolutePath() + "; Deleted: " + f.delete());
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
        config.setRecurseFolders(b);
    }

    /**
     * if set to true, it will ignore exising metadata and fetch new information
     * even it doesn't need to.
     * 
     * @param b
     */
    @CommandLineArg(name = "overwrite", description = "Overwrite metadata and images. (default false)")
    public void setOverwrite(boolean b) {
        options.setOverwriteFanart(b);
        options.setOverwriteMetadata(b);
    }

    /**
     * if set to true, will overwrite metadata
     * 
     * @param b
     */
    @CommandLineArg(name = "overwriteMetadata", description = "Overwrite metadata. (default false)")
    public void setOverwriteMetadata(boolean b) {
        options.setOverwriteMetadata(b);
    }

    /**
     * if set to true, it will overwrite fanart
     * 
     * @param b
     */
    @CommandLineArg(name = "overwriteFanart", description = "Overwrite fanart. (default false)")
    public void setOverwriteFanart(boolean b) {
        options.setOverwriteFanart(b);
    }

    @CommandLineArg(name = "fanartEnabled", description = "Enable fanart support (backgrounds, banners, extra posters, etc). (default true)")
    public void setEnableFanart(boolean b) {
        config.setFanartEnabled(b);
    }

    @CommandLineArg(name = "fanartFolder", description = "Central Fanart Folder location")
    public void setForceThumbnailOverwrite(String folder) {
        config.setFanartEnabled(true);
        config.setCentralFanartFolder(folder);
        
        log.debug("Central Fanart Enabled; Using Folder: " + config.getFanartCentralFolder());
    }

    /**
     * used by the renderResult() to limit the # of results to show.
     * 
     * @param size
     */
    @CommandLineArg(name = "displaySize", description = "# of search results to display on the screen. (default 10)")
    public void setDisplaySize(String size) {
        config.setSearchResultDisplaySize(Integer.parseInt(size));
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
     * if true, then it will attempt to refresh metadata for entries that
     * already have metadata by using the providerDataUrl for the media file.
     * 
     * @param b
     */
    @CommandLineArg(name = "update", description = "Update Missing AND Updated Metadata. (default false)")
    public void setUpdate(boolean b) {
        config.setProcessMissingMetadataOnly(!b);
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
     * enable/disable offline video support. Offline videos are empty video
     * files with metadata and thumbnails. A user would create these if the
     * video did exist in his/her collection but they are currently offline.
     * 
     * @param b
     */
    @CommandLineArg(name = "touch", description = "Touch (update timestamp) on a given file/folder")
    public void setTouch(boolean b) {
        this.touch  = b;
    }

    /**
     * Set a new default metadata provider.
     * 
     * @param s
     */
    @CommandLineArg(name = "provider", description = "Set the metadata provider. (default imdb)")
    public void setMetadataProvider(String s) {
        log.info("Setting the default provider: " + s);
        metadataConfig.setDefaultProviderId(s);
        log.debug("Default Provider Set: " + metadataConfig.getDefaultProviderId());
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
     * Show Metadata Properties supported by this application.
     * 
     * @param 
     */
    @CommandLineArg(name = "showSupportedMetadata", description = "Show the supported metadata properties. (default false)")
    public void setShowMetadataProperties(boolean b) {
        this.showSupportedMetadata = b;
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
     * Force each item to be manually selected and updated.
     * 
     * @param s
     */
    @CommandLineArg(name = "auto", description = "Automatically choose best search result. [if false, it will force you to choose for each item] (default true)")
    public void setAutomaticUpdate(boolean b) {
        config.setAutomaticUpdate(b);
    }

    @CommandLineArg(name = "fanartOnly", description = "Only process fanart, ignore updating metadata (default false)")
    public void setFanartOnly(boolean b) {
        persistence=new CentralFanartPersistence();
        options.setOverwriteMetadata(false);
    }

    @CommandLineArg(name = "metadataOnly", description = "Only process metadata, ignore updating fanart (default false)")
    public void setMetadataOnly(boolean b) {
        persistence=new SageTVPropertiesPersistence();
        options.setOverwriteFanart(false);
    }
    
    @CommandLineArg(name = "setProperty", description = "Sets a Property (--setProperty=name:value) (use --showProperties to get property list)")
    public void setPropertu(String propAndVal) {
        Pattern p = Pattern.compile("([^:]+):(.*)");
        Matcher m = p.matcher(propAndVal);
        if (m.find()) {
            String n = m.group(1);
            String v = m.group(2);
            if (n == null || v == null) {
                throw new RuntimeException("Malformed Property: " + propAndVal);
            }
            Phoenix.getInstance().getConfigurationManager().setClientProperty(n, v);
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
        config.setRefreshSageTV(b);
    }

    /**
     * Force the SearchFactory to create TV search
     * 
     * @param s
     */
    @CommandLineArg(name = "tv", description = "Force a TV Search (default false)")
    public void setTVSearch(boolean b) {
        this.tvSearch = b;
    }

    /**
     * perform the phoenix tests on the sage server
     * 
     * @param s
     */
    @CommandLineArg(name = "report", description = "Perform a 'quick' or 'detailed' report on fanart.")
    public void setPerformTests(String reportType) {
        this.reportType = reportType;
    }

    /**
     * Enable SageTV remote configuration 
     * 
     * @param s
     */
    @CommandLineArg(name = "remote", description = "Enable sagex Remote APIs for configuration.")
    public void setPerformTests(boolean remote) {
        proxyAPI.enableRemoteAPI(remote);
    }
    
    public boolean isTVSearch() {
        return tvSearch;
    }

    public String[] getFiles() {
        return files;
    }

    public void exit(String string) {
        System.out.println(string);
        try {
            if (config.isRememberSelectedSearches()) {
                ConfigurationManager.getInstance().saveTitleMappings();
            }
            
            Phoenix.getInstance().getConfigurationManager().save();
        } catch (Exception e) {
            log.error("Failed to save configuration before exit.", e);
        }
        System.exit(1);
    }

    public IMediaMetadataPersistence getPersistence() {
        return persistence;
    }

    public PersistenceOptions getPersistenceOptions() {
        return options;
    }
}
