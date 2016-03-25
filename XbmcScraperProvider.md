# Introduction #

The great folks over at the [Xbmc project](http://xbmc.org/) have put a lot of effort into build a scraping language for fetching metadata.  I had considered writing my own, but since they have done such a great job, it was simply easier for me to write a processor that would consume their [xml files](http://xbmc.svn.sourceforge.net/viewvc/xbmc/trunk/XBMC/system/scrapers/video/).

# Details #

Xbmc scrapers are located in the METADATA\_HOME/scrapers/xbmc/video directory.  On startup, the application will scan that directory for scrapers and load them.  Using the --listProviders will show all available providers, including the ones loaded from the scrapers directory.

For the latest Xbmc scrapers, check out their [scraper files](http://xbmc.svn.sourceforge.net/viewvc/xbmc/trunk/XBMC/system/scrapers/video/).

To use the imdb scraper from the Xbmc scraper project, you can use --provider=imdb.xml

```
# java -jar MetadataTool.jar --provider=imdb.xml FOLDER
```

All Xbmc scrapers will be assigned an ID that matches their filename.  So imdb.xml is a file that exists in the scrapers/xbmc/video directory.

# Xbmc Scraper Settings #
Some xbmc scrapers have a settings that you can override.  If the look at the xml file, usually at the top, you will see a 

&lt;settings&gt;

 element with some child elements.  Each element has an id, and that id is the setting id.

For the imdb.xml, there a a number of settings that you can override.  To override a setting, you can pass use the --setProperty switch.
```
# java -jar MetadataTool.jar --provider=imdb.xml --setProperty=/xbmcScraper/imdb.xml/settings/fullcredits:true
```

I won't explain the settings here, so if you want to know what they are, you'll have to inspect the xml files directly.  Just know, that if there is a setting, then you can set it's value via the command line or the metadata.properties file.

# Scraper Development #
You can check out the [Xbmc Scraper wiki](http://xbmc.org/wiki/?title=Scraper.xml) for information on how the build your own scrapers.

If you build new scrapers, or fix bugs in the existing ones, then you should post your fixes to the Xbmc team.