package test;

import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.metadataupdater.ListMovieVisitor;

public class TestMetadataProvider {
    public static void main(String args[]) throws Exception {
        String url = "http://www.imdb.com/title/tt0277296/";
        String id = "imdb.xml";
        
        IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().getProvider(id);
        System.out.println("Using Provider: " + prov.getInfo().getId());
        IMediaMetadata md = prov.getMetaData(url);
        ListMovieVisitor.printMetadata(md, "Movie", url);
    }
}
