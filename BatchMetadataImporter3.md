

# Introduction #
Batch Metadata Tools (aka BMT) is a set of metadata tools for SageTV, that helps in the fetching of metadata and fanart for movies and tv shows (downloaded or recorded).  Using the BMT WebUI, you can scan you movie collection and edit metadata for your SageTV media files.

Metadata refers to the attributes of a media file. ie, Title, Description, Plot, Release Date, MPAA Rating, etc.

Fanart refers to images related to the media, such a banners, posters, and backgrounds.

Metadata sources are called **Metadata Providers** because they are sources that provide metadata. BMT ships a few different providers, including [IMDb](http://www.imdb.com/), [TheTVDB.com](http://TheTVDB.com/), [TheMovieDB.org](http://themoviedb.org/), [DVD Profiler](http://www.invelos.com/), and [MyMovies](http://www.mymovies.dk/).

# Installation #
Before you install BMT, install the [Jetty Server plugin for SageTV](http://forums.sagetv.com/forums/downloads.php?do=file&id=233) and ensure that it is running correctly.  If this plugin is installed, then you can configure most aspects of BMT using a Web UI.  Jetty is not required for BMT, but it is highly recommended, especially so that you can take advantage of the Web UI for configuration/management.

Be sure to stop the SageTV server before installation/updating.

To install BMT, simply download the latest build from the downloads section and then extract the .zip file into the SageTV home directory.  This should put some .jar files in the JARs directory put the web ui applications (bmtweb) into the jetty webapps area.

Once you've extraced the files, you can restart the SageTV server.  Once the server is started, you can now access the BMT Web UI using a browser and continue to configure the tool and perform a scan of your collection.

To access the UI, you will use the following URL
```
 http://JETTY_SERVER:JETTY_PORT/bmt/
```
Where **JETTY\_SERVER** is the ip address/hostname of your SageTV Server and the **JETTY\_PORT** is the port that you used to install Jetty.

After you upgrade a BMT release, be sure to refresh BMT Web UI in your browser by hitting the **Shift+Reload** button in your browser.  This will force your browser to reload the newer files, in the event that your browser is still caching them.

## Load the STV Plugin for On-Demand Lookups ##
BMT provides a basic On-Demand search for Movies and TV Shows, that enables you to download metadata/fanart for a given SageTV media file, using the SageTV TV interface.

Depending on if you are running the SageMC STV UI or the Default STV, do one of the following. (if you don't know, then you are probably running the default stv)

### Default STV ###
From the Default STV setup menu, import the **MetadataFanartTools-DefaultSTV.stvi** into the default STV.

### SageMC ###
From the SageMC settings menu, import the **MetadataFanartTools-SageMC.xml**.  This will add a Metadata/Fanart configuration section to the "Extras" section in the SageMC configuration screens.