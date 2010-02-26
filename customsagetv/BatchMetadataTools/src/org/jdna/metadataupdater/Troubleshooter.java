package org.jdna.metadataupdater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.IOUtils;

public class Troubleshooter {
    public static class StubFileZipper extends DirectoryWalker {
        ZipOutputStream zos;
        public StubFileZipper(ZipOutputStream zos) {
            super();
            this.zos=zos;
        }

        public List zip(File startDirectory) throws IOException {
            List results = new ArrayList();
            walk(startDirectory, results);
            return results;
        }

        protected void handleFile(File file, int depth, Collection results) {
            Pattern p = Pattern.compile("\\.vob|\\.bdmv|\\.m2ts|\\.avi|\\.mkv|\\.mpg|\\.divx|\\.m4v", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(file.getName());
            if (file.isFile() && !m.find()) {
                try {
                    addStubFile(zos, file);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        private void addStubFile(ZipOutputStream zos2, File file) throws Exception {
            ZipEntry ze = new ZipEntry(file.getPath());
            zos.putNextEntry(ze);
            zos.write(("StubFile: " + file.getAbsolutePath()).getBytes());
            zos.closeEntry();
        }
    }

    public static File createSupportZip(String problemDescription, boolean includeLogs, boolean includeProps, List<File> locations) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        File output = new File("bmtsupport-"+sdf.format(Calendar.getInstance().getTime())+".zip");
        return createSupportZip(problemDescription, includeLogs, includeProps, locations, output);
    }
/**
     * Attempts to zip up various log files, props, and stub video locations for support purpsoses.
     * 
     * @param problemDescription
     * @param includeLogs
     * @param includeProps
     * @param locations
     * @throws Exception 
     */
    public static File createSupportZip(String problemDescription, boolean includeLogs, boolean includeProps, List<File> locations, File output) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output));
        if (problemDescription!=null) {
            ZipEntry ze = new ZipEntry("PROBLEM.txt");
            zos.putNextEntry(ze);
            zos.write(problemDescription.getBytes());
            zos.closeEntry();
        }
        
        if (includeLogs) {
            addFile(zos, new File("bmt.log"));
            addFile(zos, new File("phoenix.log"));
            addFile(zos, new File("mailcheck.log"));
            addFile(zos, new File("sagetv_0.txt"));
            addFile(zos, new File("log4j.properties"));
        }
        
        if (includeProps) {
            addFile(zos, new File("Sage.properties"));
        }
        
        if (locations!=null && locations.size()>0) {
            for (File loc: locations) {
                addStubLocation(zos, loc);
            }
        }
        zos.close();
        return output;
    }

    private static void addStubLocation(ZipOutputStream zos, File loc) throws IOException {
        if (loc.exists() && loc.isDirectory()) {
            StubFileZipper zipper = new StubFileZipper(zos);
            zipper.zip(loc);
        }
    }

    private static void addFile(ZipOutputStream zos, File file) throws Exception {
        if (file.exists() && file.isFile()) {
            ZipEntry ze = new ZipEntry(file.getPath());
            zos.putNextEntry(ze);
            FileInputStream fis = new FileInputStream(file);
            IOUtils.copy(fis, zos);
            fis.close();
            zos.closeEntry();
        }
    }
}
