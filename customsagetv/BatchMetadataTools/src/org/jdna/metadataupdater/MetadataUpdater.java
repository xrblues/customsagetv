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
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.MediaResourceFactory;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.impl.composite.CompositeMetadataConfiguration;
import org.jdna.media.metadata.impl.composite.CompositeMetadataProvider;
import org.jdna.media.metadata.impl.sage.CentralFanartPersistence;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesPersistence;
import org.jdna.media.metadata.impl.sage.SageTVPropertiesWithCentralFanartPersistence;
import org.jdna.media.util.AutomaticUpdateMetadataVisitor;
import org.jdna.media.util.CollectorResourceVisitor;
import org.jdna.media.util.CompositeResourceVisitor;
import org.jdna.media.util.CountResourceVisitor;
import org.jdna.media.util.MissingMetadataVisitor;
import org.jdna.media.util.RefreshMetadataVisitor;
import org.jdna.metadataupdater.gui.BatchMetadataToolsGUI;
import org.jdna.util.LoggerConfiguration;

import sagex.SageAPI;
import sagex.api.Global;
import sagex.api.WidgetAPI;

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
    

    private static MetadataUpdaterConfiguration config = null;
    
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
        LoggerConfiguration.configure();
        
        try {

            String title = "Batch MetaData Tools (" + Version.VERSION + ")";
            System.out.println(title);

            // init the configuration before we process, since some command line
            // vars can
            // override default configuration settings
            initConfiguration();
            
            logMetadataEnvironment();

            // process the command line
            CommandLine cl = new CommandLine(title, "java MetadataTool", args);
            cl.process();

            // apply the command line args to this instance.
            MetadataUpdater mdu = new MetadataUpdater();
            try {
                cl.applyToAnnotated(mdu);

                // check for help
                if (cl.hasArg("help") || args==null || args.length==0) {
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

    public static void logMetadataEnvironment() {
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

    private String[] files;
    private boolean  listMovies     = false;
    private boolean  listProvders   = false;
    private boolean  offline        = false;
    private boolean  showMetadata   = false;
    private boolean  showProperties = false;
    private boolean  prompt         = true;
    private boolean showSupportedMetadata = false;
    private boolean gui = false;

    private PersistenceOptions options = new PersistenceOptions();
    private IMediaMetadataPersistence persistence = new SageTVPropertiesWithCentralFanartPersistence();

    private boolean tvSearch;
    private String reportType;
    
    /**
     * This is the entry into the tool. This will process() all files/dirs that
     * are passed.
     * 
     * @throws Exception
     *             if processing fails for some unknown reason.
     */
    public void process() throws Exception {
        String provider = ConfigurationManager.getInstance().getMetadataConfiguration().getDefaultProviderId();
        log.debug("Using Providers: " + provider);
        
        if (!config.isAutomaticUpdate()) {
            System.out.println("** Automatic Updating Disabled ***");
        }
        
        // dump our properties
        if (showProperties) {
            PrintWriter pw = new PrintWriter(System.out);
            ConfigurationManager.getInstance().dumpProperties(pw);
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
                resources.add(MediaResourceFactory.getInstance().createResource(file.toURI()));
            }
        }

        // put all the videos/folders in a virtual top level folder for
        // processing
        if (resources.size() == 1 && resources.get(0).getType() == IMediaFolder.TYPE_FOLDER) {
            parentFolder = (IMediaFolder) resources.get(0);
        } else {
            parentFolder = MediaResourceFactory.getInstance().createVirtualFolder("Videos", resources);
        }

        if (gui==true) {
           BatchMetadataToolsGUI.runApp(this); 
           return; 
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
            
            // collectors and counters for automatic updating
            CollectorResourceVisitor autoHandled = new CollectorResourceVisitor();
            CollectorResourceVisitor autoSkipped = new CollectorResourceVisitor();
            if (!config.isProcessMissingMetadataOnly()) {
                parentFolder.accept(new RefreshMetadataVisitor(persistence, options, autoHandled, autoSkipped));
                return;
            }

            IMediaResourceVisitor updatedDisplay = new IMediaResourceVisitor() {
                public void visit(IMediaResource resource) {
                    IMediaMetadata md = persistence.loadMetaData(resource);
                    String title = resource.getTitle();
                    if (md!=null) {
                        title=md.getMediaTitle();
                    }
                    System.out.printf("Updated: %s; %s\n", title, resource.getLocationUri());
                    
                    // touch the resource, so that Sage will reload.
                    resource.touch();
                }
            };

            IMediaResourceVisitor up2dateDisplay = new IMediaResourceVisitor() {
                public void visit(IMediaResource resource) {
                    System.out.println("Skipping: " + resource.getLocationUri());
                }
            };
            
            CollectorResourceVisitor autoNotFound = new CollectorResourceVisitor();
            CountResourceVisitor autoUpdatedCount = new CountResourceVisitor();
            CountResourceVisitor autoSkippedCount = new CountResourceVisitor();
            CompositeResourceVisitor autoUpdated  = new CompositeResourceVisitor(updatedDisplay, autoUpdatedCount);
            
            SearchQuery.Type searchType=null;
            if (isTVSearch()) {
                log.debug("Forcing Search TV Search");
                searchType=SearchQuery.Type.TV;
            }
            
            // Main visitor for automatic updating
            AutomaticUpdateMetadataVisitor autoUpdater = new AutomaticUpdateMetadataVisitor(provider, persistence, options, searchType, autoUpdated, autoNotFound);
            
            // collectors and counters for manual updating
            CountResourceVisitor manualUpdatedCount = new CountResourceVisitor();
            CountResourceVisitor manualSkippedCount = new CountResourceVisitor();
            CompositeResourceVisitor manualUpdated  = new CompositeResourceVisitor(updatedDisplay, manualUpdatedCount);
            
            // Main visitor for manual interactive searching
            ManualConsoleSearchMetadataVisitor manualUpdater = new ManualConsoleSearchMetadataVisitor(provider, persistence, options, searchType, manualUpdated, manualSkippedCount, config.getSearchResultDisplaySize());

            // Main visitor that only does files that a missing metadata
            MissingMetadataVisitor missingMetadata = new MissingMetadataVisitor(persistence, config.isAutomaticUpdate() ? autoUpdater : manualUpdater, new CompositeResourceVisitor(up2dateDisplay, config.isAutomaticUpdate() ? autoSkippedCount : manualSkippedCount));
            
            if (options.isOverwriteFanart() || options.isOverwriteMetadata()) {
                // do all videos... no matter what
                parentFolder.accept(config.isAutomaticUpdate() ? autoUpdater : manualUpdater, config.isRecurseFolders());
            } else {
                // do only vidoes that are missing metadat
                parentFolder.accept(missingMetadata, config.isRecurseFolders());
            }

            // lastly, if the user wants, let's prompt for any that could not be
            // found.
            if (config.isAutomaticUpdate() && prompt && autoNotFound.getCollection().size() > 0) {
                // now process all the files that autoupdater could not process
                // automatically
                IMediaFolder mf = MediaResourceFactory.getInstance().createVirtualFolder("manualUpdate", autoNotFound.getCollection());
                mf.accept(manualUpdater, false);
            } else if (autoNotFound.getCollection().size() > 0) {
                // dump out the skipped entries that were not automatically updated
                System.out.println("\nThe Following Media Entries could not be updated.");
                for (IMediaResource r : autoNotFound.getCollection()) {
                    System.out.println(r.getLocationUri());
                }
            }

            if (config.isRememberSelectedSearches()) {
                ConfigurationManager.getInstance().saveTitleMappings();
            }
            
            ConfigurationManager.getInstance().save();
            
            // Render stats
            System.out.println("\n\nMetaData Stats...");
            System.out.printf("Auto Updated: %d; Auto Skipped: %d; Manual Updated:%d; Manual Skipped: %d; \n\n", autoUpdatedCount.getCount(), autoSkippedCount.getCount(), manualUpdatedCount.getCount(), manualSkippedCount.getCount());
        }

        // check if we need to refresh sage tv
        if (config.isRefreshSageTV()) {
            try {
                System.out.println("Notifying Sage to Refresh Imported Media");
                SageAPI.setProvider(SageAPI.getRemoteProvider());
                Global.RunLibraryImportScan(false);
            } catch (Throwable t) {
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
        
        config = ConfigurationManager.getInstance().getMetadataUpdaterConfiguration();
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
     * Set a new default metadata provider.
     * 
     * @param s
     */
    @CommandLineArg(name = "provider", description = "Set the metadata provider. (default imdb)")
    public void setMetadataProvicer(String s) {
        log.info("Setting the default provider: " + s);
        ConfigurationManager.getInstance().getMetadataConfiguration().setDefaultProviderId(s);
        log.debug("Default Provider Set: " + ConfigurationManager.getInstance().getMetadataConfiguration().getDefaultProviderId());
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
        config.setRefreshSageTV(b);
    }

    /**
     * Notify SageTV to refresh it's media
     * 
     * @param s
     */
    @CommandLineArg(name = "gui", description = "Enable/Disable gui (default true)")
    public void setEnableGUI(boolean b) {
        this.gui = b;
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
    
    public boolean isTVSearch() {
        return tvSearch;
    }

    public String[] getFiles() {
        return files;
    }

    public static void exit(String string) {
        System.out.println(string);
        try {
            if (ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().isRememberSelectedSearches()) {
                ConfigurationManager.getInstance().saveTitleMappings();
            }
            
            ConfigurationManager.getInstance().save();
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
