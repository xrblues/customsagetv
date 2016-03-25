


# Introduction #

If you haven't done so, install the remote api services using the documentation on [this page](http://code.google.com/p/customsagetv/wiki/SageTVapi).

As of 6.5.9, a new api handler was introduced that is meant to supercede the previous xml and json handlers.

The new api handler, /sagex/api, supports 3 encoder formats, Xml, Json/JsonP, and Niel's Xml Format from his Webserver Project.

The orginal json/xml apis returned only references to sage objects which resulted in a large number of calls in order to completely access a sage object remotely.  The new api uses a serialization approach so that complete objects are returned in xml or json.

# Details #

The /sagex/api handler has documentation built into the handler.  If you call this handler from a browser without any arguments, it will return a page that shows the basic usage and the api documentation.  Using this api page you can search the api as well.

The basic format of the new api is as follows

```
http://server/sagex/api?c=SAGE_COMMD&1=arg1&2=arg2....&start=#&size=#&context=UI_CONTEXT&encoder=xml|json|nielm&jsoncallback=CALLBACK_FUNCTION
```

## Command Arguments ##
### c or command ###
The c or command argument is used to specify the Sage Command.  The command can be either just the command, such as **GetMediaFiles** or it can contain the API class as well, such as **MediaFileAPI.GetMediaFiles**.

When calling a command, you pass the command arguments by using numbers, starting at 1 for the first argument.

For exmaple, the following command requests all MediaFiles for type TV.
```
/sagex/api?c=GetMediaFiles&1=T
```

### context ###
for commands that require a ui context, then pass the context using this arg.

### start and size ###
For commands that return arrays, you can pass the **start** argument to only return elements starting at the **start** position.  Start is used in conjunction with the **size** argument.

**start** and **size** can be used to implement paging.  For example if your **size** is 50, then you can make repeated calls to the Command changing the **start** argument, by incrementing it by the **size**.

for, example if you want to page through the list of media files, you could do something like
```
/sagex/api?c=GetMediaFiles&start=0&size=50
/sagex/api?c=GetMediaFiles&start=50&size=50
/sagex/api?c=GetMediaFiles&start=100&size=50
/sagex/api?c=GetMediaFiles&start=150&size=50
```

The default size is 50.  A default size is implemented because all objects are serialized.  If you were to call GetMediaFiles with size=99999, then you'd run out of memory on the server.  For this reason a size cap is installed by default.

If you pass a size that is greater than the size of the array, then only the # of items in the array is returned.  So passing size=99999 will only return the # of item in the array, and not fill the rest with empty items.

### encoder ###
There are 3 encoders, xml, json, and nielm.

#### xml ####
Xml is the default encoder.  It is an Xml structure that mimics the Sage Object structure.  The root node of of the Xml call is 

&lt;Result&gt;

.  If the result is an array, then a **size** attribute will indicate the number of elements in the array.

Sample Xml
```
<Result size="33">
<MediaFile>    
    <MediaFileFormatDescription><![CDATA[MPEG2-PS[MPEG2-Video 1:1 720p@29.97fps, MP2/384Kbps@48kHz Stereo]]]></MediaFileFormatDescription>
    <Airing>    
    <IsWatched>false</IsWatched>
    <RecordingName><![CDATA[]]></RecordingName>
    <AiringChannelName><![CDATA[TOONC]]></AiringChannelName>
    <AiringChannelNumber><![CDATA[554]]></AiringChannelNumber>
    <AiringDuration>1800000</AiringDuration>
    <AiringStartTime>1222201800000</AiringStartTime>

    <AiringEndTime>1222203600000</AiringEndTime>
<Show>    <IsShowEPGDataUnique>true</IsShowEPGDataUnique>
    <ShowMisc><![CDATA[]]></ShowMisc>
    <ShowCategory><![CDATA[Children]]></ShowCategory>
    <ShowSubCategory><![CDATA[Animated]]></ShowSubCategory>
    <ShowDescription><![CDATA[Kaz learns that the Creatures of Perim have cracked the Chaotic Code and are getting ready to invade Earth.]]></ShowDescription>
    <ShowEpisode><![CDATA[Chaotic Crisis]]></ShowEpisode>
    <ShowExpandedRatings><![CDATA[]]></ShowExpandedRatings>

    <ShowParentalRating><![CDATA[]]></ShowParentalRating>
    <ShowRated><![CDATA[]]></ShowRated>
    <ShowDuration>0</ShowDuration>
    <ShowTitle><![CDATA[Chaotic]]></ShowTitle>
    <ShowYear><![CDATA[]]></ShowYear>
    <ShowExternalID><![CDATA[EP8664930037]]></ShowExternalID>
    <OriginalAiringDate>1203206400000</OriginalAiringDate>
    <PeopleInShow><![CDATA[]]></PeopleInShow>

<PeopleListInShow size="0"></PeopleListInShow><RolesInShow size="0"></RolesInShow>    <IsShowObject>true</IsShowObject>
    <IsShowFirstRun>false</IsShowFirstRun>
    <IsShowReRun>false</IsShowReRun>
    <ShowLanguage><![CDATA[]]></ShowLanguage>
</Show>
....
```

#### json and jsoncallback ####
The json reply directly mimics the default xml reply in that it returns the sage object structure, except that it uses json syntax instead of xml.


