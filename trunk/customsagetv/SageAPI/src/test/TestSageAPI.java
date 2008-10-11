package test;

import java.io.File;

import sagex.SageAPI;
import sagex.api.Configuration;
import sagex.api.Global;
import sagex.api.MediaFileAPI;
import sagex.remote.SageAPIRemote;

/**
 * To enable the remote sage api, you need to add
 * sagex.remote.SageRPCServerRunner to the server property,
 * load_at_startup_runnable_classes
 * 
 * the sagex-api-6.3.10.jar needs to be in the JARs directory.
 * 
 * @author seans
 */
public class TestSageAPI {
	public static void main(String args[]) throws Exception {
		// we can explicitly set the remote server, or let it figure it out...
		SageAPI.setProvider(new SageAPIRemote("localhost", 9999));

		// what os is the remote server running...
		System.out.println("Remote Sage OS: " + Global.GetOS());

		// lets find all sage servers on the network (ie, what the extenders do
		// when they start up)
		System.out.println("Discovering Sage Servers on the network....");
		String servers[] = Global.DiscoverSageTVServers(1000);
		if (servers != null) {
			for (int i = 0; i < servers.length; i++) {
				System.out.println("Discovered Sage Server: " + servers[i]);
			}
		}

		// Simply media file test....
		Object files[] = MediaFileAPI.GetMediaFiles();
		if (files != null) {
			System.out.println("Got Files: " + files.length);
			Object mf = files[10];
			System.out.println("Title: " + MediaFileAPI.GetMediaTitle(mf));
			System.out.println("Runtime: " + MediaFileAPI.GetFileDuration(mf));
			System.out.println("ID: " + MediaFileAPI.GetMediaFileID(mf));
		}

		// simple call to tell us how much video we have..
		System.out.println("Total Video Duration: " + Global.GetTotalVideoDuration() / 3600);

		// dump out our media import paths on the server
		File[] libDirs = Configuration.GetLibraryImportPaths();
		if (libDirs != null) {
			for (File f : libDirs) {
				System.out.println("Library import dir: " + f.getAbsolutePath());
			}
		}

		// dump out a server config property
		System.out.println("Get Runnable Classes: " + Configuration.GetProperty("load_at_startup_runnable_classes", "Not Set"));

		System.out.println("Were Done.");
	}
}
