package test;

import java.util.List;

import org.jdna.media.metadata.IVideoMetaDataProvider;
import org.jdna.media.metadata.IVideoSearchResult;
import org.jdna.media.metadata.VideoMetaDataFactory;
import org.jdna.media.metadata.impl.dvdprof.DVDProfMetaDataProvider;
import org.jdna.metadataupdater.MetadataUpdater;

public class TestDVDProfiler {
	public static void main(String args[]) throws Exception {
		MetadataUpdater.initConfiguration();

		DVDProfMetaDataProvider prov = new DVDProfMetaDataProvider();
		List<IVideoSearchResult> results = prov.search(IVideoMetaDataProvider.SEARCH_TITLE, "Sharkboy");
		//List<IVideoSearchResult> results = prov.search(prov.SEARCH_TITLE, "Everyone's Hero");
		//List<IVideoSearchResult> results = prov.search(prov.SEARCH_TITLE, "8 Mile");

		TestUtils.dumpResults(results);
		
		if (results!=null && results.size()>0) {
			IVideoSearchResult res = results.get(0);
			TestUtils.dumpMetaData(VideoMetaDataFactory.getInstance().getMetaData(res));
		}
	}
}
