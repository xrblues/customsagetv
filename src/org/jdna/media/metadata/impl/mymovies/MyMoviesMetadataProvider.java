package org.jdna.media.metadata.impl.mymovies;

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

public class MyMoviesMetadataProvider implements IMediaMetadataProvider {
    private static final Logger                 log               = Logger.getLogger(MyMoviesMetadataProvider.class);

    public static final String                  PROVIDER_ID       = "mymovies";
    public static final String                  PROVIDER_NAME     = "MyMovies";
    public static final String                  PROVIDER_ICON_URL = "";
    private static final String                 PROVIDER_DESC     = "MyMovies metadata provider (Stuckless).";

    private static IProviderInfo                info              = new ProviderInfo(PROVIDER_ID, PROVIDER_NAME, PROVIDER_DESC, PROVIDER_ICON_URL);

    private File                                xmlFile           = null;
    private File                                imageDir          = null;
    private MyMoviesXmlFile                      xmlFileTool;

    private boolean                             initialized       = false;
    
    private MyMoviesConfiguration cfg = new MyMoviesConfiguration();
    
    private static MyMoviesMetadataProvider instance          = new MyMoviesMetadataProvider();
    
    private static final MediaType[] supportedSearchTypes = new MediaType[] {MediaType.MOVIE};

    public MyMoviesMetadataProvider() {
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
                log.error("Failed to rebuild the indexes for MyMovies!", e);
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
            return MyMoviesIndex.getInstance().searchTitle(arg);
        } catch (Exception e) {
            throw new Exception("Failed to find: " + arg);
        }
    }

    private void initialize() throws Exception {
        String indexDir = cfg.getIndexDir();
        MyMoviesIndex.getInstance().setIndexDir(indexDir);

        String xml = cfg.getXmlFile();
        if (xml == null) {
            throw new Exception("Missing xml.  Please Set MyMovies Xml Location.");
        }

        xmlFile = new File(xml);
        if (!xmlFile.exists()) {
            throw new Exception("Missing Xml File: " + xmlFile.getAbsolutePath());
        }
        
        log.debug("MyMovies Xml: " + xmlFile.getAbsolutePath());

        xmlFileTool = new MyMoviesXmlFile(xmlFile);
        
        initialized = true;
    }
    
    private boolean isXmlModified() {
        return xmlFile.lastModified() > cfg.getXmlFileLastModified();
    }

    private void rebuildIndexes() throws Exception {
        log.debug("Rebuilding MyMovies Indexes....");

        MyMoviesIndex.getInstance().clean();
        MyMoviesIndex.getInstance().beginIndexing();
        xmlFileTool.visitMovies(MyMoviesIndex.getInstance());
        MyMoviesIndex.getInstance().endIndexing();
        
        cfg.setXmlFileLastModified(xmlFile.lastModified());
        Phoenix.getInstance().getConfigurationManager().save();
    }

    private boolean shouldRebuildIndexes() {
        return MyMoviesIndex.getInstance().isNew() || isXmlModified();
    }

    public MyMoviesXmlFile getMyMoviesXmlFile() {
        return xmlFileTool;
    }

    public File getImagesDir() {
        return imageDir;
    }

    public IMediaMetadata getMetaData(IMetadataSearchResult result) throws Exception {
        if (!initialized) initialize();
        
        if (MetadataUtil.hasMetadata(result)) return MetadataUtil.getMetadata(result);

        return new MyMoviesParser(result.getId()).getMetaData();
    }

    public IProviderInfo getInfo() {
        return info;
    }

    public MediaType[] getSupportedSearchTypes() {
        return supportedSearchTypes;
    }
}
