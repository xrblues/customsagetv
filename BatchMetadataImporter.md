

# Introduction #

Batch Metadata Tools (BMT) is a command line tool and STV plugin that will find and download metadata and fanart for your Movie and TV collection.  The [SageTV MediaFileMetadata Plugin](http://code.google.com/p/customsagetv/wiki/SageTVMetadataPlugin) portion of this tool will automatically search and download metadata and images for new media as it's added to your SageTV library.  As of 2.0, the command line tool and the STV plugin have been merged into a single download.

Metadata refers to the attributes of a media file.  ie, Title, Description, Plot, Release Date, MPAA Rating, etc.

Fanart refers to images related to the media, such a banners, posters, and backgrounds.

Metadata sources, ie, imdb, tvdb, etc, are called Metadata Providers or Providers for short.  BMT ships a few different providers.

For details about each provider, check out the links below
  * [IMDB Provider](IMDBProvider.md)
  * [DVD Profiler Provider](DVDProfilerProvider.md)
  * [themoviedb.com Provider](TheMovieDBProvider.md)
  * [CompositeProvider](CompositeProvider.md) Composite (build your own) Provider]
  * [XbmcScraperProvider](XbmcScraperProvider.md) Provider that uses the Xbmc Scraper Format

The current provider list
```
  imdb                
   - IMDB Provider (Stuckless)
   - IMDB Provider that provides very resonable results, AND exact match searches.

  nielm_imdb          
   - IMDB Provider (Nielm)
   - IMDB Provider that provides very detailed results, but no exact match searches.

  dvdprofiler   
   - Local DVD Profiler Provider
   - DVD Profiler Provider using local xml and images (Stuckless).

  themoviedb.org      
   - themoviedb.org
   - Provider that uses themoviedb as metadata and coverart source.

  themoviedb.org-2    
   - Composite Stuckless IMDB/TheMovieDB Provider
   - Composite Provider that uses the default IMDB provider for searching, and themoviedb.com for details.  This provider can provide decent results for posters and backdrop coverart.

  imdb.xml            
   - IMDb (XBMC Scraper)
   - scrapers/xbmc/video/imdb.xml

  tvdb.xml            
   - TheTVDB.com (XBMC Scraper)
   - scrapers/xbmc/video/tvdb.xml
```


# Central Fanart #
As of 2.0, several projects collaborated to come up with a unified way to store and retrieve fanart.  A set of APIs, called Phoenix APIs, were created to help developers deal with fanart locations in a conistent way.  BMT usese those fanart apis to ensure that it can download fanart to the correct locations.

2.0 also added 2 new command line arguments --fanartEnabled and --fanartFolder.  If you set the fanart folder, then it will implicitly set the fanartEnabled to true.

# Requirements #
Java 5 or later

# Installation #
Download the zip and extract into a new directory or the SageTV root directory, if you are running this as a plugin.  As with any tool/plugin, if you extract into an existing directory, then make sure you clean the libs directories and ensure that there are no duplicate jars with different versions.

_NOTE: As of 2.0 there metadata-updater.jar and sagex-api.jar do not contain a versions in their filenames.  This was done to make future upgrades of the 2.x releases less painful.  But, if you do extract a 2.x release over a 1.x release, then you must clean out the jars area and remove the metadata-updater-VERSION.jar and sagex.api-VERSION.jar but leave the jars that do not contain a version._

If you are running this from the commandline, then it will clean the jar area automatically, and notify you about duplicate jars.  So, even if you are installing this as a plugin, it's best to run,

```
# java -jar MetadataTool.jar
```

Just so that it can clean your JARs area, by removing duplicate jars that will cause runtime issues.  Only the help will be displayed, but it will have removed any duplicate jars.

## STV / Automated Metadata Fetching Installation ##

Shutdown your Sage client/server process.

Download the zip and unzip at the root of the SageTV server directory.

Restart your Sage client / server so that the new jars are picked up by SageTV.


### Default STV ###
If you running the default STV, then do this.

Import the MetadataFanartTools-DefaultSTV.stvi into the default stv.  This will add a new Metadata/Fanart configuration option to the Detailed Setup menu.  From there you can enable the central fanart and set the central fanart location.  You can also enable the automated metadata lookup.  If you enable the automated metadata lookup, you will need to restart the server process for this change to be picked up by sage.

