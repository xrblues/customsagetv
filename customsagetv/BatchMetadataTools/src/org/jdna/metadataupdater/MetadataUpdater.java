package org.jdna.metadataupdater;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jdna.cmdline.CommandLine;
import org.jdna.cmdline.CommandLineArg;
import org.jdna.cmdline.CommandLineProcess;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MediaMetadataPersistence;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.MetadataKey;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.media.metadata.impl.xbmc.XbmcScraper;
import org.jdna.media.metadata.impl.xbmc.XbmcScraperParser;
import org.jdna.media.metadata.impl.xbmc.XbmcScraperProcessor;
import org.jdna.media.metadata.impl.xbmc.XbmcUrl;
import org.jdna.process.InteractiveMetadataProcessor;
import org.jdna.process.MetadataItem;
import org.jdna.process.MetadataProcessor;
import org.jdna.util.LoggerConfiguration;

import sagex.SageAPI;
import sagex.api.Global;
import sagex.api.WidgetAPI;
import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.IConfigurationElement;
import sagex.phoenix.configuration.IConfigurationMetadataVisitor;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.fanart.MediaType;
import sagex.phoenix.progress.ProgressTracker;
import sagex.phoenix.progress.TrackedItem;
import sagex.phoenix.vfs.IMediaFolder;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.MediaFolderTraversal;
import sagex.phoenix.vfs.MediaResourceType;
import sagex.phoenix.vfs.VirtualMediaFolder;
import sagex.phoenix.vfs.filters.AndResourceFilter;
import sagex.phoenix.vfs.filters.MediaTypeFilter;
import sagex.phoenix.vfs.impl.FileResourceFactory;
import sagex.phoenix.vfs.util.PathUtils;

/**
 * This is an engine that will process one of more directories and files and
 * attempt to find metadata for each entry. The engine can operate in several
 * modes including automatic, stacked, etc.
 * 
 * @author seans
 * 
 */
@CommandLineProcess(acceptExtraArgs = true, description = "Import/Update TV/Movie information from an external source such as imdb or thetvdb.")
public class MetadataUpdater {
    public static final Logger APPLOG = Logger.getLogger(MetadataUpdater.class.getName() + ".APPLOG");
    private static final Logger log = Logger.getLogger(MetadataUpdater.class);

    private MetadataConfiguration metadataConfig = null;
    private BMTSageAPIProvider proxyAPI = null;

    private String[] files;
    private boolean  listMedia     = false;
    private boolean  listProvders   = false;
    private boolean  offline        = false;
    private boolean  showMetadata   = false;
    private boolean  showProperties = false;
    private boolean  dontUpdate = false;
    private boolean  prompt         = true;
    private boolean showSupportedMetadata = false;

    private PersistenceOptions options = null;
    
    private MediaType searchType = null;
    private String tvProviders = null;
    private String movieProviders = null;
    private String musicProviders = null;
    
    private String reportType;
    private boolean automaticUpdate = true;
    private boolean recurse = true;
    private int displaySize = 10;
    private boolean updateMetadata = false;
    private boolean refreshSageTV = false;
    private boolean rememberSelection = true;

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
        metadataConfig = GroupProxy.get(MetadataConfiguration.class);
        options = new PersistenceOptions();
        
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
        if (!automaticUpdate) {
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

        // show the supported metadata fields
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
            renderProviders(MediaMetadataFactory.getInstance().getMetaDataProviders());
            return;
        }
        
