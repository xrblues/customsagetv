package org.jdna.media.metadata.impl.dvdproflocal;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.configuration.ConfigurationManager;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Type;

public class LocalDVDProfMetaDataProvider implements IMediaMetadataProvider {
    private static final Logger                 log               = Logger.getLogger(LocalDVDProfMetaDataProvider.class);

    public static final String                  PROVIDER_ID       = "dvdprofiler_local";
    public static final String                  PROVIDER_NAME     = "Local DVD Profiler Provider";
    public static final String                  PROVIDER_ICON_URL = "http://www.invelos.com/images/Logo.png";
    private static final String                 PROVIDER_DESC     = "DVD Profiler Provider using local xml and images (Stuckless).";

    private static IProviderInfo                info              = new ProviderInfo(PROVIDER_ID, PROVIDER_NAME, PROVIDER_DESC, PROVIDER_ICON_URL);

    private boolean                             rebuildIndex      = false;
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

    public IMediaMetadata getMetaData(String providerDataUrl) throws Exception {
        if (!initialized) initialize();
        return new LocalDVDProfParser(providerDataUrl).getMetaData();
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
        initialized = true;

        String indexDir = ConfigurationManager.getInstance().getDVDProfilerLocalConfiguration().getIndexDir();
        LocalMovieIndex.getInstance().setIndexDir(indexDir);

        String imageDir = ConfigurationManager.getInstance().getDVDProfilerLocalConfiguration().getImageDir();
        if (imageDir == null) {
            throw new Exception(String.format("Missing imageDir.  Please Set: %s.imageDir", this.getClass().getName()));
        }
        this.imageDir = new File(imageDir);
        if (!this.imageDir.exists()) {
            throw new Exception("Imagedir does not exist: " + imageDir);
        }

        String xml = ConfigurationManager.getInstance().getDVDProfilerLocalConfiguration().getXmlFile();
        if (xml == null) {
            throw new Exception(String.format("Missing xml.  Please Set: %s.xmlFile", this.getClass().getName()));
        }

        xmlFile = new File(xml);
        if (!xmlFile.exists()) {
            throw new Exception("Missing Xml File: " + xmlFile.getAbsolutePath());
        }

        xmlFileTool = new DVDProfXmlFile(xmlFile);
        rebuildIndex = ConfigurationManager.getInstance().getMetadataUpdaterConfiguration().isRefreshIndexes();
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

    public IMediaMetadata getMetaData(IMediaSearchResult result) throws Exception {
        return getMetaData(result.getUrl());
    }

    public IProviderInfo getInfo() {
        return info;
    }

    public IMediaMetadata getMetaDataByIMDBId(String imdbId) throws Exception, UnsupportedOperationException {
        throw new UnsupportedOperationException("DVDProfiler Doesn't Know how to handle IMDB ids");
    }

    public Type[] getSupportedSearchTypes() {
        return supportedSearchTypes;
    }
}
