package test;

import sagex.SageAPI;
import sagex.api.Global;
import sagex.api.MediaFileAPI;
import sagex.remote.rmi.RMISageAPI;

public class TestSageMediaFileAPI {
    public static void main(String args[]) {
    	// connect to your server
    	SageAPI.setProvider(new RMISageAPI("mediaserver"));
    	
    	// call an api to see what your server address would be
    	System.out.println(Global.GetServerAddress());
    	
    	// get a list of mediafiles on the server and dumpe the first 10
    	int i=0;
    	Object[] mediafiles = MediaFileAPI.GetMediaFiles();
    	for (Object mf: mediafiles) {
    		System.out.println("Show: " + MediaFileAPI.GetMediaTitle(mf));
    		if (i++>10) break;
    	}
    	
    	System.out.println("DONE");
    }
}
