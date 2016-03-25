

# Feature Set #
  * Complete access to the Sage API from Java
  * Java code can run remotely and access the Sage Server (Java RPC over RMI and HTTP)
  * Java code can run within the SageTV process
  * Java client can auto discover the running sage server for transparent access
  * Complete access to the Sage API via Rest services
  * Rest API can pass ui context
  * Java API now supports ui context
  * Can provide custom services via server side javascript
  * Can access media information, such as media files, posters, thumbnails, backgrounds, etc.

Check out the [newer Api format](http://code.google.com/p/customsagetv/wiki/RemoteHttpApiForSageTV) which includes support for a variety of API options, media handlers, services, etc.

# Background #

SageTV documents their [API](http://download.sage.tv/api/) but unfortunately, they don't provide an API that matches their documentation.  Instead, in order to use their API, you need to call the sage service and pass in the named SageTV Api method.  This unfortunately leads to typos, and if you are using a modern IDE, then there is no auto completion.

[Greg](http://forums.sagetv.com/forums/member.php?u=5484), a very competent SageTV developer, solved this API problem by creating a new [API](http://forums.sagetv.com/forums/downloads.php?do=file&id=128) that mirrored SageTV's intention.

# Why create another API? #

[Greg](http://forums.sagetv.com/forums/member.php?u=5484), a very competent SageTV developer, solved this API problem by creating a new [API](http://forums.sagetv.com/forums/downloads.php?do=file&id=128) that mirrored SageTV's intention.
I needed it to do more, and something different, that the existing APIs did not support.

I wanted to be able to access the SageTV api remotely, primarily in Java, but also using http.

I wanted the API to reflect what SageTV had documented.  In other words, I wasn't looking to create wrapper objects, etc, I just wanted to be able to use the API as SageTV had documented it.

This new api achieves all of this, and more.

# What makes it really different? #
I took a 3 tier approach to this API.  Despite the 3 tiers, there isn't that much to it.  The first tier consists of a set of Static Classes and Methods that is auto generated from the SageTV javadoc.

The generated Methods simply call a core static method
```
   sagex.SageAPI.call(String name, Object[] args);
```

So instead of calling SageTV directly, I call another layer.

The SageAPI layer accepts an ISageAPIProvider instance that is responsible for doing the real work.  SageAPI has a setProvider() method where you can explicitly set the provider to use for the global instance, or if none is set, then it will attempt to find the best known provider.

One very interesting capability of using this API is that code written using this API can run inside the SageTV Process or Externally without code changes or recompiling.  This is one of the primary reasons why I created the API.  I have some projects that I'm working on, but I don't want to litter the SageTV jvm with these processes.  Now I an host these processes externally in another JVM.  I have process isolation, so that unstable code that I might write won't cause the SageTV process to fail.

## API Provider Implementations ##

### EmbeddedSageAPIProvider ###
This provider is the provider that is used when the API is embedded into a running SageTV process.  This api is simply a pass through to the actual SageTV api as is.  There is no marshalling and unmarshalling of data, so it's very efficient at handling SageTV requests without creating a lot of extra objects.

### Remote API Providers ###
There are 2 remote Api providers installed by default.  The default one is an RMI provider that will communicate with the sage server over RMI.  When you run client code, and you do not force a remote provider, then it will use RMI.

The second provider is a Http Provider.  This provider has some issues when running in Neil's web server.  (It's a limitation of the underlying server, not Neil's code).  In Jetty this Http provider works ok.

Here is a list of current http enabled providers
  * /sagex/api - New API Handler
  * /sagex/media - New Media Handler

#### Sage7: Installation ####
Use the plugin manager and install the "sagex-services".  It will install the Jetty web server, and configure and enable the RMI and Http services.

#### Sage6: Installation for Niel's Web Server ####
To install this Provider....
  * Install [Nielm's Webserver Plugin](http://forums.sagetv.com/forums/downloads.php?do=file&id=26)
  * Stop Sage Server
  * Copy the sagex-api.VERSION.jar to the SAGE\_HOME/JARs dir
  * Add/edit the following in webserver/servlet.properties
```
# Sage RPC Api
servlet./sagex.code=sagex.remote.SagexServlet
```

  * Start Sage Server

The Sage RPC server will start up and listen for requests on the same port as the webserver.

Your remote client code will also require that the sagex-api.VERSION.jar is in it's classpath.

In your client code, you simply need to invoke the SageTV api and it should find the remote server automatically.
```
		// Simply media file test....
		Object files[] = MediaFileAPI.GetMediaFiles();
		if (files!=null) {
			System.out.println("Got Files: " + files.length);
			Object mf = files[0];
			System.out.println("Title: " + MediaFileAPI.GetMediaTitle(mf));
			System.out.println("Runtime: " + MediaFileAPI.GetFileDuration(mf));
			System.out.println("ID: " + MediaFileAPI.GetMediaFileID(mf));
		}
```

#### Sage6: Installation for Jetty Server ####

  * Install Jreichen's [Jetty Plugin](http://forums.sagetv.com/forums/downloads.php?do=file&id=233)
  * Install the sagex-api-VERSION.zip file into the SAGE\_HOME directory
  * restart sage

### StubSageAPIProvider ###
This is simply a testing stub.  It does nothing.  It can't be used for anything, but it does allow for some offline testing.

# Technical Details #
The RPC server uses and existing Servlet engine and a UDP server.  The client, if it's not specifically configured with a RemoteSageAPIProvider will querry the server for it's address using udp.  It sends a message on the group 228.5.6.7 on the port 9998.  The RPC server sends back it's url to the client.  If you have more than one Sage server on the network, running the sagex-services plugin, then the client will choose the first available server, and communicate with it.

# Contributions #
If you want to work on the project, or feel that something is missing.  Send me a message.

## Taglib Generator ##
Jreichen has created a set of sage api tags using the parser from this project.  I'm assuming he'll be releasing those with his Jetty Plugin.