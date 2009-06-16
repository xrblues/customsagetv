package org.jdna.media;

import java.io.File;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdna.util.FileCleaner;
import org.jdna.util.FileToucher;

import sagex.phoenix.fanart.FanartUtil;

public class FileHDFolderMediaFile extends FileMediaFile {
    private static Pattern dvdFileExtPattern = Pattern.compile("\\.vob$|\\.ifo$|\\.bup$", Pattern.CASE_INSENSITIVE);

    public FileHDFolderMediaFile(File file) {
        super(file);
    }

    public FileHDFolderMediaFile(URI uri) {
        this(new File(uri));
    }

    @Override
    public ContentType getContentType() {
        return ContentType.HDFOLDER;
    }

    @Override
    public Type getType() {
        return Type.File;
    }
    
    public static boolean isDVD(File f) {
        if (f.isDirectory()) {
            if (FanartUtil.isDVDFolder(f)) {
                return true;
            }

            // deep scan
            File files[] = f.listFiles();
            if (files == null) {
                return false;
            }

            for (File ff : files) {
                Matcher m = dvdFileExtPattern.matcher(ff.getName());
                if (m.find()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void delete() {
        FileCleaner.clean(getFile());
    }

    @Override
    public void touch() {
        FileToucher.touch(getFile(), System.currentTimeMillis());
    }
}