You can also configure the metadata providers that you want to use for searching for new metadata/fanart.  By default it should come with pretty much everything enabled.

### SageMC ###
If you are running SageMC, then do this.

In SageMC import the MetadataFanartTools-SageMC.xml.  This will add a Metadata/Fanart configuration section to the "Extras" section in the SageMC configuration screens.

From there, like with the default stv, you can enable the automated lookups and configure which providers you want to use.  To configure the Central Fanart folder, use the SageMC fanart configuration options.

### Manual Installation ###
Only do this if you want to manualy install/configure the automatic plugin.

The Sage.properties.metadataupdater is the property that you need to update/edit in the Sage.properties.

To update the Sage.properties, **shut down SageTV**, then edit the Sage.properties.  Merge the property from the Sage.properties.metadataupdater into the core Sage.properties.

```
mediafile_metadata_parser_plugins=org.jdna.sage.MetadataUpdaterPlugin
custom_metadata_properties=MediaProviderDataID;MediaTitle;MediaType;OriginalAirDate;EpisodeTitle;EpisodeNumber;SeasonNumber;DiscNumber;UserRating
```

If you have an existing metadata.properties, then copy that file to the sagetv server root directory (ie, same directory where Sage.properties exists)

Start SageTV, add a new movie, and then tell SageTV to refresh.  If your movie was automatically found, then it should appear with the appropriate thumbnail and properties.

