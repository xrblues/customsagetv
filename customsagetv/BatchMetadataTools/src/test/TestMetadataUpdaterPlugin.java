package test;

import java.io.File;
import java.io.IOException;

import org.jdna.sage.MetadataUpdaterPlugin;


public class TestMetadataUpdaterPlugin {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

		MetadataUpdaterPlugin mup = new MetadataUpdaterPlugin();
		Object objData = mup.extractMetadata(new File("c:\\dvd\\casino_royale\\video_ts"), "");
	}

}
