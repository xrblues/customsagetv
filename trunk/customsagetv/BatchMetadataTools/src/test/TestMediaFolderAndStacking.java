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
		
		MediaFolder folder = new MediaFolder(null, new File(args[0]));
		folder.setFilter(MovieResourceFilter.INSTANCE);
		folder.setStackingModel(CDStackingModel.INSTANCE);
		List<IMediaResource> l = folder.members();
		for (IMediaResource r : l) {
			System.out.printf("Movie: %s\n", r.getTitle());
		}
	}
}