        // do reports
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
                if (!file.exists()) {
                    file.createNewFile();
                }
                IMediaResource r = FileResourceFactory.createResource(null, file);
                resources.add(r);
            }
        }

        // put all the videos/folders in a virtual top level folder for
        // processing
        if (resources.size() == 1 && (resources.get(0) instanceof IMediaFolder)) {
            parentFolder = (IMediaFolder) resources.get(0);
        } else {
            parentFolder = new VirtualMediaFolder(null, "BMT Videos");
            for (IMediaResource r : resources) {
                ((VirtualMediaFolder)parentFolder).addMediaResource(r);
            }
        }

        // if there are no parent folders to process, the just do nothing...
        if (parentFolder.getChildren().size() == 0) {
            System.out.println("No Files to process.");
        } else {
            // check if we are just listing movies
            if (listMedia) {
                MediaFolderTraversal.walk(parentFolder, recurse, new ListMovieVisitor(false));
                return;
            }

            // we are showing info for these...
            if (showMetadata) {
                MediaFolderTraversal.walk(parentFolder, recurse, new ListMovieVisitor(true));
                return;
            }
            
            // Now we are doing the real work....
            // setup the providers
            Map<MediaType, IMediaMetadataProvider> providers = new HashMap<MediaType, IMediaMetadataProvider>();
            String tv = metadataConfig.getTVProviders();
            tv = (tvProviders==null) ? tv : tvProviders;
            providers.put(MediaType.TV, MediaMetadataFactory.getInstance().getProvider(tv, MediaType.TV));

            String movie = metadataConfig.getMovieProviders();
            movie = (movieProviders==null) ? movie : movieProviders;
            providers.put(MediaType.MOVIE, MediaMetadataFactory.getInstance().getProvider(movie, MediaType.MOVIE));
            
            //String music = metadataConfig.getMusicProviders();
            //Phoenix.getInstance().getEventBus().addHandler(
            //        ScanMediaFileEvent.class.getName(), 
            //        new AutomaticScanMediaFileEventHandler(music, MediaType.MUSIC));

            // set the persistence engine...
            IMediaMetadataPersistence persistence = null;
            if (dontUpdate) {
                persistence = new STDOUTPersistence();
            } else {
                persistence = new MediaMetadataPersistence();
            }
            
            // set some basic options for commandline use
            options.setImportAsTV(false);
            options.setUseTitleMasks(true);
            
            AndResourceFilter filter = new AndResourceFilter(new MediaTypeFilter(MediaResourceType.ANY_VIDEO));
            ProgressTracker<MetadataItem> progress = new ProgressTracker<MetadataItem>(new ConsoleProgressMonitor());
            
            // finally process the videos
            if (automaticUpdate) {
                MetadataProcessor processor = new MetadataProcessor(searchType, providers, persistence, options);
                processor.process(parentFolder, recurse, filter, progress);
            } else {
                // manual update, prompt and select
                InteractiveMetadataProcessor processor = new ConsoleInteractiveMetadataProcessor(searchType, providers, persistence, options, displaySize);
                processor.process(parentFolder, recurse, filter, progress);
            }
            
            // now that we've scanned the files, let's check the results...
            System.out.printf("         Updated: %s items\n", progress.getSuccessfulItems().size());
            System.out.printf("Failed to Update: %s items\n", progress.getFailedItems().size());
            
            if (automaticUpdate && progress.getFailedItems().size()>0 && prompt) {
                while (!progress.isCancelled() && progress.getFailedItems().peek()!=null) {
                    TrackedItem<MetadataItem> item = progress.getFailedItems().pop();
                    System.out.println("  File: " + PathUtils.getLocation(item.getItem().getFile()));
                    System.out.println("Reason: " + item.getMessage());
                    System.out.println("---------------------------------");
                    InteractiveMetadataProcessor processor = new ConsoleInteractiveMetadataProcessor(searchType, providers, persistence, options, displaySize);
                    processor.process(item.getItem().getFile(), recurse, filter, progress);
                    System.out.println("---------------------------------");
                }
            } else {
                for (TrackedItem<MetadataItem> item : progress.getFailedItems()) {
                    System.out.println("  File: " + PathUtils.getLocation(item.getItem().getFile()));
                    System.out.println("Reason: " + item.getMessage());
                    System.out.println("---------------------------------");
                }
            }
        }

        // TODO: Wait for all processes to finish... udpate status every couple of seconds...
        
        // check if we need to refresh sage tv
        if (refreshSageTV) {
            try {
                System.out.println("Notifying Sage to Refresh Imported Media");
                proxyAPI.enableRemoteAPI(true);
                Global.RunLibraryImportScan(false);
            } catch (Throwable t) {
                log.error("Failed while calling RunLibraryImportScan()", t);
            }
        }
    }

    public void renderProviders(List<IMediaMetadataProvider> providers) {
        System.out.println("\n\nInstalled Metadata Providers (*=default");
        for (IMediaMetadataProvider p : providers) {
            System.out.printf("%1s %-20s\n", "", p.getInfo().getId());
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
    @CommandLineArg(name = "recurse", description = "Recursively process sub directories. (default true)")
    public void setRecurse(boolean b) {
        this.recurse=b;
    }

    /**
     * if set to true, it will ignore exising metadata and fetch new information
     * even it doesn't need to.
     * 
     * @param b
     */
    @CommandLineArg(name = "overwrite", description = "Overwrite metadata and fanart. (default false)")
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
        metadataConfig.setFanartEnabled(b);
    }

    @CommandLineArg(name = "fanartFolder", description = "Central Fanart Folder location")
    public void setForceThumbnailOverwrite(String folder) {
        metadataConfig.setFanartEnabled(true);
        metadataConfig.setCentralFanartFolder(folder);
        
        log.debug("Central Fanart Enabled; Using Folder: " + metadataConfig.getFanartCentralFolder());
    }

    /**
     * used by the renderResult() to limit the # of results to show.
     * 
     * @param size
     */
    @CommandLineArg(name = "displaySize", description = "# of search results to display on the screen. (default 10)")
    public void setDisplaySize(String size) {
        displaySize = (Integer.parseInt(size));
    }

    /**
     * Just list the movies that it would find given the setFiles(...)
     * 
     * @param b
     */
    @CommandLineArg(name = "listMedia", description = "Just list the media it would find.")
    public void setListMovies(boolean b) {
        this.listMedia = b;
    }

    /**
     * List the know metadata provides that are installed.
     * 
     * @param b
     */
    @CommandLineArg(name = "listProviders", description = "Just list the installed metadata providers.")
    public void setListProviders(boolean b) {
        this.listProvders = b;
    }

    /**
     * if true, then it will attempt to refresh metadata for entries that
     * already have metadata by using the providerDataUrl for the media file.
     * 
     * @param b
     */
    @CommandLineArg(name = "update", description = "update/refresh metadata for items that have been modified (ie, .properties file has been edited) and update files that have no metadata.")
    public void setUpdate(boolean b) {
        this.updateMetadata = b;
    }

    /**
     * enable/disable offline video support. Offline videos are empty video
     * files with metadata and thumbnails. A user would create these if the
     * video did exist in his/her collection but they are currently offline.
     * 
     * @param b
     */
    @CommandLineArg(name = "offline", description = "Create an offline file for the mediafile passed, and fetch its metadata.")
    public void setOffline(boolean b) {
        this.offline = b;
    }

    
    /**
     * update the timestamp on a file
     * 
     * @param b
     */
    @CommandLineArg(name = "touch", description = "Touch (update timestamp) on a given file/folder")
    public void setTouch(boolean b) {
        options.setTouchingFiles(b);
    }

    /**
     * Set tv metadata provider.
     * 
     * @param s
     */
    @CommandLineArg(name = "tvProviders", description = "Set the TV metadata providers. Comma separated, no spaces.")
    public void setTVMetadataProvider(String s) {
        log.info("Setting the tv providers: " + s);
        tvProviders = s;
    }

    /**
     * Set tv metadata provider.
     * 
     * @param s
     */
    @CommandLineArg(name = "movieProviders", description = "Set the movie metadata providers. Comma separated, no spaces.")
    public void setMovieMetadataProvider(String s) {
        log.info("Setting the movie providers: " + s);
        movieProviders = s;
    }

    /**
     * Set music metadata provider.
     * 
     * @param s
     */
    @CommandLineArg(name = "musicProviders", description = "Set the music metadata providers. Comma separated, no spaces.")
    public void setMusicMetadataProvider(String s) {
        log.info("Setting the music providers: " + s);
        musicProviders = s;
    }

    /**
     * Show Metadata Information for a given movie
     * 
     * @param s
     */
    @CommandLineArg(name = "listMetadata", description = "List the current metadata/properties for media items")
    public void setShowMetadata(boolean b) {
        this.showMetadata = b;
    }

    /**
     * Show Metadata Properties supported by this application.
     * 
     * @param 
     */
    @CommandLineArg(name = "listSupportedMetadata", description = "List the supported metadata properties")
    public void setShowMetadataProperties(boolean b) {
        this.showSupportedMetadata = b;
    }

    /**
     * Dump Current Configuration Properties
     * 
     * @param s
     */
    @CommandLineArg(name = "listProperties", description = "List the current configuration")
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
        automaticUpdate=b;
    }

    @CommandLineArg(name = "fanartOnly", description = "Only process fanart, ignore updating metadata (default false)")
    public void setFanartOnly(boolean b) {
        options.setOverwriteFanart(b);
        options.setOverwriteMetadata(!b);
    }

    @CommandLineArg(name = "metadataOnly", description = "Only process metadata, ignore updating fanart (default false)")
    public void setMetadataOnly(boolean b) {
        options.setOverwriteMetadata(b);
        options.setOverwriteFanart(!b);
    }
    
    @CommandLineArg(name = "renameFilePattern", description = "Renames the file(s) being processed according to the pattern given.")
    public void setRenameFilePattern(String pattern) {
    	options.setFileRenamePattern(pattern);
    }
    
    @CommandLineArg(name = "setProperty", description = "Sets a configuration property (--setProperty=name:value) (use --showProperties to get property list)")
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
    @CommandLineArg(name = "refreshSageTV", description = "Notify SageTV to refresh it's Media Library. (default false, require RemoteAPIs to be installed)")
    public void setRefreshSageTV(boolean b) {
        refreshSageTV=b;
    }

    /**
     * forces the search type
     * 
     * @param s
     */
    @CommandLineArg(name = "searchType", description = "Force the search type ('tv', 'movie', or 'music', default will autoselect based on media")
    public void setTVSearch(String s) {
        this.searchType = MediaType.valueOf(s);
    }

    /**
     * perform the phoenix tests on the sage server
     * 
     * @param s
     */
    @CommandLineArg(name = "report", description = "Perform a 'quick' or 'detailed' report on fanart (use this to debug farart for an item).")
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
    /**
     * @param s
     */
    @CommandLineArg(name = "out", description = "Don't update anything, just dump results to stdout")
    public void setDontUpdate(boolean out) {
        this.dontUpdate=out;
    }

    /**
     * Test Xbmc Scraper 
     * 
     * @param s
     */
    @CommandLineArg(name = "testXbmcScraper", description = "Test XbmcScraper; Pass ScraperPath,Function,ScraperUrl (for debugging only)")
    public void testXbmcScraper(String cmdArgs) {
        try {
            String parts[] = cmdArgs.split(",");
            if (parts==null || parts.length<3) {
                System.err.println("testXbmcScraper requires Function and Url in the form; ScraperFile,Function,Url");
            } else {
                XbmcScraperParser parser = new XbmcScraperParser();
                System.err.println("Loading Scraper: " + parts[0]);
                XbmcScraper scraper = parser.parseScraper(new File(parts[0].trim()));
                XbmcScraperProcessor proc = new XbmcScraperProcessor(scraper);
                System.err.println("Loading Url: " + parts[2].trim());
                XbmcUrl url = new XbmcUrl(parts[2].trim());
                System.err.println("Executing Function: " + parts[1].trim());
                String xml = (proc.executeFunction(parts[1].trim(), new String[] {"", url.getTextContent(), url.toExternalForm()}));
                if (xml==null || xml.trim().length()==0) {
                    throw new Exception("Invalid Function or URL!");
                }
                
                FileWriter fw = new FileWriter(parts[1].trim()+".xml");
                IOUtils.write(xml, fw);
                fw.flush();
                fw.close();
                
                System.err.println("Wrote: " + parts[1].trim()+".xml");
                
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        System.exit(0);
    }
    
    public String[] getFiles() {
        return files;
    }

    public void exit(String string) {
        System.out.println(string);
        try {
            if (rememberSelection) {
                ConfigurationManager.getInstance().saveTitleMappings();
            }
            
            Phoenix.getInstance().getConfigurationManager().save();
        } catch (Exception e) {
            log.error("Failed to save configuration before exit.", e);
        }
        System.exit(1);
    }

    public PersistenceOptions getPersistenceOptions() {
        return options;
    }
}
