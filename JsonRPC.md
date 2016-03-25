# Introduction #

As of 6.4.8-7, and thanks to jreichen, the api also contains a JSON/JSONP repsonse to a REST style request.

Please read the [Xml Guide](http://code.google.com/p/customsagetv/wiki/XmlRPCQuickStart), before continuing, because It will explain how to make REST calls to the SageTV Server.

The only change in making a REST call for a JSON/JSONP response, is that the url changes so that instead of **sagex/xmlRpc/** you use **sagex/jsonRpc/**

The response comming back will will either be a JSON or [JSONP](http://bob.pythonmac.org/archives/2005/12/05/remote-json-jsonp/) response depending on whether or not you pass a jsoncallback url parameter.  If you pass a jsoncallback url parameter, then the response comming back will be a JSONP repsonse.  [JSONP](http://bob.pythonmac.org/archives/2005/12/05/remote-json-jsonp/) is used to do cross domain mash ups.

# Full Example #
This [example](http://code.google.com/p/customsagetv/source/browse/trunk/customsagetv/SageAPI/html/test/testJson.html) requires [jquery](http://jquery.com/).

If you run this example, change the ajaxUrl variable to that of your server.  When you run it it should build a list of recorded shows.
```
<html>
	<head>
		<script src="jquery-1.2.6.min.js"></script>
		<script>
			function listRecordings() {
				var ajaxUrl = "http://mediaserver:8081/sagex/rpcJson";
				
				// call the GetMediaFiles/T to return all recorded shows
				$.getJSON(ajaxUrl + "/MediaFileAPI/GetMediaFiles/T?jsoncallback=?", function(data) {
					// update the recorded shows indicator
					$("#itemCount").text(data.body.arrayRef.size);
					
					// for each recorded show, get its title
					for (var i=0;i<data.body.arrayRef.size;i++) {
						$.getJSON(ajaxUrl + "/MediaFileAPI/GetMediaTitle/"+data.body.arrayRef.ref + ":" + i + "?jsoncallback=?", function(title) {
							$("#recordings").append("<li>"+ title.body.value +"</li>");
						});
					}
				});
			}
		</script>
	</head>
	<body>
		<a href="#listtitle" onclick="listRecordings()">List Recorded Shows</a>
		<div><label>Recording Count: </label><span id="itemCount">?</span></div>
		<ul id="recordings">
		</ul>
	</body>
</html>
```