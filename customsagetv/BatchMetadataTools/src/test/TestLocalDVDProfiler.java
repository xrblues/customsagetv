package test;

import java.util.List;

import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.impl.dvdproflocal.LocalDVDProfMetaDataProvider;
import org.jdna.metadataupdater.MetadataUpdater;

public class TestLocalDVDProfiler {
	public static void main(String args[]) throws Exception {
		MetadataUpdater.initConfiguration();

		IMediaMetadataProvider prov = new LocalDVDProfMetaDataProvider();
		List<IMediaSearchResult> results = prov.search(IMediaMetadataProvider.SEARCH_TITLE, "Batman Begins");

		TestUtils.dumpResults(results);
		
		if (results!=null && results.size()>0) {
			IMediaSearchResult res = results.get(0);
			TestUtils.dumpMetaData(MediaMetadataFactory.getInstance().getMetaData(res));
		}
	}
}
