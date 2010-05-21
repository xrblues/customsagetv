package org.jdna.metadataupdater;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BMT upgrade script.  Used to clean up jars, etc.
 * 
 * @author seans
 *
 */
public class Upgrade {
	private String[] JARs = new String[] {
		"lucene-core-2.4.1.jar",
		"lucene-cor.jar",
		"commons-codec.jar",
		"commons-dbutils.jar",
		"commons-io.jar",
		"commons-lang.jar",
		"easymock.jar",
		"easymock-2.5.2.jar",
		"h2.jar",
		"junit.jar",
		"junit-4.7.jar",
		"htmlparser.jar",
		"log4j.jar",
		"sage-plugin.jar",
		"commons-net-1.4.1.jar",
		"sagex.api.jar"
	};
	
	private String[] RegexJARs = new String[] {
		"metadataupdater-",
		"sagex.api-"
	};
	
	public void upgrade() {
		File dir = new File("JARs");
		if (!dir.exists()) {
			System.out.println("You Must run this from the SAGE TV HOME directory and not: " + dir);
			return;
		}
		
		for (String jar : JARs) {
			File f = new File(dir, jar);
			if (f.exists()) {
				if (!f.delete()) {
					System.out.printf("Can't remove jar: %s; Please make sure Sage is stopped and try again, or manually remove the file.\n",f);
				} else {
					System.out.printf("Removed jar: %s\n", f);
				}
			}
		}
		
		for (String pat : RegexJARs) {
			cleanJars(dir, pat);
		}
	}
	
    public static void cleanJars(File libDir, String pattern) {
        Pattern p = Pattern.compile(pattern);
        if (libDir.exists()) {
            for (File f : libDir.listFiles()) {
                Matcher m = p.matcher(f.getName());
                if (m.find()) {
                    if (!f.delete()) {
                    	System.out.printf("Cannot remove jar: %s; Please make sure Sage is stopped and try again, or manually remove the file.\n", f);
                    } else {
    					System.out.printf("Removed jar: %s\n", f);
                    }
                }
            }
        }
    }
	
	public static void main(String args[]) {
		Upgrade upgrade= new Upgrade();
		upgrade.upgrade();
	}
}
