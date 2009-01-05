package test;

import java.io.File;
import java.io.IOException;

import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.MediaResourceFactory;
import org.jdna.media.util.CollectorResourceVisitor;
import org.jdna.media.util.CompositeResourceVisitor;
import org.jdna.media.util.MissingMetadataVisitor;
import org.jdna.metadataupdater.MetadataUpdater;

public class TestResourceVisitor {
    public static void main(String args[]) throws IOException {
        // do this so that the configuration manager gets inited
        MetadataUpdater.initConfiguration();

        IMediaFolder folder = (IMediaFolder) MediaResourceFactory.getInstance().createResource(new File("/media/FileServer/Media/Videos/Movies/").toURI().toString());
        CollectorResourceVisitor crv = new CollectorResourceVisitor();
        MissingMetadataVisitor mdv = new MissingMetadataVisitor(new CompositeResourceVisitor(new IMediaResourceVisitor() {
            public void visit(IMediaResource resource) {
                System.out.printf("Missing Metadata for: %s\n", resource.getTitle());
                if (resource.getType() == IMediaFile.TYPE_FILE) {
                    System.out.println("Is Stacked: " + ((IMediaFile) resource).isStacked());
                }
            }
        }, crv));

        folder.accept(mdv);

        IMediaFolder mf = MediaResourceFactory.getInstance().createVirtualFolder("testFolder", crv.getCollection());

        System.out.printf("Found %s missing.\n", mf.members().size());
    }
}
