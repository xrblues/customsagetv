package test;

import java.util.List;

import org.jdna.media.metadata.IVideoMetaDataProvider;
import org.jdna.media.metadata.IVideoSearchResult;
import org.jdna.media.metadata.VideoMetaDataFactory;
import org.jdna.media.metadata.impl.dvdproflocal.LocalDVDProfMetaDataProvider;
import org.jdna.metadataupdater.MetadataUpdater;

public class TestLocalDVDProfiler {
	public static void main(String args[]) throws Exception {
		MetadataUpdater.initConfiguration();

		IVideoMetaDataProvider prov = new LocalDVDProfMetaDataProvider();
		List<IVideoSearchResult> results = prov.search(IVideoMetaDataProvider.SEARCH_TITLE, "Batman Begins");

		TestUtils.dumpResults(results);
		
		if (results!=null && results.size()>0) {
			IVideoSearchResult res = results.get(0);
			TestUtils.dumpMetaData(VideoMetaDataFactory.getInstance().getMetaData(res));
		}
	}
}
