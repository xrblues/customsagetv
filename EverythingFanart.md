<dl>
<dt>What are the Phoenix APIs?</dt>
<dd>
The Phoenix APIs is a set of Fanart/Metadata apis that make is easier for STV developers to build Fanart Support into their STV plugins.<br>
</dd>

<dt>Why build a library for this?</dt>
<dd>
A library makes sense, since the logic to "discover" Fanart was getting overly complex, and each plugin had to reproduce the logic in each plugin, in order to support it.  Using a library (api) means that STV developers call use a single consistent api call to fetch metadata for a given media file.<br>
</dd>

<dt>What is required to use the new Phoenix APIs to it's full potential</dt>
<dd>
Latest Beta of SageTV (TODO: Find Version)<br>
Latest SageMC (TODO: Find Version)<br>
Metadata Tools 2.0 or later<br>
Evilpenquin's MediaScraper Tools (TODO: Find Version)<br>
</dd>

<dt>What uses the new Phoenix Fanart APIs</dt>
<dd>
SageMC<br>
Metadata Tools<br>
Evilpenquins MediaScraper supports the File Structure and the Metadata Properties, but it doesn't require the actual phoenix jar file<br>
</dd>

<dt>How do I enable Fanart in the (STV) UI?</dt>
<dd>
See the SageMC fanart guide<br>
<br>
or<br>
<br>
Import the STVi for SageMC or the Default STV and then enable Fanart in the Setting Screen and set the Central Fanart Folder.<br>
</dd>

<dt>What is the new folder layout?</dt>
<dd>
TV/SeriesName/Backgrounds<br>
TV/SeriesName/Posters<br>
TV/SeriesName/Banners<br>
TV/SeriesName/Season #/Backgrounds<br>
TV/SeriesName/Season #/Posters<br>
TV/SeriesName/Seaons #/Banners<br>
<br>
Movies/Title/Backgrounds<br>
Movies/Title/Posters<br>
Movies/Title/Banners<br>
<br>
Music/Album/Backgrounds<br>
Music/Album/Posters<br>
Music/Album/Banners<br>
<br>
Music/Artist/Backgrounds<br>
Music/Artist/Posters<br>
Music/Artist/Banners<br>
</dd>

<dt>I don't like the new folder layout, Can I use the old SageMC format?</dt>
<dd>
Yes, simply set the follow property in your Sage properties, and it will use the SageMC legacy format for looking up Fanart.<br>
TODO: Add property<br>
</dd>

<dt>If I use the Old SageMC format will the scraper tools still work?</dt>
<dd>
It was decided that the scraper tools will only support the new folder structure.  So if you opt to use the legacy SageMC format, then the scaper tools will not put the fanart in the SageMC legacy directories.<br>
</dd>

<dt>I don't want to use the new format or the SageMC format, Can I use have simple background image?</dt>
<dd>
Yes. Simply enable the LocalFanartStorage by setting the following property in your Sage properties.<br>
TODO: Add Property<br>
<br>
This will enable the phoenix apis to find a single poster and a single background in the current directory of the Media item.<br>
<br>
But it will not find anything for a SageTV Airing (ie, a guide entry)<br>
</dd>


<dt>When I download Fanart using the UI Search/Select Tools, is it supposed to download the metadata as well?</dt>
<dd>
Currently SageTV does not suppport updating metadata after a mediafile has been imported.   Because of this, the Search/Select tools that are provided in the STV plugin will only fetch fanart and not metadata.<br>
</dd>

<dt>What do I do when I have problems?</dt>
<dd>
1. Read the FAQ<br>
2. Run the diagnostic tool<br>
3. Search the forums<br>
4. Post for help in the forums<br>
</dd>

<dt></dt>
<dd></dd>

<dt></dt>
<dd></dd>

<dt></dt>
<dd></dd>

</dl>