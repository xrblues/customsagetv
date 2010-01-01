package test;

import java.io.File;

import org.apache.log4j.BasicConfigurator;

import sagex.api.Configuration;

public class TestDVDFailing {
    public static void main(String args[]) {
        BasicConfigurator.configure();

        
        add();
        //remove();
    }

    public static void add() {
        String dir = "/home/FileServer/Media/Videos/VideoCollection/Movies/DVD2";
        Configuration.AddVideoLibraryImportPath(dir);
        
        File paths[] = Configuration.GetVideoLibraryImportPaths();
        for (File f : paths) {
            System.out.println("Dir: " + f.getAbsolutePath());
        }
    }

    public static void remove() {
        String dir = "/home/FileServer/Media/Videos/VideoCollection/Movies/DVD2";
        Configuration.RemoveVideoLibraryImportPath(new File(dir));
        
        File paths[] = Configuration.GetVideoLibraryImportPaths();
        for (File f : paths) {
            System.out.println("Dir: " + f.getAbsolutePath());
        }
    }
}
