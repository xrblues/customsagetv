package test;

import java.util.List;

import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.impl.dvdprof.DVDProfMetaDataProvider;
import org.jdna.metadataupdater.MetadataUpdater;

public class TestDVDProfiler {
	public static void main(String args[]) throws Exception {
		MetadataUpdater.initConfiguration();

		DVDProfMetaDataProvider prov = new DVDProfMetaDataProvider();
		List<IMediaSearchResult> results = prov.search(IMediaMetadataProvider.SEARCH_TITLE, "Sharkboy");
		//List<IVideoSearchResult> results = prov.search(prov.SEARCH_TITLE, "Everyone's Hero");
		//List<IVideoSearchResult> results = prov.search(prov.SEARCH_TITLE, "8 Mile");

		TestUtils.dumpResults(results);
		
		if (results!=null && results.size()>0) {
			IMediaSearchResult res = results.get(0);
			TestUtils.dumpMetaData(prov.getMetaData(res));
		}
	}
}
