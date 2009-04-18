package org.jdna.media.metadata.impl.dvdproflocal;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Type;

public class LocalDVDProfMetaDataProvider implements IMediaMetadataProvider {
    private static final Logger                 log               = Logger.getLogger(LocalDVDProfMetaDataProvider.class);

    public static final String                  PROVIDER_ID       = "dvdprofiler";
    public static final String                  PROVIDER_NAME     = "DVD Profiler";
    public static final String                  PROVIDER_ICON_URL = "http://www.invelos.com/images/Logo.png";
    private static final String                 PROVIDER_DESC     = "DVD Profiler metadata provider using local xml and images (Stuckless).";

    private static IProviderInfo                info              = new ProviderInfo(PROVIDER_ID, PROVIDER_NAME, PROVIDER_DESC, PROVIDER_ICON_URL);

    private File                                xmlFile           = null;
    private File                                imageDir          = null;
    private DVDProfXmlFile                      xmlFileTool;

    private boolean                             initialized       = false;
    private static LocalDVDProfMetaDataProvider instance          = null;
    
    private static final Type[] supportedSearchTypes = new SearchQuery.Type[] {SearchQuery.Type.MOVIE};

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

    public String getName() {
        return PROVIDER_NAME;
    }

    public List<IMediaSearchResult> search(SearchQuery query) throws Exception {
        if (!initialized) initialize();

        if (shouldRebuildIndexes()) {
            try {
                rebuildIndexes();
            } catch (Exception e) {
                log.error("Failed to rebuild the indexes for DVD Profiler!", e);
            }
        }

        String arg = query.get(SearchQuery.Field.TITLE);
        try {
            return LocalMovieIndex.getInstance().searchTitle(arg);
        } catch (Exception e) {
            throw new Exception("Failed to find: " + arg);
        }
    }

    private void initialize() throws Exception {
        String indexDir = ConfigurationManager.getInstance().getDVDProfilerLocalConfiguration().getIndexDir();
        LocalMovieIndex.getInstance().setIndexDir(indexDir);

        String xml = ConfigurationManager.getInstance().getDVDProfilerLocalConfiguration().getXmlFile();
        if (xml == null) {
            throw new Exception("Missing xml.  Please Set DVDProfiler Xml Location.");
        }

        xmlFile = new File(xml);
        if (!xmlFile.exists()) {
            throw new Exception("Missing Xml File: " + xmlFile.getAbsolutePath());
        }
        
        log.debug("DVD Profiler Xml: " + xmlFile.getAbsolutePath());

        String strImageDir = ConfigurationManager.getInstance().getDVDProfilerLocalConfiguration().getImageDir();
        if (strImageDir == null) {
            log.warn("DVD Profiler Image dir is not set, will use a relative Images path.");
            this.imageDir = new File(xmlFile.getParentFile(), "Images");
        } else {
            this.imageDir = new File(strImageDir);
        }
        
        if (!this.imageDir.exists()) {
            throw new Exception("Imagedir does not exist: " + strImageDir);
        }

        xmlFileTool = new DVDProfXmlFile(xmlFile);
        
        initialized = true;
    }
    
    private boolean isXmlModified() {
        return xmlFile.lastModified() > ConfigurationManager.getInstance().getDVDProfilerLocalConfiguration().getXmlFileLastModified();
    }

    private void rebuildIndexes() throws Exception {
        log.debug("Rebuilding DVD Profiler Indexes....");

        LocalMovieIndex.getInstance().clean();
        LocalMovieIndex.getInstance().beginIndexing();
        xmlFileTool.visitMovies(LocalMovieIndex.getInstance());
        LocalMovieIndex.getInstance().endIndexing();
        
        ConfigurationManager.getInstance().getDVDProfilerLocalConfiguration().setXmlFileLastModified(xmlFile.lastModified());
        ConfigurationManager.getInstance().updated(ConfigurationManager.getInstance().getDVDProfilerLocalConfiguration());
    }

    private boolean shouldRebuildIndexes() {
        return LocalMovieIndex.getInstance().isNew() || isXmlModified();
    }

    public DVDProfXmlFile getDvdProfilerXmlFile() {
        return xmlFileTool;
    }

    public File getImagesDir() {
        return imageDir;
    }

    public IMediaMetadata getMetaData(IMediaSearchResult result) throws Exception {
        return getMetaDataByUrl(result.getUrl());
    }

    public IProviderInfo getInfo() {
        return info;
    }

    public Type[] getSupportedSearchTypes() {
        return supportedSearchTypes;
    }

	public IMediaMetadata getMetaDataFromCompositeId(String compositeId)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

    public IMediaMetadata getMetaDataById(MetadataID id) throws Exception {
        if (!initialized) initialize();
        return new LocalDVDProfParser(id.getId()).getMetaData();
    }

    public IMediaMetadata getMetaDataByUrl(String url) throws Exception {
        if (!initialized) initialize();
        return new LocalDVDProfParser(url).getMetaData();
    }
}
