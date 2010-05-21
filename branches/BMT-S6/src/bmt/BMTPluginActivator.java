package bmt;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.metadataupdater.Version;
import org.jdna.sage.MetadataUpdaterPlugin;
import org.jdna.url.CachedUrlCleanupTask;

import sage.SageTVPluginRegistry;
import sagex.api.Configuration;
import sagex.api.metadata.ISageCustomMetadataRW;
import sagex.api.metadata.SageMetadata;
import sagex.phoenix.Phoenix;
import sagex.plugin.AbstractPlugin;
import sagex.plugin.ConfigValueChangeHandler;
import sagex.util.Log4jConfigurator;

public class BMTPluginActivator extends AbstractPlugin {
    private static final String PROP_AUTOMATIC_ENABLED = "bmt/automaticPluginEnabled";
    private static final String PROP_FANART_ENABLED    = "phoenix/mediametadata/fanartEnabled";
    private static final String PROP_FANART_FOLDER     = "phoenix/mediametadata/fanartCentralFolder";
    private static final String PROP_MOVIE_PROVIDERS   = "bmt/metadata/movieProviders";
    private static final String PROP_TV_PROVIDERS      = "bmt/metadata/tvProviders";

    private Logger              log                    = Logger.getLogger(BMTPluginActivator.class);

    public BMTPluginActivator(SageTVPluginRegistry registry) {
        super(registry);
        // configure the logger
        Log4jConfigurator.configureQuietly("bmt");

        addProperty(CONFIG_BOOL, PROP_AUTOMATIC_ENABLED, "true", "Automatically find metadata/fanart for new media", "When the automatic plugin is enabled it will attempt to fetch metadata and farnart when new media files are added, or when new TV shows are recorded.");
        addProperty(CONFIG_BOOL, PROP_FANART_ENABLED, "true", "Enable Fanart", "BMT will download fanart (posters, backgrounds, banners) in addition to normal metadata");
        addProperty(CONFIG_DIRECTORY, PROP_FANART_FOLDER, "STVs/Phoenix/Fanart", "Fanart Folder", "").setVisibleOnSetting(this, PROP_FANART_ENABLED);
        addProperty(CONFIG_MULTICHOICE, PROP_MOVIE_PROVIDERS, "imdb-2,themoviedb.org,imdb.xml", "Movie Providers", "You can select multiple providers for fetching Metadata/Fanart", new String[] { "imdb", "imdb-2", "themoviedb.org", "dvdprofiler", "dvdprofiler-2", "mymovies", "imdb.xml" }, ",");
        addProperty(CONFIG_MULTICHOICE, PROP_TV_PROVIDERS, "tvdb", "TV Providers", "You can select multiple providers for fetching Metadata/Fanart", new String[] { "tvdb" }, ",");
    }

    /*
     * (non-Javadoc)
     * 
     * @see sagex.plugin.AbstractPlugin#start()
     */
    @Override
    public void start() {
        super.start();
        log.info("========= BEGIN BATCH METADATA TOOLS ENVIRONMENT ==============");
        log.info("    BMT Version:  " + Version.VERSION);
        log.info("Phoenix Version:  " + phoenix.api.GetVersion());
        log.info("  Sagex Version:  " + sagex.api.Version.GetVersion());
        log.info("   Java Version:  " + System.getProperty("java.version"));
        log.info("========= END BATCH METADATA TOOLS ENVIRONMENT ==============");

        // Add clean up tasks
        log.info("Registering URL Cache Cleaner Process");
        Phoenix.getInstance().getTaskManager().scheduleTask(CachedUrlCleanupTask.TaskID, new CachedUrlCleanupTask(), Calendar.getInstance().getTime(), 24 * 60 * 60 * 1000);

        // install the custom metadata fields
        Set<String> all = new TreeSet<String>();
        log.info("Setting custom metadata fields...");
        String customPropsStr = Configuration.GetServerProperty("custom_metadata_properties", null);
        if (customPropsStr != null) {
            String fields[] = customPropsStr.split("\\s*;\\s*");
            all.addAll(Arrays.asList(fields));
        }
        String defined[] = SageMetadata.getPropertyKeys(ISageCustomMetadataRW.class);
        all.addAll(Arrays.asList(defined));
        // remove any empty fields
        all.remove("");
        String allFields = StringUtils.join(all, ";");
        log.info("Custom Metadata Fields have been updated: " + allFields);
        Configuration.SetServerProperty("custom_metadata_properties", allFields);

        // check if fanart is enabled, and if so, verify that the fanart dir is
        // set
        if (getConfigBoolValue(PROP_FANART_ENABLED)) {
            File f = new File(getConfigValue(PROP_FANART_FOLDER));
            if (!f.exists()) {
                log.info("Fanart dir does not exist; Creating: " + f);
                if (!f.mkdirs()) {
                    log.warn("Failed to create Fanart Dir: " + f);
                }
            }
        }

        // set the automatic plugin status
        onAutomaticHandlerChanged();
    }

    @ConfigValueChangeHandler(PROP_AUTOMATIC_ENABLED)
    public void onAutomaticHandlerChanged() {
        String auto = Configuration.GetServerProperty("mediafile_metadata_parser_plugins", "");
        String fields[] = auto.split("\\s*;\\s*");
        Set<String> parsers = new TreeSet<String>();
        parsers.addAll(Arrays.asList(fields));
        if (getConfigBoolValue(PROP_AUTOMATIC_ENABLED)) {
            log.info("Registering Automatic Plugin");
            parsers.add(MetadataUpdaterPlugin.class.getName());
        } else {
            log.info("Removing Automatic Plugin");
            parsers.remove(MetadataUpdaterPlugin.class.getName());
        }

        if (parsers.size() > 0) {
            // remove any empty fields
            parsers.remove("");
            Configuration.SetServerProperty("mediafile_metadata_parser_plugins", StringUtils.join(parsers, ";"));
        } else {
            Configuration.RemoveServerProperty("mediafile_metadata_parser_plugins");
        }
    }
}
