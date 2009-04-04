package test;

import java.io.File;
import java.io.IOException;

import org.jdna.media.IMediaResource;
import org.jdna.media.MediaResourceFactory;
import org.jdna.media.impl.MovieResourceFilter;

public class TestMediaFilter {
    public static void main(String args[]) throws IOException {
       IMediaResource res = MediaResourceFactory.getInstance().createResource(new File("/test4/test/test/movie.avi").toURI());
       MovieResourceFilter filter = new MovieResourceFilter("/test2/|/test3/");
       System.out.println("Movie: " + res.getLocationUri());
       System.out.println("Accepted: " + filter.accept(res));
    }
}
