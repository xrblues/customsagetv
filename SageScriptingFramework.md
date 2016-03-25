# Introduction #

This project started because I needed an excuse to try out the new [Scripting Engine](http://java.sun.com/developer/technicalArticles/J2SE/Desktop/scripting/) that ships with Java6.  Java6 now includes a Javascript engine that can be used to add scripting your java applications.  Not everyone is a Java programmer, but now you can use the best of both worlds.

The Sage Scripting Framework, is a simple scripting framework, that relies on Java6 and it's built in [Scripting Engine](http://java.sun.com/developer/technicalArticles/J2SE/Desktop/scripting/).  I've also included [python](http://www.jython.org/Project/) and [groovy](http://groovy.codehaus.org/) engines as well.  So, out of the gate, you can start writing scripts for SageTV using Javascript, Python, or Groovy.

The Sage Scripting Framework does not run inside SageTV and it is not meant to be a scripting framework for writing SageTV plugins.  It's purpose is to allow scripters to write command line scripts for SageTV.  Since this does not run inside the SageTV JVM, you MUST have the [Sage Remote APIs](http://code.google.com/p/customsagetv/wiki/SageTVapi) installed and configured.

# What Would I do with this? #
In the scripts folder, I've includes 3 sample sample scripts.  A Javascript script, python script and a Groovy script.

The python and groovy scripts don't do anything special, except show the OS on which your Sage Server is running.  Not too impressive, but it's meant to show how you access the [Sage Remote APIs](http://code.google.com/p/customsagetv/wiki/SageTVapi) from each scripting language.

To do any special, you will need to know how to use the [Sage TV API](http://download.sage.tv/api/index.html).  You can pretty much do anything that they expose in their API.

In the Javascript script, I go a little further, and I dump out the title and the episode name for all Recorded Shows that have NOT been watched.

The possibilities of what you can do, are endless.  You can write scripts that find all media files that are of a particular type and then add a transcode job to transcode them into an ipod format.  Because you have complete access (for the most part) to the [SageTV API](http://download.sage.tv/api/index.html), then with a little scripting knowledge, you can do just about anything.

# Details #
To Run your scripts, you should put your script in the scripts directory, and then use the following command
```
# java -jar ssf.jar scripts/yourScript.js
```

For example, to run the testSage.js script, you would use..
```
# java -jar ssf.jar scripts/testSage.js
```

You must be in the root folder of the extracted Sage Scripting Framework folder when you execute your scripts.

If you get a script error, then fix and re-run your script.

# Passing Arguments to your Scripts #
Each script gets passed 2 global variables; SCRIPT\_NAME and SCRIPT\_ARGS.

SCRIPT\_NAME is the name of your script, as it was passed on the command line.

SCRIPT\_ARGS is an array of command line arguments that your script can access.