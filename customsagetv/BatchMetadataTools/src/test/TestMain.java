package test;

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
        String file = "/home/seans/DevelopmentProjects/workspaces/sage/MovieMetadataUpdater/target/sage/../../testing/Movies/Transformers 2 (2009).avi";
        MetadataUpdater.main(new String[] {file});
    }

}
