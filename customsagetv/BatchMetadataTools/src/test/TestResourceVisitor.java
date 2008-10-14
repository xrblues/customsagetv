package test;

import java.io.File;
import java.io.IOException;

import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IResourceVisitor;
import org.jdna.media.MediaResourceFactory;
import org.jdna.media.impl.CDStackingModel;
import org.jdna.media.impl.MovieResourceFilter;
import org.jdna.media.metadata.VideoMetaDataFactory;
import org.jdna.media.util.MissingMetadataVisitor;
import org.jdna.metadataupdater.MetadataUpdater;

public class TestResourceVisitor {
	public static void main(String args[]) throws IOException {
		// do this so that the configuration manager gets inited
		MetadataUpdater.initConfiguration();

		IMediaFolder folder = (IMediaFolder) MediaResourceFactory.getInstance().createResource(new File("/media/FileServer/Media/Videos/Movies/").toURI().toString());
		folder.setFilter(MovieResourceFilter.INSTANCE);
		folder.setStackingModel(CDStackingModel.INSTANCE);
		MissingMetadataVisitor mdv = new MissingMetadataVisitor(VideoMetaDataFactory.getInstance().getDefaultPeristence(), new IResourceVisitor() {
			public void visit(IMediaResource resource) {
				System.out.printf("Missing Metadata for: %s\n", resource.getTitle() );
			}
		});
		
		folder.accept(mdv);
		
		System.out.printf("Found %s missing.\n", mdv.getMissingMetadata().size());
	}
}
