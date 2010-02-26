package org.jdna.media;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sagex.phoenix.vfs.IMediaFile;
import sagex.phoenix.vfs.IMediaResource;
import sagex.phoenix.vfs.filters.IResourceFilter;
import sagex.phoenix.vfs.util.PathUtils;

/**
 * Exclude filter that will exclude based on the media resource name or path
 * 
 * @author seans
 *
 */
public class ExcludeMediaFilter implements IResourceFilter {
    private Pattern excludePattern = null;

    public ExcludeMediaFilter(Pattern p) {
        this.excludePattern=p;
    }

    public boolean accept(IMediaResource res) {
        if (res==null) {
            return false;
        }
        String path = null;
        String name = null;
        
        File f = null;
        if (res instanceof IMediaFile) {
            f = PathUtils.getFirstFile((IMediaFile) res);
        }
        
        if (f!=null) {
            path = f.getAbsolutePath();
            name = f.getName();
        } else {
            path = PathUtils.getLocation(res);
            name = PathUtils.getName(res);
        }
        
        Matcher m;
        if (path!=null) {
            m=excludePattern.matcher(path);
            if (m.find()) return false;
        }
        
        if (name!=null) {
            m = excludePattern.matcher(name);
            if (m.find()) return false;
        }
        
        return true;
    }
}
