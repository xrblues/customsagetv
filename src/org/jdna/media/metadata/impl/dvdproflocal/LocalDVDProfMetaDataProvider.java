package org.jdna.media.metadata.impl.dvdproflocal;

import java.io.File;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MetadataUtil;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;

import sagex.phoenix.Phoenix;
import sagex.phoenix.fanart.IMetadataSearchResult;
import sagex.phoenix.fanart.MediaType;

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
    private static LocalDVDProfMetaDataProvider instance          = new LocalDVDProfMetaDataProvider();
    
    private static final MediaType[] supportedSearchTypes = new MediaType[] {MediaType.MOVIE};
    
    private DVDProfilerLocalConfiguration cfg = new DVDProfilerLocalConfiguration();

    public LocalDVDProfMetaDataProvider() {
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

    public List<IMetadataSearchResult> search(SearchQuery query) throws Exception {
        if (!initialized) initialize();

        if (shouldRebuildIndexes()) {
            try {
                rebuildIndexes();
            } catch (Exception e) {
                log.error("Failed to rebuild the indexes for DVD Profiler!", e);
            }
        }

        // search by ID, if the ID is present
        if (!StringUtils.isEmpty(query.get(SearchQuery.Field.ID))) {
            List<IMetadataSearchResult> res = MetadataUtil.searchById(this, query, query.get(SearchQuery.Field.ID));
            if (res!=null) {
                return res;
            }
        }
        
        // carry on normal search
        String arg = query.get(SearchQuery.Field.QUERY);
        try {
            return LocalMovieIndex.getInstance().searchTitle(arg);
        } catch (Exception e) {
            throw new Exception("Failed to find: " + arg, e);
        }
    }

    private void initialize() throws Exception {
        String indexDir = cfg.getIndexDir();
        LocalMovieIndex.getInstance().setIndexDir(indexDir);

        String xml = cfg.getXmlFile();
        if (xml == null) {
            throw new Exception("Missing xml.  Please Set DVDProfiler Xml Location.");
        }

        xmlFile = new File(xml);
        if (!xmlFile.exists()) {
            throw new Exception("Missing Xml File: " + xmlFile.getAbsolutePath());
        }
        
        log.debug("DVD Profiler Xml: " + xmlFile.getAbsolutePath());

        String strImageDir = cfg.getImageDir();
        if (strImageDir == null) {
            log.warn("DVD Profiler Image dir is not set, will use a relative Images path.");
            this.imageDir = new File(xmlFile.getParentFile(), "Images");
        } else {
            this.imageDir = new File(strImageDir);
        }
        
        if (!this.imageDir.exists()) {
            log.warn("Imagedir does not exist: " + strImageDir + "; Will not use DVD Profiler Covers");
        }

        xmlFileTool = new DVDProfXmlFile(xmlFile);
        
        initialized = true;
    }
    
    private boolean isXmlModified() {
        return xmlFile.lastModified() > cfg.getXmlFileLastModified();
    }

    private void rebuildIndexes() throws Exception {
        log.debug("Rebuilding DVD Profiler Indexes....");

        LocalMovieIndex.getInstance().clean();
        LocalMovieIndex.getInstance().beginIndexing();
        xmlFileTool.visitMovies(LocalMovieIndex.getInstance());
        LocalMovieIndex.getInstance().endIndexing();
        
        cfg.setXmlFileLastModified(xmlFile.lastModified());
        Phoenix.getInstance().getConfigurationManager().save();
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

    public IMediaMetadata getMetaData(IMetadataSearchResult result) throws Exception {
        if (!initialized) initialize();
        
        if (MetadataUtil.hasMetadata(result)) return MetadataUtil.getMetadata(result);
        
        return new LocalDVDProfParser(result.getId()).getMetaData();
    }

    public IProviderInfo getInfo() {
        return info;
    }

    public MediaType[] getSupportedSearchTypes() {
        return supportedSearchTypes;
    }
}
