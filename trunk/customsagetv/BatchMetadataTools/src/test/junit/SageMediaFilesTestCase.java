package test.junit;

import java.io.File;

import junit.framework.TestCase;

import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaFile.ContentType;
import org.jdna.sage.media.SageMediaFile;
import org.jdna.sage.media.SageMediaFolder;

import sagex.SageAPI;
import sagex.api.MediaFileAPI;
import sagex.stub.StubSageAPI;

import static test.junit.FilesTestCase.*;

public class SageMediaFilesTestCase extends TestCase {

    public SageMediaFilesTestCase() {
        super();
    }

    public SageMediaFilesTestCase(String name) {
        super(name);
    }
    
    public void testSageMediaFileCreation() {
        StubSageAPI provider = new StubSageAPI();
        SageAPI.setProvider(provider);
        MediaFileAPI.AddMediaFile(makeFile("test/sample.avi"), "Movie");
        
        SageMediaFile mf = new SageMediaFile(1);
        assertEquals("SameMediaFile by id failed", 1, MediaFileAPI.GetMediaFileID(mf.getSageMediaObject()));
        
        mf = new SageMediaFile(getFile("test/sample.avi"));
        assertEquals("SameMediaFile by file failed", 1, MediaFileAPI.GetMediaFileID(mf.getSageMediaObject()));
        
        Object smf = MediaFileAPI.GetMediaFileForID(1);
        mf = new SageMediaFile(smf);
        assertEquals("SameMediaFile by object failed", 1, MediaFileAPI.GetMediaFileID(mf.getSageMediaObject()));
        
        provider.addCall("IsAiringObject", true);
        mf = new SageMediaFile("arigingObject");
        assertEquals("Sage didn't think it was an airing", "arigingObject", mf.getSageMediaObject());
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
        assertEquals("getLocationUri()",f.toURI(),mf.getLocationUri());
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

        File f = new File("/tmp/movie cd1.avi");
        Object sageMF = MediaFileAPI.AddMediaFile(f, "Movie");
        
        IMediaFile mf = new SageMediaFile(sageMF);
        assertEquals("getBasename()","movie cd1",mf.getBasename());
        assertEquals("getExtension()","avi",mf.getExtension());
        assertEquals("getLocationUri()",f.toURI(),mf.getLocationUri());
        assertEquals("getName()","movie cd1.avi",mf.getName());
        assertEquals("getTitle()","movie cd1",mf.getTitle());
        assertEquals("getType()",IMediaResource.Type.File,mf.getType());
        assertEquals("getContentType()", ContentType.HDFOLDER, mf.getContentType());
    }

    public void testSageBlurayFile() throws Exception {
        StubSageAPI provider = new StubSageAPI();
        provider.setDebugCalls(false);
        provider.addCall("IsBluRay",true);
        SageAPI.setProvider(provider);

        File f = new File("/tmp/movie cd1.avi");
        Object sageMF = MediaFileAPI.AddMediaFile(f, "Movie");
        
        IMediaFile mf = new SageMediaFile(sageMF);
        assertEquals("getBasename()","movie cd1",mf.getBasename());
        assertEquals("getExtension()","avi",mf.getExtension());
        assertEquals("getLocationUri()",f.toURI(),mf.getLocationUri());
        assertEquals("getName()","movie cd1.avi",mf.getName());
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
        
        IMediaFile mf = new SageMediaFile(1200);
        assertEquals("getBasename()","TVShow",mf.getBasename());
        assertEquals("getExtension()",null,mf.getExtension());
        assertEquals("getLocationUri()","sage://id/1200",mf.getLocationUri().toASCIIString());
        assertEquals("getName()","TVShow",mf.getName());
        assertEquals("getTitle()","TVShow",mf.getTitle());
        assertEquals("getType()",IMediaResource.Type.File,mf.getType());
        assertEquals("getContentType()", ContentType.TV, mf.getContentType());
    }
    
    public void testSageMediaFolder() throws Exception {
        StubSageAPI provider = new StubSageAPI();
        provider.setDebugCalls(false);
        SageAPI.setProvider(provider);

        MediaFileAPI.AddMediaFile(new File("/tmp/file1.avi"), "TV");
        MediaFileAPI.AddMediaFile(new File("/tmp/file2.avi"), "TV");
        MediaFileAPI.AddMediaFile(new File("/tmp/file3.avi"), "TV");
        
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
        smf = new SageMediaFolder("sage://query/TV");
        assertEquals("uri command", "query", smf.getUriCommand());
        assertEquals("uri command Args", "TV", smf.getSageQueryTypes());
        assertEquals("size", 3, smf.members().size());
        
        // test visitors
        CountResourceVisitor crv = new CountResourceVisitor();
        smf.accept(crv);
        assertEquals("vistor count", 4, crv.getCount()); // 4 because of the visit to the folder itself
    }
}
