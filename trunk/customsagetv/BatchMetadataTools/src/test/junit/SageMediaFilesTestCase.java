package test.junit;

import static test.junit.lib.FilesTestCase.getFile;
import static test.junit.lib.FilesTestCase.makeFile;

import java.io.File;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaFile.ContentType;
import org.jdna.media.metadata.MetadataAPI;
import org.jdna.media.metadata.SearchQuery;
import org.jdna.media.metadata.SearchQueryFactory;
import org.jdna.media.metadata.SearchQuery.Type;
import org.jdna.sage.media.SageMediaFile;
import org.jdna.sage.media.SageMediaFolder;

import sagex.SageAPI;
import sagex.api.MediaFileAPI;
import sagex.stub.StubSageAPI;
import test.junit.lib.CountResourceVisitor;

public class SageMediaFilesTestCase extends TestCase {
    static {
        BasicConfigurator.configure();
    }
    
    public SageMediaFilesTestCase() {
        super();
    }

    public SageMediaFilesTestCase(String name) {
        super(name);
    }
    
    public void testSageMediaFileCreation() {
        StubSageAPI provider = new StubSageAPI();
        SageAPI.setProvider(provider);
        Object mfObj = MediaFileAPI.AddMediaFile(makeFile("test/sample.avi"), "Movie");
        int mfId = MediaFileAPI.GetMediaFileID(mfObj);
        
        SageMediaFile mf = null;
        mf = new SageMediaFile(getFile("test/sample.avi"));
        assertEquals("SameMediaFile by file failed", mfId, MediaFileAPI.GetMediaFileID(mf.getSageMediaFileObject(mf)));
        
        Object smf = MediaFileAPI.GetMediaFileForID(mfId);
        mf = new SageMediaFile(smf);
        assertEquals("SameMediaFile by object failed", mfId, MediaFileAPI.GetMediaFileID(mf.getSageMediaFileObject(mf)));
        
        provider.addCall("IsAiringObject", true);
        mf = new SageMediaFile("arigingObject");
        assertEquals("Sage didn't think it was an airing", "arigingObject", mf.getSageMediaFileObject(mf));
    }
    
    public void testSageMediaFile() throws Exception {
        StubSageAPI provider = new StubSageAPI();
        SageAPI.setProvider(provider);
        provider.addCall("IsVideoFile", true);
        File f = new File("/tmp/movie cd1.avi");
        Object sageMF = MediaFileAPI.AddMediaFile(f, "Movie");
        
        IMediaFile mf = new SageMediaFile(sageMF);
        assertEquals("getBasename()","movie cd1",mf.getBasename());
        assertEquals("getExtension()","avi",mf.getExtension());
        assertEquals("getLocationUri()",f.toURI().toASCIIString(),mf.getLocation().toURI());
        assertEquals("getName()","movie cd1.avi",mf.getName());
        assertEquals("getTitle()","movie cd1",mf.getTitle());
        assertEquals("getType()",IMediaResource.Type.File,mf.getType());
        assertEquals("getContentType()", ContentType.MOVIE, mf.getContentType());
    }
    
    public void testSageDVDFile() throws Exception {
        StubSageAPI provider = new StubSageAPI();
        provider.setDebugCalls(false);
        provider.addCall("IsDVD",true);
        SageAPI.setProvider(provider);

        File f = new File("/tmp/movie cd1");
        Object sageMF = MediaFileAPI.AddMediaFile(f, "Movie");
        
        IMediaFile mf = new SageMediaFile(sageMF);
        assertEquals("getBasename()","movie cd1",mf.getBasename());
        assertEquals("getLocationUri()",f.toURI().toASCIIString(),mf.getLocation().toURI());
        assertEquals("getName()","movie cd1",mf.getName());
        assertEquals("getTitle()","movie cd1",mf.getTitle());
        assertEquals("getType()",IMediaResource.Type.File,mf.getType());
        assertEquals("getContentType()", ContentType.HDFOLDER, mf.getContentType());
    }

    public void testSageBlurayFile() throws Exception {
        StubSageAPI provider = new StubSageAPI();
        provider.setDebugCalls(false);
        provider.addCall("IsBluRay",true);
        SageAPI.setProvider(provider);

        File f = new File("/tmp/movie cd1/");
        Object sageMF = MediaFileAPI.AddMediaFile(f, "Movie");
        
        IMediaFile mf = new SageMediaFile(sageMF);
        assertEquals("getBasename()","movie cd1",mf.getBasename());
        assertEquals("getLocationUri()",f.toURI().toASCIIString(),mf.getLocation().toURI());
        assertEquals("getName()","movie cd1",mf.getName());
        assertEquals("getTitle()","movie cd1",mf.getTitle());
        assertEquals("getType()",IMediaResource.Type.File,mf.getType());
        assertEquals("getContentType()", ContentType.HDFOLDER, mf.getContentType());
    }
    
