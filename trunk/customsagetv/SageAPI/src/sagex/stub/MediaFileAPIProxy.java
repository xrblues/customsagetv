package sagex.stub;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import sagex.api.Configuration;

public class MediaFileAPIProxy implements StubAPIProxy {
    private Properties props = new Properties();
    
    private static class MediaFile {
        public int id;
        public String title;
        public File file;
        public String toString() {
            return "StubMediaFile[id:"+id+ "; title:"+title+"; File:" +file.getAbsolutePath()+"]";
        }
    }
    
    private static int ids=1;
    private Map<Integer, MediaFile> files = new HashMap<Integer, MediaFile>();
    
    public Object call(String cmd, Object[] args) {
        if ("GetMediaFileID".equals(cmd)) {
            return ((MediaFile)args[0]).id;
        }

        if ("IsMediaFileObject".equals(cmd)) {
            return args[0] instanceof MediaFile;
        }
        
        if ("GetFileForSegment".equals(cmd)) {
            return ((MediaFile)args[0]).file;
        }
        
        if ("GetSegmentFiles".equals(cmd)) {
            return new File[] {((MediaFile)args[0]).file};
        }
        
        if ("GetMediaFileForID".equals(cmd)) {
            return files.get(args[0]);
        }

        if ("GetMediaTitle".equals(cmd) || "GetShowEpisode".equals(cmd) || "GetShowTitle".equals(cmd)) {
            return ((MediaFile)args[0]).title;
        }
        
        if ("AddMediaFile".equals(cmd)) {
            return addMediaFile((File) args[0]);
        }

        if ("GetMediaFiles".equals(cmd)) {
            return files.values().toArray(new MediaFile[files.size()]);
        }
        
        if ("IsTVFile".equals(cmd)) {
            return ((MediaFile)args[0]).file.getAbsolutePath().contains(File.separator + "TV");
        }
        
        if ("IsVideoFile".equals(cmd)) {
            return ((MediaFile)args[0]).file.getAbsolutePath().contains(File.separator + "Movie");
        }
        
        if ("IsMusicFile".equals(cmd)) {
            return ((MediaFile)args[0]).file.getAbsolutePath().contains(File.separator + "Music");
        }
        if ("IsDVD".equals(cmd)) {
            return ((MediaFile)args[0]).file.getAbsolutePath().contains(File.separator + "DVD");
        }
        if ("IsBluRay".equals(cmd)) {
            return ((MediaFile)args[0]).file.getAbsolutePath().contains(File.separator + "BluRay");
        }
        
        if ("GetMediaFileForFilePath".equals(cmd)) {
            for (MediaFile mf : files.values()) {
                if (mf.file.equals((File)args[0])) {
                    return mf;
                }
            }
            return null;
        }

        if ("SetMediaFileMetadata".equals(cmd)) {
            props.setProperty(((MediaFile)args[0]).id+":"+args[1], String.valueOf(args[2]));
            return null;
        }
        
        if ("GetMediaFileMetadata".equals(cmd)) {
            return props.getProperty(((MediaFile)args[0]).id+":"+args[1]);
        }
        
        if ("GetShowCategory".equals(cmd)) {
            String key = ((MediaFile)args[0]).id + ":category";
            // uses sage properties for stub, so that it can be set during testing
            return Configuration.GetProperty(key, null);
        }
        
        System.out.println("MediaFileAPIProxy: Unhandled: " + cmd);
        
        return null;
    }
    
    public MediaFile addMediaFile(File f) {
        MediaFile mf = new MediaFile();
        mf.title=f.getName();
        if (mf.title.indexOf(".")!=-1) {
            mf.title = mf.title.substring(0, mf.title.indexOf("."));
        }
        mf.file=f;
        mf.id=ids++;
        files.put(mf.id, mf);
        return mf;
    }
    
    public void attach(StubSageAPI api) {
        api.addProxy("GetMediaFileID", this);
        api.addProxy("IsMediaFileObject", this);
        api.addProxy("GetFileForSegment", this);
        api.addProxy("GetMediaFileForID", this);
        api.addProxy("AddMediaFile", this);
        api.addProxy("GetMediaTitle",this);
        
        api.addProxy("GetShowEpisode",this);
        api.addProxy("GetShowTitle", this);
        
        api.addProxy("GetMediaFileForFilePath", this);
        api.addProxy("GetMediaFiles", this);
        api.addProxy("GetSegmentFiles", this);
        api.addProxy("IsTVFile", this);
        api.addProxy("IsVideoFile", this);
        api.addProxy("IsMusicFile", this);
        api.addProxy("IsDVD", this);
        api.addProxy("IsBluRay", this);
        
        api.addProxy("SetMediaFileMetadata", this);
        api.addProxy("GetMediaFileMetadata", this);
        
        api.addProxy("GetShowCategory", this);
    }
}
