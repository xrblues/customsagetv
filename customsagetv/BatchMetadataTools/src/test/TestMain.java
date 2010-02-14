package test;

import java.io.File;
import java.net.URL;

import org.apache.log4j.BasicConfigurator;
import org.jdna.metadataupdater.MetadataUpdater;


public class TestMain {

    public TestMain() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        
        URL u = new URL("file:/tmp/a.txt");
        File f = new File(u.toURI());
        System.out.println("File: " + f);
        
        File f2 = new File("/tmp/a.txt");
        System.out.println("Equals: " + f.equals(f2) + "; " + (f == f2));
    }

}