Good luck, and you can post any comments/problems to the SageTV thread for the [Batch Metadata Tools](http://forums.sagetv.com/forums/showthread.php?t=34301).

# On Demand Lookups within a SageTV Application #
If you've imported the MetadataFanartTools stvi/xml for the default stv/sagemc, then you will also get the option when you have selected a video, to search/download fanart for that particular item.

In the default stv, this menu can be found by selecting a video and bringing up it's options menu.  In the options menu, there will now be a "Metadata/Fanart" button.  You can use to update the item's fanart.

In the future, once Sage support setting metadata elements, this same process will be used to update the metadata as well as the fanart for an item.


# Commandline Usage #
```
 # java -jar MetadataTool.jar
Batch MetaData Tools (2.1)
Import/Update Movie MetaData from a MetaData Provider.

Usage:java MetadataTool OPTIONS ...

OPTIONS: (* are required)
  --auto                 Automatically choose best search result. [if false, it will force you to choose for each item] (default true)
  --displaySize          # of search results to display on the screen. (default 10)
  --fanartEnabled        Enable fanart support (backgrounds, banners, extra posters, etc). (default true)
  --fanartFolder         Central Fanart Folder location
  --fanartOnly           Only process fanart, ignore updating metadata (default false)
  --gui                  Enable/Disable gui (default true)
  --listMovies           Just list the movies it would find. (default false)
  --listProviders        Just list the installed metadata providers. (default false)
  --metadata             Show metadata for movie. (default false)
  --metadataOnly         Only process metadata, ignore updating fanart (default false)
  --offline              if true, then the passed file(s) will be treated as offline files. (default false)
  --overwrite            Overwrite metadata and images. (default false)
  --overwriteFanart      Overwrite fanart. (default false)
  --overwriteMetadata    Overwrite metadata. (default false)
  --prompt               When a media file cannot be updated, then prompt to enter a title to search on. (default true)
  --provider             Set the metadata provider. (default imdb)
  --recurse              Recursively process sub directories. (default false)
  --refreshSageTV        Notify SageTV to refresh it's Media Library. (default false)
  --setProperty          Sets a Property (--setProperty=name:value) (use --showProperties to get property list)
  --showProperties       Show the current configuration. (default false)
  --showSupportedMetadata Show the supported metadata properties. (default false)
  --tv                   Force a TV Search (default false)
  --update               Update Missing AND Updated Metadata. (default false)
```

## Major Changes from the 1.x release ##
The 2.0 release is a major rewrite of some parts of the BMT code.  Because of this, many commandline arguments were removed.  Most of these changes were done to simplify the logic with the main processing component.  For example, there were separate commandline arguments for overwriting thumbnail, properties, backgrounds, etc, now, there is a single --overwrite argument.  If it's set, then will overwrite images, properties, etc.

A small change to the DVD Profiler providers were made.  There used to be 2 DVD Profiler providers... now there is 1, and it's provider id is, dvdprofiler.  Basically the remote url dvd profiler provider was removed.  Also the DVD Profiler provider will autodetect changes to it's Collection.xml and rebuild the indexes as necessary.  As a result, the --reindex command line argument was removed.

## Commandline Usage Overview ##
Before you get started, you may have to execute the command once without any arguments to see the help screen.

Once that works, try
```
# java -jar MetadataTool.jar --listProviders
```

This should list the installed metadata providers. You should see at least 1 provider.  You may see more depending of the version of the tool, or if you've downloaded other provider plugins.  See [Creating Provider Plugins](CreatingProviderPlugins.md) for information on how to create and register your own plugin.

Next, try to scan your Movie directory without changing anything....
```
# java -jar MetadataTool.jar --listMovies YOUR_MOVIE_DIR
```

This should provide you with a list of Movies that it found. If you are expecting to see multiple entries for each movie because you have multi cd movies, then you may be surprised to see (hopefully) that you only see one movie entry per multi cd movie.

Now you are ready to do some real work. For testing, perhaps copy a handful of movies to working directory and start there, until you feel comfortable with the tool.
To create the Sage metadata and thumbnails using the default options...
```
# java -jar MetadataTool.jar YOUR_MOVIE_DIR
```

This should create the necessary properties and download the thumbnails, if there were any.

Take a look at the properties and see if you are comfortable with that it produced.

The default options will automatically select what it feels is the best choice and use it. It will only prompt you if it cannot find a suitable entry automatically. When you are prompted, you have several options, quite, next (skip), enter a number of the entry to use, or enter another search string.

Once all movies are scanned, it will display a summary at the end.

If you need/want to process subdirectories, then pass the **--recurse** switch so that the processor will follow subdirectories.

# How Do I... #
### Force the command line tool to prompt me for each item? ###
```
# java -jar MetadataTool.jar --auto=false --prompt=true
```

### Prevent the command line from prompting me at ever? ###
```
# java -jar MetadataTool.jar --prompt=false
```

### Tell the command line tool to show me more results? ###
```
# java -jar MetadataTool.jar --displaySize=20
```

### Set my fanart folder? ###
If you are running from the command line, then use --fanartFolder.
If you are running it as a STV plugin, then you can set the Fanart/Metadata options in the Setup -> Detailed Setup -> Metadata/Fanart screen.  In SageMC, it's in the "Extras" menu under Setup.

### Tell the command line tool to process subdirectories? ###
```
# java -jar MetadataTool.jar --recurse
```

# Advanced Topics #
## Properties ##
You can get a list of supported properties, by running the command
```
# java -jar MetadataTool --showProperties
```

It should produce the following results.  Keep in mind, that if  you have a custom metadata.properties, then it's values will be reflected in the output.  You can use --showProperties to verify that your properties are being taken into account

BMT ships with a metadata.properties.sample, so you can rename that to metadata.properties and simply copy and paste a property from the list below (or --showProperties) and add it directly to that file.  Any changes to the property file will require a SageTV restart.

```
Batch MetaData Tools (2.1)
#       Begin: urlconfiguration
# Description: Configures Properties regarding how URLs are handled.

# Description: Cache Directory where cached URLs are stored
#        Type: java.lang.String
/urlconfiguration/cacheDir=cache/url/

# Description: How long, in seconds, URLs remain in the cache
#        Type: int
/urlconfiguration/cacheExpiryInSeconds=86400

# Description: URL Factory class name for creating new Url objects
#        Type: java.lang.String
/urlconfiguration/urlFactoryClass=org.jdna.url.CachedUrlFactory

# Description: HTTP User Agent that is sent with each hew http request
#        Type: java.lang.String
/urlconfiguration/httpUserAgent=Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.1) Gecko/2008072820 Firefox/3.0.1
# End: urlconfiguration


#       Begin: metadataUpdater
# Description: Configuration for Main Metadata Updater

# Description: Default Folder to Scan (GUI Only)
#        Type: java.lang.String
/metadataUpdater/guiFolderToScan=

# Description: Number of results to display in the search results
#        Type: int
/metadataUpdater/searchResultDisplaySize=10

# Description: Once Scanning is Complete, notify SageTV of the changes.
#        Type: boolean
/metadataUpdater/refreshSageTV=false

# Description: Automatically update by auto selecting 'best' search result.
#        Type: boolean
/metadataUpdater/automaticUpdate=true

# Description: Recursively process sub folders
#        Type: boolean
/metadataUpdater/recurseFolders=false

# Description: Only process files that is missing metadata
#        Type: boolean
/metadataUpdater/processMissingMetadataOnly=true

# Description: Enable Fanart downloading
#        Type: boolean
/metadataUpdater/fanartEnabled=true

# Description: Location of the central fanart folder
#        Type: java.lang.String
/metadataUpdater/fanartCentralFolder=

# Description: Remember a search result when you select it form a list, for use in later searches.
#        Type: boolean
/metadataUpdater/rememberSelectedSearches=true
# End: metadataUpdater


#       Begin: media
# Description: Configuration for Media items

# Description: Stacking Model regex (taken from xbmc group)
#        Type: java.lang.String
/media/stackingModelRegex=[ _\\.-]+(cd|dvd|part|disc)[ _\\.-]*([0-9a-d]+)

# Description: If true, then the thumbnail will be written to the VIDEO_TS folder.  Otherwise, it gets written to the normal DVD location
#        Type: boolean
/media/useTSFolderForThumbnail=false

# Description: Regular expression for the file extensions that are recognized
#        Type: java.lang.String
/media/videoExtensionsRegex=avi|mpg|divx|mkv|wmv|mov|xvid

# Description: Regular expression for the directory names to ignore
#        Type: java.lang.String
/media/excludeVideoDirsRegex=
# End: media


#       Begin: metadata
# Description: Configuration for Metadata

# Description: Default class name for storing metadata
#        Type: java.lang.String
/metadata/persistenceClass=org.jdna.media.metadata.impl.sage.SageTVPropertiesWithCentralFanartPersistence

# Description: Comma separated list of known metadata providers (ie, can be used for searching for metadata)
#        Type: java.lang.String
/metadata/videoMetadataProviders=org.jdna.media.metadata.impl.imdb.IMDBMetaDataProvider,org.jdna.media.metadata.impl.nielm.NielmIMDBMetaDataProvider,,org.jdna.media.metadata.impl.dvdproflocal.LocalDVDProfMetaDataProvider,org.jdna.media.metadata.impl.themoviedb.TheMovieDBMetadataProvider

# Description: Comma separated list of words that will be removed from a title when doing a search
#        Type: java.lang.String
/metadata/wordsToClean=1080p,720p,480p,1080i,720i,480i,dvd,dvdrip,cam,ts,tc,scr,screener,dvdscr,xvid,divx,avi,vrs,repack,mallat,proper,dmt,dmd,stv,HDTV,x264

# Description: Default provider id to use (comma separate, if more than 1)
#        Type: java.lang.String
/metadata/defaultProviderId=tvdb.xml,themoviedb.org,themoviedb.org-2,imdb.xml,imdb

# Description: Score which must be exceeded to consider a result a good match
#        Type: float
/metadata/goodScoreThreshold=0.9

# Description: If true, then providers will check alternate titles for matches.
#        Type: boolean
/metadata/scoreAlternateTitles=true

# Description: Resize poster to scale using the specified max width
#        Type: int
/metadata/posterImageWidth=200

# Description: Resize banner to scale using the specified max width
#        Type: int
/metadata/bannerImageWidth=-1

# Description: Resize backgrond to scale using the specified max width
#        Type: int
/metadata/backgroundImageWidth=-1

# Description: Maximum # of images within each fanart type to download.
#        Type: int
/metadata/maxDownloadableImages=5

# Description: When writing fanart, if this is enabled, an additional poster file will be written that is compatible with the default stv.
#        Type: boolean
/metadata/enableDefaultSTVPosterCompatibility=false

# Description: Regex that is used to parse the AiringId from a filename.
#        Type: java.lang.String
/metadata/airingIdRegex=([0-9]+)-[0-9]{1,2}\.
# End: metadata


#       Begin: dvdprofilerLocal
# Description: Configuration for the Local DVD Profiler provider (ie, uses local DVD Profiler, not Urls)

# Description: Directory where the local dvd profiler data will be indexed
#        Type: java.lang.String
/dvdprofilerLocal/indexDir=cache/indexDVDProfLocal/

# Description: Local DVD Profiler image directory
#        Type: java.lang.String
/dvdprofilerLocal/imageDir=

# Description: Local DVD Profiler xml file
#        Type: java.lang.String
/dvdprofilerLocal/xmlFile=

# Description: Data/Time the xml file was modified as a long value.  Should not be set directly.
#        Type: long
/dvdprofilerLocal/xmlFileLastModified=0
# End: dvdprofilerLocal


#       Begin: sageMetadata
# Description: Configuration for the SageTV Metadata Persistence

# Description: How each Actor will be written.  This mask will be applied to each actor, and then appeneded into a single line for the properties file. {0} - Actor Name, {1} - Actor Role
#        Type: java.lang.String
/sageMetadata/actorMask={0} -- {1};\n

# Description: Description Mask (note ${PROP_FIELD_NAME} field names are looked up in the property file)
#        Type: java.lang.String
/sageMetadata/descriptionMask=${Description}\nUser Rating: ${UserRating}\n

# Description: Title to use for multi volume vidoes (_disc is disc # 1,2,3,etc)
#        Type: java.lang.String
/sageMetadata/multiCDTitleMask=${Title} Disc ${DiscNumber}

# Description: Title to use for single volume vidoes
#        Type: java.lang.String
/sageMetadata/titleMask=${Title}

# Description: Number genre levels to write.  -1 means all levels.
#        Type: int
/sageMetadata/genreLevels=1

# Description: Title mask to use for TV Files
#        Type: java.lang.String
/sageMetadata/tvTitleMask=${Title} - S${SeasonNumber}E${EpisodeNumber} - ${EpisodeTitle}

# Description: Title mask to use for TV on Dvd
#        Type: java.lang.String
/sageMetadata/tvDvdTitleMask=${Title} - S${SeasonNumber}D${DiscNumber}

# Description: Rewrite titles so that 'A Big Adventure' becomes 'Big Adventure, A'
#        Type: boolean
/sageMetadata/rewriteTitle=false

# Description: A Search/Replace Regex containing 2 groups that will rewrite the title
#        Type: java.lang.String
/sageMetadata/rewriteTitleRegex=^(in\s+the|in\s+a|i\s+am|in|the|a|an|i|am),?\s+(.*)
# End: sageMetadata


#       Begin: imdb
# Description: Configuration for IMDB Metadata Parser

# Description: Preferred height or width of an imdb thumbnail
#        Type: int
/imdb/forcedIMDBImageSize=512
# End: imdb


#       Begin: compositeMetadataProviders
# Description: Configuration for a Composite Metadata Provider

# Description: Provider Name
#        Type: java.lang.String
/compositeMetadataProviders/sample/name=Sample Composite Provider

# Description: Provider Description
#        Type: java.lang.String
/compositeMetadataProviders/sample/description=This is a sample Composite Provider that will disappear as soon as you create one

# Description: Optional Icon Url for the provider
#        Type: java.lang.String
/compositeMetadataProviders/sample/iconUrl=

# Description: Provider ID to use when searching
#        Type: java.lang.String
/compositeMetadataProviders/sample/searchProviderId=imdb

# Description: Provider ID to use when getting details
#        Type: java.lang.String
/compositeMetadataProviders/sample/detailProviderId=themoviedb.org

# Description: 1 - Use Search searchProvider then detailProvider; 2 - Use detailsProvider then searchProvider
#        Type: int
/compositeMetadataProviders/sample/compositeMode=2
# End: compositeMetadataProviders

```


If you need to change the value of a property for a single use, then you can use the --setProperty option.

```
# java -jar MetadataTool.jar --setProperty=/sageMetadata/genreLevels:2 --showProperties
```

If you run the output of the above command, you should see that the value of /sageMetadata/genreLevels is now set to 2.  This is not a permanent change, but only for the current session.  If you want to make a property permanant, put it in the metadata.properties.