package test;

import java.io.File;

import org.jdna.media.metadata.MetadataKey;

public class TestMisc {
    public static void main(String args[]) {
        File f = new File("/media/FileServer/Media/Videos/Magic Videos/");
        System.out.println(f.toURI().toString());

        // testing enums
        MetadataKey mdKey = MetadataKey.valueOf("ASPECT_RATIO");
        if (mdKey == null) {
            System.out.println("Not Aspect Ratio");
        } else {
            System.out.println("Got Aspect Ratio: " + (mdKey == MetadataKey.ASPECT_RATIO));
        }
        
        System.out.println("Background: " + GetBackgroundFilename("test.mine.avi"));
        
        String name = "te\\st *:\"(2008)?<>|.avi";
        name = name.replaceAll("[(\\\\/:\\*?\"<>|)]", "");
        System.out.println("Name: " + name);
    }
    
    private static String GetBackgroundFilename(String name) {
        String baseName = name.substring(0, name.lastIndexOf("."));
        return baseName + "_background.jpg";
     }
}
