package org.jdna.bmt.web.server;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.BasicConfigurator;
import org.jdna.bmt.web.client.ui.BatchOperation;
import org.jdna.bmt.web.client.ui.BatchOperations;

import sagex.SageAPI;
import sagex.phoenix.Phoenix;
import sagex.util.Log4jConfigurator;

public class ServicesInit {
    private static boolean initialized = false;

    public static void init() {

        if (!initialized) {
            initialized = true;
            
    		Log4jConfigurator.configureQuietly("bmtweb", ServicesInit.class.getClassLoader());

            if (SageAPI.isRemote()) {
                BasicConfigurator.configure();
                System.out.println("*** USING TEST PHOENIX DIR ****");
                
                try {
                	File phoenixDir = new File("../../Phoenix");
                	if (!phoenixDir.exists()) {
                		phoenixDir = new File("../Phoenix");
                	}
                	
                	if (!phoenixDir.exists()) {
	                	String phoenixSrc = System.getProperty("PHOENIX_SRC", System.getenv("PHOENIX_SRC"));
	                    System.out.println("*** PHOENIX_SRC: " + phoenixSrc);
	                    if (phoenixSrc == null) {
	                    	System.out.println("*** Need to PHOENIX_SRC property to the Phoenix Project Src ** ");
	                    	return;
	                    }
	                    phoenixDir = new File(phoenixSrc);
                	}
                	
                    File phoenix = new File(phoenixDir, "src/main/STVs/Phoenix/");
                    if (!phoenix.exists()) {
                    	throw new IOException("Invalid Phoenix Dir: " + phoenix);
                    }
                    
                    System.out.println("*** BEGIN Copying Phoenix files for Testing from " + phoenix);
                    FileUtils.copyDirectory(phoenix, new File("testing/Phoenix/"), new FileFilter() {
						@Override
						public boolean accept(File pathname) {
							if (pathname.getName().endsWith(".svn")) return false;
							return true;
						}
					});
                    
                    
                    // now copy from phoenix UI area
                	phoenixDir = new File("../../PhoenixUI");
                	if (!phoenixDir.exists()) {
                		phoenixDir = new File("../PhoenixUI");
                	}
                	
                    phoenix = new File(phoenixDir, "STVs/Phoenix/");
                    if (!phoenix.exists()) {
                    	System.out.println("** NO PHOENIX UI **");
                    } else {
                        System.out.println("*** BEGIN Copying Phoenix files for Testing from " + phoenix);
                        FileUtils.copyDirectory(phoenix, new File("testing/Phoenix/"), new FileFilter() {
    						@Override
    						public boolean accept(File pathname) {
    							if (pathname.getName().endsWith(".svn")) return false;
    							return true;
    						}
    					});
                    }
                    
                    System.out.println("*** END Copying Phoenix files for Testing.... ***");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                System.setProperty("phoenix/homeDir", "testing/Phoenix/");

                try {
                    // force bmt load the services, normally done using the plugin, but since we are running remotely, we need to do it.
                    Phoenix.getInstance().initServices();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            
            // initialize the batch operations, see if they are valid
            for (BatchOperation bo : BatchOperations.getInstance().getBatchOperations()) {
            	try {
            		GlobalServicesImpl.createVisitor(bo);
            	} catch (Throwable t) {
            		bo.setLabel("** BROKEN ** " + bo.getLabel());
            		t.printStackTrace();
            	}
            }
        }
    }
}
