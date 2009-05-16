package sagex.stub;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MediaFileAPIProxy implements StubAPIProxy {
    private static class MediaFile {
        public int id;
        public String title;
        public File file;
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
        
        if ("GetMediaFileForID".equals(cmd)) {
            return files.get(args[0]);
        }

        if ("GetMediaTitle".equals(cmd)) {
            return ((MediaFile)args[0]).title;
        }
        
        if ("AddMediaFile".equals(cmd)) {
            return addMediaFile((File) args[0]);
        }

        if ("GetMediaFiles".equals(cmd)) {
            return files.values().toArray(new MediaFile[files.size()]);
        }
        
        if ("GetMediaFileForFilePath".equals(cmd)) {
            for (MediaFile mf : files.values()) {
                if (mf.file.equals((File)args[0])) {
                    return mf;
                }
            }
            return null;
        }
        
        System.out.println("MediaFileAPIProxy: Unhandled: " + cmd);
        
        return null;
    }
    
    public MediaFile addMediaFile(File f) {
        MediaFile mf = new MediaFile();
        mf.title=f.getName();
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
        api.addProxy("GetMediaFileForFilePath", this);
        api.addProxy("GetMediaFiles", this);
    }
}
