package test;

import java.io.File;

import org.jdna.media.metadata.MetadataKey;

public class TestMisc {
    public static void main(String args[]) {
        String s = "org.jdna.MyClass;org.jdna.YourClass";
        
        System.out.println(s);
        System.out.println(s.replaceAll(";org.jdna.MyClass", ""));
        System.out.println(s.replaceAll("org.jdna.MyClass;", ""));
    }
}
