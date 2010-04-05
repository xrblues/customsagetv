package bmt;

import java.util.Map;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.process.ScanMediaFileEvent;
import org.jdna.sage.PluginConfiguration;
import org.jdna.sage.SageScanMediaFileEvenHandler;

import sage.SageTVPluginRegistry;
import sagex.api.MediaFileAPI;
import sagex.phoenix.Phoenix;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.event.HandlerRegistration;
import sagex.plugin.AbstractPlugin;
import sagex.plugin.PluginProperty;
import sagex.plugin.SageEvent;

public class BMTPluginActivator extends AbstractPlugin {
    private Logger log = Logger.getLogger(BMTPluginActivator.class);
    private HandlerRegistration mediaHandler = null;

    private PluginConfiguration pluginConfig;

    public BMTPluginActivator(SageTVPluginRegistry registry) {
        super(registry);
        addProperty(new PluginProperty(CONFIG_BOOL, "bmt/automaticPluginEnabled", "true", 
                "Enable Automatic Plugin", 
                "When the automatic plugin is enabled it will attempt to fetch metadata and farnart when new media files are added, or when new TV shows are recorded.", null));

        addProperty(new PluginProperty(CONFIG_BOOL, "phoenix/mediametadata/fanartEnabled", "true", 
                "Enable Fanart", 
                "", null));

        addProperty(new PluginProperty(CONFIG_DIRECTORY, "phoenix/mediametadata/fanartCentralFolder", "STVs/Phoenix/Fanart", 
                "Enable Fanart", 
                "", null));

        addProperty(new PluginProperty(CONFIG_DIRECTORY, "bmt/metadata/movieProviders", "imdb-2,themoviedb.org,imdb.xml", 
                "Movie Providers", 
                "You can select multiple providers for fetching Metadata/Fanart", new String[] {"imdb","imdb-2","themoviedb.org","dvdprofiler","mymovies","tvdb","imdb.xml"}));

        addProperty(new PluginProperty(CONFIG_DIRECTORY, "bmt/metadata/tvProviders", "tvdb", 
                "TV Providers", 
                "You can select multiple providers for fetching Metadata/Fanart", new String[] {"tvdb"}));

        pluginConfig = GroupProxy.get(PluginConfiguration.class);
    }

    @SageEvent("MediaFileImported")
    public void onMediaFileImported(String name, Map args) {
        Object mf = args.get("MediaFile");
        log.info("Handling Imported MediaFile: " + mf);
        
        PersistenceOptions options = new PersistenceOptions();
        options.setUseTitleMasks(true);
        options.setCreateDefaultSTVThumbnail(pluginConfig.getCreateDefaultSTVThumbnail());
        options.setCreateProperties(pluginConfig.getCreateProperties());
        options.setUpdateWizBin(true);
        
        // for now, set this to false, in the future, we'll set it true again...
        options.setUsingAutomaticPlugin(false);

        // fire the event that will actually handle the metadata fetching
        Phoenix.getInstance().getEventBus().fireEvent(new ScanMediaFileEvent(MediaFileAPI.GetFileForSegment(mf, 0), options));
    }

    @SageEvent("RecordingCompleted")
    public void onRecordingCompleted(String name, Map args) {
        // for now just call mediafile added
        onMediaFileImported(name, args);
    }

    /* (non-Javadoc)
     * @see sagex.plugin.AbstractPlugin#start()
     */
    @Override
    public void start() {
        super.start();
        
        mediaHandler = Phoenix.getInstance().getEventBus().addHandler(ScanMediaFileEvent.TYPE, new SageScanMediaFileEvenHandler());
    }

    /* (non-Javadoc)
     * @see sagex.plugin.AbstractPlugin#stop()
     */
    @Override
    public void stop() {
        super.stop();
        mediaHandler.remove();
    }
}
