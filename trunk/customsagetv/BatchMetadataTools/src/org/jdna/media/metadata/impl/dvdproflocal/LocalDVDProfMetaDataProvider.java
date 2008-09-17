package org.jdna.media.metadata.impl.dvdproflocal;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.metadata.IVideoMetaData;
import org.jdna.media.metadata.IVideoMetaDataProvider;
import org.jdna.media.metadata.IVideoSearchResult;

public class LocalDVDProfMetaDataProvider implements IVideoMetaDataProvider {
	private static final Logger log = Logger.getLogger(LocalDVDProfMetaDataProvider.class);

	public static final String PROVIDER_ID = "dvdprofiler_local";
	public static final String PROVIDER_NAME = "DVD Profiler Provider using local xml and images (Stuckless)";
	public static final String PROVIDER_ICON_URL = "http://www.invelos.com/images/Logo.png";

	private boolean rebuildIndex = false;
	private File xmlFile = null;
	private File imageDir=null;
	private DVDProfXmlFile xmlFileTool;
	
	private boolean initialized = false;
	private static LocalDVDProfMetaDataProvider instance = null;
	public static final LocalDVDProfMetaDataProvider getInstance() {
		return instance;
	}
	
	public LocalDVDProfMetaDataProvider() {
		LocalDVDProfMetaDataProvider.instance = this;
	}
	
	public String getIconUrl() {
		return PROVIDER_ICON_URL;
	}

	public String getId() {
		return PROVIDER_ID;
	}

	public IVideoMetaData getMetaData(String providerDataUrl) throws Exception {
		if (!initialized) initialize();
		return new LocalDVDProfParser(providerDataUrl).getMetaData();
	}

	public String getName() {
		return PROVIDER_NAME;
	}

	public List<IVideoSearchResult> search(int searchType, String arg)	throws Exception {
		if (!initialized) initialize();
		
		if (shouldRebuildIndexes()) {
			try {
				rebuildIndexes();
			} catch (Exception e) {
				log.error("Failed to rebuild the indexes for DVD Profiler!", e);
			}
		}
		
		try {
			return LocalMovieIndex.getInstance().searchTitle(arg);
		} catch (Exception e) {
			throw new Exception("Failed to find: " + arg);
		}
	}

	private void initialize() throws Exception {
		initialized=true;

		
		
		String indexDir = ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "indexDir", "cache/indexDVDProfLocal/");
		LocalMovieIndex.getInstance().setIndexDir(indexDir);

		
		String imageDir = ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "imageDir", null);
		if (imageDir==null) {
			throw new Exception(String.format("Missing imageDir.  Please Set: %s.imageDir", this.getClass().getName()));
		}
		this.imageDir = new File(imageDir);
		if (!this.imageDir.exists()) {
			throw new Exception("Imagedir does not exist: " + imageDir);
		}
		
		String xml = ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "xmlFile", null);
		if (xml==null) {
			throw new Exception(String.format("Missing xml.  Please Set: %s.xmlFile", this.getClass().getName()));
		}
	
		xmlFile = new File(xml);
		if (!xmlFile.exists()) {
			throw new Exception("Missing Xml File: " + xmlFile.getAbsolutePath());
		}
		
		xmlFileTool = new DVDProfXmlFile(xmlFile);
		rebuildIndex = Boolean.parseBoolean(ConfigurationManager.getInstance().getProperty(this.getClass().getName(), "forceRebuild", "false"));
	}

	private void rebuildIndexes() throws Exception {
		log.debug("Rebuilding Indexes....");

		LocalMovieIndex.getInstance().clean();
		LocalMovieIndex.getInstance().beginIndexing();
		xmlFileTool.visitMovies(LocalMovieIndex.getInstance());
		LocalMovieIndex.getInstance().endIndexing();
		
		rebuildIndex = false;
	}

	private boolean shouldRebuildIndexes() {
		return LocalMovieIndex.getInstance().isNew() || rebuildIndex;
	}

	public DVDProfXmlFile getDvdProfilerXmlFile() {
		return xmlFileTool;
	}
	
	public File getImagesDir() {
		return imageDir;
	}

	public IVideoMetaData getMetaData(IVideoSearchResult result) throws Exception {
		return getMetaData(result.getId());
	}
}
