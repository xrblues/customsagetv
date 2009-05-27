package org.jdna.media.metadata.impl.mymovies;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MetadataID;
import org.jdna.media.metadata.ProviderInfo;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQuery.Type;

import sagex.phoenix.Phoenix;

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
    
    private static final Type[] supportedSearchTypes = new SearchQuery.Type[] {SearchQuery.Type.MOVIE};

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

    public List<IMediaSearchResult> search(SearchQuery query) throws Exception {
        if (!initialized) initialize();

        if (shouldRebuildIndexes()) {
            try {
                rebuildIndexes();
            } catch (Exception e) {
                log.error("Failed to rebuild the indexes for MyMovies!", e);
            }
        }

        String arg = query.get(SearchQuery.Field.TITLE);
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
        return new MyMoviesParser(id.getId()).getMetaData();
    }

    public IMediaMetadata getMetaDataByUrl(String url) throws Exception {
        if (!initialized) initialize();
        return new MyMoviesParser(url).getMetaData();
    }
}
