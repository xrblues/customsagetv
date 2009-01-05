package test;

import java.io.IOException;

import org.jdna.metadataupdater.MetadataUpdater;
import org.jdna.url.IUrl;
import org.jdna.url.UrlFactory;

public class TestCachedUrl {
    public static void main(String args[]) throws IOException {
        MetadataUpdater.initConfiguration();

        IUrl u = UrlFactory.newUrl("http://www.google.ca/");
        System.out.printf("Url: %s; Moved: %s\n ", u.getUrl(), u.hasMoved());

    }
}
