import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

/**
 * Simple Launcher that automatically builds up the Classpath and invokes the
 * main class. This negates the need to write a bat/cmd/sh script for each
 * platform.
 * 
 * You invoke it by
 * 
 * <pre>
 * # java [-DMetadataTool.debug=true|false] MetadataTool [OPTIONS]
 * </pre>
 * 
 * @author seans
 * 
 */
public class MetadataTool {
    public static final String DEFAULT_MAIN_CLASS = "org.jdna.metadataupdater.MetadataUpdater";
    public static final String HOME_DIR_VAR       = "METADATA_UPDATER_HOME";
    public static boolean      debug              = false;

    /**
     * @param args
     */
    public static void main(String[] args) {
        debug = Boolean.getBoolean("MetadataTool.debug");

        String mainClass = System.getProperty("jdna.MainClass", DEFAULT_MAIN_CLASS);

        if (debug) System.out.println("Using main class: " + mainClass);

        // Read and set the default HOME from which we will base all other
        // relative reads in order to setup the program
        String home = System.getenv(HOME_DIR_VAR);
        if (home == null) {
            home = ".";
        }

        File homeDir = null;
        try {
            homeDir = new File(home).getCanonicalFile();
        } catch (IOException e2) {
            System.out.println("Failed to set home dir: " + home);
            System.exit(1);
        }

        if (debug) System.out.println("Assuming Home Dir: " + homeDir.getAbsolutePath());

        if (!homeDir.exists() && !homeDir.isDirectory()) {
            System.out.printf("Home Dir: %s does not exist, or is not a valid directory.\n", home);
            System.exit(1);
        }
        // build a list of urls for the classpath
        java.util.List<URL> urls = new ArrayList<URL>();

        // add in bin dir, if it exists
        File defClassDir = new File(homeDir, "bin");
        if (defClassDir.exists()) {
            if (debug) System.out.println("Adding Bin dir: " + defClassDir.getAbsolutePath());
            try {
                urls.add(defClassDir.toURI().toURL());
            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                if (debug) e1.printStackTrace();
            }
        }

        // add in each jar in the libs
        File libDir = new File(homeDir, "libs");
        File libs[] = libDir.listFiles();

        for (File lib : libs) {
            try {
                if (lib.getName().endsWith(".jar")) {
                    if (debug) System.out.println("Adding Lib: " + lib.getAbsolutePath());
                    urls.add(lib.toURI().toURL());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        // create the classloader using our found jars
        URLClassLoader cl = new URLClassLoader(urls.toArray(new URL[urls.size()]), MetadataTool.class.getClassLoader());
        if (debug) {
            for (URL u : cl.getURLs()) {
                System.out.printf("ClassLoader Url: %s\n", u.toExternalForm());
            }
        }

        Class run = null;
        try {
            run = cl.loadClass(mainClass);
        } catch (ClassNotFoundException e) {
            System.out.printf("Failed to Find Class: %s\nPlease set the %s environment variable.\n", mainClass, HOME_DIR_VAR);
            System.exit(1);
        }

        // run the main method
        Method m = null;
        try {
            m = run.getMethod("main", new Class[] { String[].class });
        } catch (Exception e) {
            System.out.println("Failed to find a valid main() method for class: " + mainClass);
            System.exit(1);
        }

        try {
            if (debug) System.out.printf("Invoking with %d args\n", args.length);
            m.invoke(null, (Object) args);
        } catch (Exception e) {
            System.out.println("Error Launching Class: " + mainClass);
            e.printStackTrace();
            System.exit(1);
        }

        if (debug) System.out.println("Command completed without error.");
        System.exit(0);
    }
}
