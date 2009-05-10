package test.junit;

import java.io.File;

import junit.framework.TestCase;

import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaFile.ContentType;
import org.jdna.sage.media.SageMediaFile;
import org.jdna.sage.media.SageMediaFolder;

import sagex.SageAPI;
import sagex.stub.StubSageAPI;

public class SageMediaFilesTestCase extends TestCase {

    public SageMediaFilesTestCase() {
        super();
    }

    public SageMediaFilesTestCase(String name) {
        super(name);
    }
    
    public void testSageMediaFile() throws Exception {
        StubSageAPI provider = new StubSageAPI();
        provider.setDebugCalls(false);
        provider.addCall("IsMediaFileObject",true);
        provider.addCall("IsAiringObject",false);
        provider.addCall("GetMediaFileForID", "sagemf");
        provider.addCall("IsVideoFile", true);
        provider.addCall("GetFileForSegment", new File("/tmp/movie cd1.avi"));
        SageAPI.setProvider(provider);
        
        IMediaFile mf = new SageMediaFile(1200);
        assertEquals("getBasename()","movie cd1",mf.getBasename());
        assertEquals("getExtension()","avi",mf.getExtension());
        assertEquals("getLocationUri()","sage://id/1200",mf.getLocationUri().toASCIIString());
        assertEquals("getName()","movie cd1.avi",mf.getName());
        assertEquals("getTitle()","movie cd1",mf.getTitle());
        assertEquals("getType()",IMediaResource.Type.File,mf.getType());
        assertEquals("getContentType()", ContentType.MOVIE, mf.getContentType());
    }
    
    public void testSageDVDFile() throws Exception {
        StubSageAPI provider = new StubSageAPI();
        provider.setDebugCalls(false);
        provider.addCall("IsMediaFileObject",true);
        provider.addCall("IsDVD",true);
        provider.addCall("GetMediaFileForID", "sagemf");
        provider.addCall("GetFileForSegment", new File("/tmp/movie cd1.avi"));
        SageAPI.setProvider(provider);
        
        IMediaFile mf = new SageMediaFile(1200);
        assertEquals("getBasename()","movie cd1",mf.getBasename());
        assertEquals("getExtension()","avi",mf.getExtension());
        assertEquals("getLocationUri()","sage://id/1200",mf.getLocationUri().toASCIIString());
        assertEquals("getName()","movie cd1.avi",mf.getName());
        assertEquals("getTitle()","movie cd1",mf.getTitle());
        assertEquals("getType()",IMediaResource.Type.File,mf.getType());
        assertEquals("getContentType()", ContentType.HDFOLDER, mf.getContentType());
    }

    public void testSageBlurayFile() throws Exception {
        StubSageAPI provider = new StubSageAPI();
        provider.setDebugCalls(false);
        provider.addCall("IsMediaFileObject",true);
        provider.addCall("IsBluRay",true);
        provider.addCall("GetMediaFileForID", "sagemf");
        provider.addCall("GetFileForSegment", new File("/tmp/movie cd1.avi"));
        SageAPI.setProvider(provider);
        
        IMediaFile mf = new SageMediaFile(1200);
        assertEquals("getBasename()","movie cd1",mf.getBasename());
        assertEquals("getExtension()","avi",mf.getExtension());
        assertEquals("getLocationUri()","sage://id/1200",mf.getLocationUri().toASCIIString());
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

        try {
            SageMediaFolder smf = new SageMediaFolder("sagex://find");
            fail("SageMediaFolder should only access sage:// uris");
        } catch (Exception e) {
        }
        
        // create a folder with a static set of items
        Object items[] = new Object[] {"m1","m2","m3"};
        SageMediaFolder smf = new SageMediaFolder(items);
        assertEquals("size", 3, smf.members().size());
        
        for (IMediaResource mr : smf.members()) {
            assertTrue("not a sage media file", mr instanceof SageMediaFile);
        }
        
        // create a sage media folder with a sage uri lookup
        provider.addCall("GetMediaFiles", new Object[] {"1","2"}); 
        smf = new SageMediaFolder("sage://query/TV");
        assertEquals("uri command", "query", smf.getUriCommand());
        assertEquals("uri command Args", "TV", smf.getSageQueryTypes());
        assertEquals("size", 2, smf.members().size());
        
        // test visitors
        CountResourceVisitor crv = new CountResourceVisitor();
        smf.accept(crv);
        assertEquals("vistor count", 3, crv.getCount()); // 3 because of the visit to the folder itself
    }
    
    
}