    public void testSageTVAiring() throws Exception {
        StubSageAPI provider = new StubSageAPI();
        provider.setDebugCalls(false);
        provider.addCall("IsTVFile",true);
        provider.addCall("IsAiringObject",true);
        provider.addCall("GetMediaFileForID", "sagemf");
        provider.addCall("GetAiringTitle","TVShow");
        SageAPI.setProvider(provider);
        
        IMediaFile mf = new SageMediaFile(new File("shows/House-TestTitle-000000000-0.ts"));
        assertEquals("getBasename()","TVShow",mf.getBasename());
        assertEquals("getExtension()",null,mf.getExtension());
        //assertEquals("getLocationUri()","file://shows/House-TestTitle-000000000-0.ts",mf.getLocation().toURI());
        assertEquals("getName()","TVShow",mf.getName());
        assertEquals("getTitle()","TVShow",mf.getTitle());
        assertEquals("getType()",IMediaResource.Type.File,mf.getType());
        assertEquals("getContentType()", ContentType.TV, mf.getContentType());

        SearchQuery query = SearchQueryFactory.getInstance().createQuery(mf);
        assertEquals(Type.TV, query.getType());
    }

    public void testSageTVMovie() throws Exception {
        StubSageAPI provider = new StubSageAPI();
        provider.setDebugCalls(false);
        provider.addCall("IsTVFile",true);
        provider.addCall("IsAiringObject",true);
        provider.addCall("GetMediaFileForID", "sagemf");
        provider.addCall("GetAiringTitle","TVShow");
        provider.addCall("GetShowCategory", "Movie");
        SageAPI.setProvider(provider);
        
        IMediaFile mf = new SageMediaFile(1200);
        assertEquals("getBasename()","TVShow",mf.getBasename());
        assertEquals("getExtension()",null,mf.getExtension());
        assertEquals("getLocationUri()","sage://id/1200",mf.getLocation().toURI());
        assertEquals("getName()","TVShow",mf.getName());
        assertEquals("getTitle()","TVShow",mf.getTitle());
        assertEquals("getType()",IMediaResource.Type.File,mf.getType());
        assertEquals("getContentType()", ContentType.MOVIE, mf.getContentType());
        
        SearchQuery query = SearchQueryFactory.getInstance().createQuery(mf);
        assertEquals(Type.MOVIE, query.getType());
        
    }
    
    public void testSageMediaFolder() throws Exception {
        StubSageAPI provider = new StubSageAPI();
        provider.setDebugCalls(false);
        SageAPI.setProvider(provider);

        MediaFileAPI.AddMediaFile(makeFile("tmp/file1.avi"), "V");
        MediaFileAPI.AddMediaFile(makeFile("tmp/file2.avi"), "V");
        MediaFileAPI.AddMediaFile(makeFile("tmp/file3.avi"), "V");
        
        try {
            SageMediaFolder smf = new SageMediaFolder("sagex://find");
            fail("SageMediaFolder should only access sage:// uris");
        } catch (Exception e) {
        }
        
        // create a folder with a static set of items
        SageMediaFolder smf = new SageMediaFolder(MediaFileAPI.GetMediaFiles());
        assertEquals("size", 3, smf.members().size());
        
        for (IMediaResource mr : smf.members()) {
            assertTrue("not a sage media file", mr instanceof SageMediaFile);
        }
        
        // create a sage media folder with a sage uri lookup
        smf = new SageMediaFolder("sage://query/V");
        assertEquals("uri command", "query", smf.getUriCommand());
        assertEquals("uri command Args", "V", smf.getSageQueryTypes());
        assertEquals("size", 3, smf.members().size());
        
        // test visitors
        CountResourceVisitor crv = new CountResourceVisitor();
        smf.accept(crv);
        assertEquals("vistor count", 4, crv.getCount()); // 4 because of the visit to the folder itself
    }
    
    public void testMultiCD() {
        StubSageAPI provider = new StubSageAPI();
        provider.setDebugCalls(false);
        SageAPI.setProvider(provider);

        SageMediaFile smf1 = new SageMediaFile(MediaFileAPI.AddMediaFile(makeFile("tmp/movies/The Terminator cd1.avi"), "Movie1"));
        SageMediaFile smf2 = new SageMediaFile(MediaFileAPI.AddMediaFile(makeFile("tmp/movies/The Terminator cd2.avi"), "Movie2"));
        SageMediaFile smf3 = new SageMediaFile(MediaFileAPI.AddMediaFile(makeFile("tmp/movies/Finding Nemo.avi"), "Movie3"));
        assertEquals("1", MetadataAPI.getCDFromMediaFile(smf1));
        assertEquals("2", MetadataAPI.getCDFromMediaFile(smf2));
        assertEquals(null, MetadataAPI.getCDFromMediaFile(smf3));
    }
}