Sample Json Reply
```
{"Result":[{"Airing":{"ParentalRating":"","RealWatchedStartTime":1222637089663,"AiringID":1528709,"AiringChannelNumber":"554","ScheduleDuration":1800000,"AiringChannelName":"TOONC","TrackNumber":0,"ScheduleEndTime":1222203600000,"AiringRatings":[],"IsNotManualOrFavorite":true,"RealWatchedEndTime":1222637117879,"WatchedEndTime":1222201855624,"IsAiringHDTV":false,"IsWatched":false,"ExtraAiringDetails":"Closed Captioned, Stereo","AiringDuration":1800000,"IsWatchedCompletely":false,"ScheduleStartTime":1222201800000,"LatestWatchedTime":1222201858624,"IsFavorite":false,"RecordingName":"","IsAiringObject":true,"IsManualRecord":false,"Show":{"PeopleListInShow":[],"ShowSubCategory":"Animated","ShowRated":"","ShowExternalID":"EP8664930037","ShowCategory":"Children","IsShowObject":true,"ShowTitle":"Chaotic","ShowYear":"","IsShowFirstRun":false,"ShowEpisode":"Chaotic Crisis","IsShowEPGDataUnique":true,"ShowExpandedRatings":"","OriginalAiringDate":1203206400000,"ShowDuration":0,"ShowParentalRating":"","ShowLanguage":"","RolesInShow":[],"ShowMisc":"","ShowDescription":"Kaz learns that the Creatures of Perim have cracked the Chaotic Code and are getting ready to invade Earth.","IsShowReRun":false,"PeopleInShow":""},"WatchedStartTime":1222201800001,"ScheduleRecordingRecurrence":"","AiringEndTime":1222203600000,"RecordingQuality":"","WatchedDuration":55623,"IsDontLike":false,"AiringStartTime":1222201800000,"AiringTitle":"Chaotic"},"FileEndTime":1222203594660,"IsPictureFile":false,"IsLocalFile":true,"ParentDirectory":"/var/media/tv","IsCompleteRecording":true,"SegmentFiles":["/var/media/tv/Chaotic-ChaoticCrisis-1528709-0.mpg"],"MediaFileID":1563351,"MediaFileEncoding":"video0 Fair-H.264","MediaFileFormatDescription":"MPEG2-PS[MPEG2-Video 1:1 720p@29.97fps, MP2/384Kbps@48kHz..........
```

If you need to call the Sage from another domain, then you can use the jsonp syntax and pass a **jsoncallback** function.  If you pass the jsoncallback argument, you do not need to set the encoder to json, since it will be implied.  Using the jsoncallback means the rather than getting a standard json reply, then reply will be javascript that will call you jsoncallback funtion passing the json data into it as a parameter.


#### nielm ####
The nielm encoder will return the results using the SageXmlWriter tools that neil wrote for the Sage Web Server.  Note that not all SageCommands can be used with this encoder.  ie, if you were to call the GetOS command with encoder=nielm, then the command will be executed, but the reply will be an error stating that the encoder cannot handler a "string".  So, only use this encoder,if you know the return will be a Sage Object or a Sage Object Collection.  ie, GetMediaFiles will be fine, but not GetOS.

# Samples #
Get a List of Media Files
```
/sagex/api?c=GetMediaFiles
```

Refresh the Sage Media Library
```
/sages/api?c=RunLibraryImportScan&1=true
```

Return the same result as json
```
/sages/api?c=RunLibraryImportScan&1=true&encoder=json
```

Return the TV media files (first 10) in Niel's format
```
/sagex/api?c=GetMediaFiles&size=10&encoder=nielm
```



# Media Handler #
In addition to the api handler, there are a number of media handlers for fetching the media file, poster, background, banner and thumbnail.

You can get a help page for the media handler using
```
 /sagex/media
```

## Media Handler Format ##
```
 /sagex/media/COMMAND/MEDIA_ID
```

  * COMMAND - one of thumbnail, mediafile, poster, banner, background
  * MEDIA\_ID - MediaFile ID

The alternate format is
```
 /sagex/media/COMMAND?mediafile=MEDIAFILE_ID
```

## Phoenix APIs ##
If you have 1.26 or later of the Phoenix APIs installed, then you can user poster, banner, or background.  These will not work without the phoenix apis.

Also, if you have Phoenix APIs instaled, you can dynamically apply any image tranformation by passing it in as a **transform** arg.

ie,
```
 /sagex/media/poster?mediafile=2313223&transform=[{name:scale, height:100},{name:reflection}]
```

This will fetch the poster for the mediafile and scale the poster to 100px height, and then apply a reflection to the poster.

# Custom Services #
The Sage API Handler can also call custom services that you write using javascript.  If you write a services file, you can place it in the sagex/services/ directory and then you can access it using the api url.

for example, if you write a services file called **test.js** and put it in the **sagex/services** directory on the server, containing the following test code.
```
function TestHello(name) {
	return "Hello " + name;
}
```

You can now call this service using
```
sagex/api?c=test:TestHello&1=Bill
```

This will return
```
<result>Hello: Bill</result>
```

A Custom Service has complete access to All SageAPI calls. For example, if you added the following to your test.js file,
```
function GetAllTV() {
	return MediaFileAPI.GetMediaFiles("T");
}
```

You can then call it using
```
sagex/api?c=test:GetAllTV
```

This feature allows you add your own .js files to the server that will allow you to offload complex tasks to javascript.  A Service contains a FILENAME:METHOD where FILENAME is your javascript filename, less the .js extension, and METHOD is the method that you want to call from your javascript file.

If you are writing a plugin, then you automatically deploy your services by ensuring that your service file gets extracted into sagex/services.  Deploying a new service file or making changing to it, does not require a server restart.