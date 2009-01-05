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
    }
}
