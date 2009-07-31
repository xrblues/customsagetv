package test.junit;

import static test.junit.FilesTestCase.makeDir;
import static test.junit.FilesTestCase.makeFile;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.filefilter.FileFileFilter;
import org.jdna.media.CDStackingModel;
import org.jdna.media.FileHDFolderMediaFile;
import org.jdna.media.FileMediaFile;
import org.jdna.media.FileMediaFolder;
import org.jdna.media.IMediaFile;
import org.jdna.media.IMediaFolder;
import org.jdna.media.IMediaResource;
import org.jdna.media.IMediaResourceVisitor;
import org.jdna.media.StackedMediaFile;
import org.jdna.media.StackedMediaFolder;
import org.jdna.media.VirtualMediaFile;
import org.jdna.media.VirtualMediaFolder;
import org.jdna.util.DirectoryScanner;

public class MediaResourcesTestCase extends TestCase {
    private static class Counter {
        public int counter=0;
    }
    
    public MediaResourcesTestCase() {
        super();
    }

    public MediaResourcesTestCase(String name) {
        super(name);
    }

    public void testMediaFile() {
        File mfile = FilesTestCase.makeFile("test/movie cd1.avi");
        IMediaFile mf = new FileMediaFile(mfile);
        assertEquals("getBasename()","movie cd1",mf.getBasename());
        assertEquals("getExtension()","avi",mf.getExtension());
        assertEquals("getLocationUri()",mfile.toURI().toASCIIString(),mf.getLocation().toURI());
        assertEquals("getName()","movie cd1.avi",mf.getName());
        assertEquals("getTitle()","movie cd1",mf.getTitle());
        assertEquals("getType()",IMediaResource.Type.File,mf.getType());
        mf.delete();
        assertEquals("delete()", false, mf.exists());
    }
    
    
    public void testMediaFolder() {
        File mfile = FilesTestCase.makeDir("test/folder 1");
        IMediaFolder mf = (IMediaFolder)FileMediaFolder.createResource(mfile);
        assertEquals("getBasename()","folder 1",mf.getBasename());
        assertEquals("getExtension()",null,mf.getExtension());
        assertEquals("getLocationUri()",mfile.toURI().toASCIIString(),mf.getLocation().toURI());
        assertEquals("getName()","folder 1",mf.getName());
        assertEquals("getTitle()","folder 1",mf.getTitle());
        assertEquals("getType()",IMediaResource.Type.Folder,mf.getType());
        mf.delete();
        assertEquals("delete()", false, mf.exists());
    }
    
    public void testVirtualFolder() throws Exception {
        VirtualMediaFolder vmf =null;
        vmf = new VirtualMediaFolder("test:/my/virtual%20folder");
        vmf.addMember(new VirtualMediaFile("test:/f1/movie%20cd1.avi"));
        vmf.addMember(new VirtualMediaFile("test:/f1/movie%20cd2.avi"));
        vmf.addMember(new VirtualMediaFile("test:/f1/anothermovie.avi"));
        vmf.addMember(new VirtualMediaFile("test:/f1/testmovie.avi"));
        vmf.addMember(new VirtualMediaFolder("test:/f2/test2/"));

        assertEquals("getBasename()","virtual folder",vmf.getBasename());
        assertEquals("getExtension()",null,vmf.getExtension());
        assertEquals("getLocationUri()","test:/my/virtual%20folder",vmf.getLocation().toURI());
        assertEquals("getName()","virtual folder",vmf.getName());
        assertEquals("getTitle()","virtual folder",vmf.getTitle());
        assertEquals("getType()",IMediaResource.Type.Folder,vmf.getType());
        assertEquals("exists()", false, vmf.exists());
        assertEquals("member count", 5, vmf.members().size());
    
        final Counter counter =new Counter();
        vmf.accept(new IMediaResourceVisitor() {
            public void visit(IMediaResource resource) {
                counter.counter++;
            }
        });
        
        // 5 because 1 for the folder and 4 the children and 1 for the virtual folder
        assertEquals("accept()", 6, counter.counter);
    }
    
    public void testStackedFolder() throws Exception {
        VirtualMediaFolder vmf = new VirtualMediaFolder("test:/my/virtual%20folder");
        vmf.addMember(new VirtualMediaFile("test:/f1/movie%20cd1.avi"));
        vmf.addMember(new VirtualMediaFile("test:/f1/movie%20cd2.avi"));
        vmf.addMember(new VirtualMediaFile("test:/f1/anothermovie.avi"));
        vmf.addMember(new VirtualMediaFile("test:/f1/testmovie.avi"));
        vmf.addMember(new VirtualMediaFolder("test:/f2/test2/"));
        
        StackedMediaFolder smf = new StackedMediaFolder(vmf, CDStackingModel.INSTANCE);
        assertEquals("member count", 4, smf.members().size());

        int found=0;
        for (IMediaResource r : smf.members()) {
            if (r instanceof StackedMediaFile) {
                assertEquals("stacked title", r.getTitle(), "movie");
                assertEquals("stacked items size", 2, ((StackedMediaFile)r).getStackedFiles().size());
                found++;
            }
        }
        
        assertEquals("too many stacked items", 1, found);
    }
    
