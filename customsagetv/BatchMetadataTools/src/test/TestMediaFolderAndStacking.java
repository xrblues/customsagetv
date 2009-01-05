package test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdna.media.IMediaResource;
import org.jdna.media.impl.CDStackingModel;
import org.jdna.media.impl.MediaFolder;
import org.jdna.media.impl.MovieResourceFilter;
import org.jdna.metadataupdater.MetadataUpdater;

public class TestMediaFolderAndStacking {
    public static void main(String args[]) throws IOException {
        // do this so that the configuration manager gets inited
        MetadataUpdater.initConfiguration();

        MediaFolder folder = new MediaFolder(new File("/home/FileServer/Media/Videos/Movies/").toURI());
        folder.setFilter(MovieResourceFilter.INSTANCE);
        folder.setStackingModel(CDStackingModel.INSTANCE);
        List<IMediaResource> l = folder.members();
        for (IMediaResource r : l) {
            System.out.printf("Movie: %s\n", r.getName());
            System.out.printf("Movie: %s\n", r.getTitle());
        }

        // testing stackings
        IMediaResource r = folder.getResource("wanted.2008.dvdrip.xvid-amiable.cd1.avi");
        if (r == null) {
            System.out.println("********* Get Stacked Resources failed");
        } else {
            System.out.println("Found Stacked Resources");
            System.out.printf("Movie: %s\n", r.getName());
            System.out.printf("Movie: %s\n", r.getTitle());
        }

        // test nested find
        r = folder.getResource("Watched/88 Minutes.avi");
        if (r == null) {
            System.out.println("******** Get Stacked Resources failed");
        } else {
            System.out.println("Found Stacked Resources");
            System.out.printf("Movie: %s\n", r.getName());
            System.out.printf("Movie: %s\n", r.getTitle());
        }
    }
}
