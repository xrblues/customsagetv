package test;

import sagex.SageAPI;
import sagex.api.Global;
import sagex.api.MediaFileAPI;
import sagex.api.MediaNodeAPI;
import sagex.remote.rmi.RMISageAPI;

public class TestSageMediaFileAPI {
    public static void main(String args[]) {
    	SageAPI.setProvider(new RMISageAPI("seans-desktop"));
    	System.out.println(Global.GetServerAddress());
    	
    	Object node = MediaNodeAPI.GetMediaSource("FileSystem");
    	if (MediaNodeAPI.IsNodeFolder(node)) {
    		System.out.println("Folder: " + MediaNodeAPI.GetNodePrimaryLabel(node));
    		Object nodes[] = MediaNodeAPI.GetNodeChildren(node);
    		if (nodes!=null) {
    			for (Object o: nodes) {
    				if (MediaNodeAPI.IsNodeFolder(o)) {
    					System.out.println("Folder: " + MediaNodeAPI.GetNodePrimaryLabel(o));
    				} else {
    					System.out.println("Item: " + MediaNodeAPI.GetNodePrimaryLabel(o));
    				}
    			}
    		}
    	}

    	System.out.println("================================");
    	node = MediaNodeAPI.GetMediaSource("VideosByFolder");
    	if (MediaNodeAPI.IsNodeFolder(node)) {
    		System.out.println("Folder: " + MediaNodeAPI.GetNodePrimaryLabel(node));
    		Object nodes[] = MediaNodeAPI.GetNodeChildren(node);
    		if (nodes!=null) {
    			for (Object o: nodes) {
    				if (MediaNodeAPI.IsNodeFolder(o)) {
    					System.out.println("Folder: " + MediaNodeAPI.GetNodePrimaryLabel(o));
    					System.out.println("    --: " + MediaNodeAPI.GetNodeFullPath(o));
    				} else {
    					System.out.println("Item: " + MediaNodeAPI.GetNodePrimaryLabel(o));
    					System.out.println("  --: " + MediaNodeAPI.GetNodeDataType(o));
    					System.out.println("  --: " + MediaFileAPI.GetMediaTitle(o));
    				}
    			}
    		}
    	}

    	
    	
    	System.out.println("DONE");
    }
}
