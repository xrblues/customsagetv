package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.jdna.util.StoredStringSet;

public class TestStoredSet {
    public static void main(String args[]) throws FileNotFoundException, IOException {
        StoredStringSet set = new StoredStringSet();
        File f = new File("target/testset");
        if (f.exists()) {
            set.load(new FileReader(f));
        }
        if (set.contains("myimage.jpg")) {
            System.out.println("Has mymage.jpg");
        } else {
            System.out.println("Adding mymage.jpg");
            set.add("myimage.jpg");
            set.store(new FileWriter(f), "Testing");
        }
    }
}