    public void testBlurayFolders() {
        File br = FilesTestCase.makeFile("movies/Terminator/BDMV/test.m2ts");
        
        // create using DBMV dir
        IMediaResource mr = FileMediaFolder.createResource(br.getParentFile());
        assertEquals("getType", IMediaResource.Type.File, mr.getType());
        
        FileHDFolderMediaFile mf = (FileHDFolderMediaFile)mr;
        assertEquals("content type", IMediaFile.ContentType.HDFOLDER, mf.getContentType());
        assertEquals("getTitle()","Terminator", mf.getTitle());

        // create using Terminator dir
        mr = FileMediaFolder.createResource(br.getParentFile().getParentFile());
        assertEquals("getType", IMediaResource.Type.File, mr.getType());
        
        mf = (FileHDFolderMediaFile)mr;
        assertEquals("content type", IMediaFile.ContentType.HDFOLDER, mf.getContentType());
        assertEquals("getTitle()","Terminator", mf.getTitle());

        mf.delete();
        assertEquals("delete()", false, mf.exists());
    }

    public void testDVDFolders() {
        File br = FilesTestCase.makeFile("movies/Terminator2/VIDEO_TS/test.vob");
        
        // create using videots dir
        IMediaResource mr = FileMediaFolder.createResource(br.getParentFile());
        assertEquals("getType", IMediaResource.Type.File, mr.getType());
        
        FileHDFolderMediaFile mf = (FileHDFolderMediaFile)mr;
        assertEquals("content type", IMediaFile.ContentType.HDFOLDER, mf.getContentType());
        assertEquals("getTitle()","Terminator2", mf.getTitle());

        // create using Terminator2 dir
        mr = FileMediaFolder.createResource(br.getParentFile().getParentFile());
        assertEquals("getType", IMediaResource.Type.File, mr.getType());
        
        mf = (FileHDFolderMediaFile)mr;
        assertEquals("content type", IMediaFile.ContentType.HDFOLDER, mf.getContentType());
        assertEquals("getTitle()","Terminator2", mf.getTitle());

        mf.delete();
        assertEquals("delete()", false, mf.exists());
        
        // create using .vob files
        br = FilesTestCase.makeFile("movies/Terminator3/test.vob");
        
        // create using videots dir
        mr = FileMediaFolder.createResource(br.getParentFile());
        assertEquals("getType", IMediaResource.Type.File, mr.getType());
        
        mf = (FileHDFolderMediaFile)mr;
        assertEquals("content type", IMediaFile.ContentType.HDFOLDER, mf.getContentType());
        assertEquals("getTitle()","Terminator3", mf.getTitle());

        mf.delete();
        assertEquals("delete()", false, mf.exists());
    }
    
    public void testMediaScan() {
        makeFile("test/movies/dira/Nemo.avi");
        makeFile("test/movies/dira/Nemo.avi.properties");
        makeFile("test/movies/dira/Nemo.jpg");
        makeFile("test/movies/dira/Terminator.avi");
        makeFile("test/movies/dirb/x.avi");
        makeFile("test/movies/dirc/y/VIDEO_TS/test.vob");
        makeFile("test/movies/dirc/y/VIDEO_TS/test.buf");
        makeFile("test/movies/dird/dire/z.avi");
        makeFile("test/movies/dire/BDMV/test.m2ts");
        makeFile("test/movies/merlin/video.vob");
        
        final List<IMediaResource> files = new ArrayList<IMediaResource>();
        IMediaResourceVisitor vis = new IMediaResourceVisitor() {
            public void visit(IMediaResource resource) {
                if (resource.getType()== IMediaResource.Type.File) {
                    files.add(resource);
                }
            }
        };
        
        FileMediaFolder mf = (FileMediaFolder) FileMediaFolder.createResource(makeDir("test/movies"));
        mf.accept(vis, true);
        assertEquals("visitor failed", 7, files.size());
        assertEquals("children", 6, mf.members().size());
        
        int fileCtr=0, dirs=0;
        for (IMediaResource r : mf.members()) {
            if (r.getType() == IMediaResource.Type.File) {
                fileCtr++;
            } else {
                dirs++;
            }
        }
        
        assertEquals("files", 2, fileCtr);
        assertEquals("folders", 4, dirs);
        

        // should not have deleted everything because .jpg and .properties files
        mf.delete();
        assertEquals("delete()", true, mf.exists());
        
        final List filesLeft = new LinkedList();
        DirectoryScanner scanner = new DirectoryScanner(FileFileFilter.FILE);
        scanner.scan(makeDir("test/movies"), filesLeft);
        
        assertEquals("files left", 2, filesLeft.size());
    }
}
