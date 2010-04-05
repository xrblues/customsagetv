package org.jdna.metadataupdater;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jdna.media.BackupConfiguration;

import sagex.api.Configuration;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.util.SageTV;

public class Troubleshooter {
    private static final Logger log = Logger.getLogger(Troubleshooter.class);

    public static class StubFileZipper extends DirectoryWalker {
        ZipOutputStream zos;

        public StubFileZipper(ZipOutputStream zos) {
            super();
            this.zos = zos;
        }

        public List zip(File startDirectory) throws IOException {
            List results = new ArrayList();
            walk(startDirectory, results);
            return results;
        }

        protected void handleFile(File file, int depth, Collection results) {
            Pattern p = Pattern.compile("\\.vob|\\.bdmv|\\.m2ts|\\.avi|\\.mkv|\\.mpg|\\.divx|\\.m4v|\\.ts", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(file.getName());
            if (file.isFile() && m.find()) {
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
            zos.write("X".getBytes());
            zos.closeEntry();
        }
    }

    public static File createSupportZip(String problemDescription, boolean includeLogs, boolean includeProps, boolean includeImports) throws Exception {
        if (includeImports) {
            return createSupportZip(problemDescription, includeLogs, includeProps, Arrays.asList(Configuration.GetVideoLibraryImportPaths()));
        } else {
            return createSupportZip(problemDescription, includeLogs, includeProps, null);
        }
    }

    public static File createSupportZip(String problemDescription, boolean includeLogs, boolean includeProps, List<File> locations) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        File output = new File("bmtsupport-" + sdf.format(Calendar.getInstance().getTime()) + ".zip");
        return createSupportZip(problemDescription, includeLogs, includeProps, locations, output);
    }

    /**
     * Attempts to zip up various log files, props, and stub video locations for
     * support purpsoses.
     * 
     * @param problemDescription
     * @param includeLogs
     * @param includeProps
     * @param locations
     * @throws Exception
     */
    public static File createSupportZip(String problemDescription, boolean includeLogs, boolean includeProps, List<File> locations, File output) throws Exception {
        final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output));
        if (problemDescription == null) {
            problemDescription = "-- NO PROBLEM ENTERED --";
        }
        ZipEntry ze = new ZipEntry("PROBLEM.txt");
        zos.putNextEntry(ze);
        zos.write(problemDescription.getBytes());
        addString(zos, "\n");
        addString(zos, "    BMT Version:  " + Version.VERSION);
        addString(zos, "Phoenix Version:  " + phoenix.api.GetVersion());
        addString(zos, "  Sagex Version:  " + sagex.api.Version.GetVersion());
        addString(zos, "   Java Version:  " + System.getProperty("java.version"));
        addString(zos, "   Sage Version:  " + SageTV.getSageVersion());
        addString(zos, " Java Classpath:  " + System.getProperty("java.class.path"));
        addString(zos, "\n");
        zos.closeEntry();

        if (includeLogs) {
            File dir = new File(".");
            dir.listFiles(new FileFilter() {
                public boolean accept(File f) {
                    if (f.getName().endsWith(".log") || f.getName().endsWith("log4j.properties")) {
                        try {
                            // remove path info
                            addFile(zos, new File(f.getName()));
                        } catch (Exception e) {
                            log.warn("Could not add " + f + " to support zip.");
                        }
                    }
                    return false;
                }
            });
            addFile(zos, new File("sagetv_0.txt"));
            dir = new File("logs");
            if (dir.exists()) {
                dir.listFiles(new FileFilter() {
                    public boolean accept(File f) {
                        if (f.getName().endsWith(".log") || f.getName().endsWith("log4j.properties")) {
                            try {
                                addFile(zos, f);
                            } catch (Exception e) {
                                log.warn("Could not add " + f + " to support zip.");
                            }
                        }
                        return false;
                    }
                });
            }
        }

        if (includeProps) {
            addFile(zos, new File("Sage.properties"));
        }

        if (locations != null && locations.size() > 0) {
            for (File loc : locations) {
                addStubLocation(zos, loc);
            }
        }
        zos.close();
        return output;
    }

    private static void addString(ZipOutputStream zos, String string) {
        try {
            zos.write(string.getBytes());
            zos.write("\n".getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    public static void backupWizBin() throws Exception {
        BackupConfiguration cfg = GroupProxy.get(BackupConfiguration.class);
        File outdir = new File(cfg.getBackupFolder());
        if (!outdir.exists()) {
            log.debug("Creating Backup Folder: " + outdir.getAbsolutePath());
            if (!outdir.mkdirs()) {
                throw new IOException("Failed to create backup dir: " + outdir);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        File output = new File(outdir, "Wiz.bin-" + sdf.format(Calendar.getInstance().getTime()));
        if (output.exists()) throw new IOException("Backup file exists: " + output.getAbsolutePath());

        File input = new File("Wiz.bin");
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(input);
            fos = new FileOutputStream(output);
            IOUtils.copyLarge(fis, fos);
        } finally {
            if (fis != null) {
                IOUtils.closeQuietly(fis);
            }
            if (fos != null) {
                fos.flush();
                IOUtils.closeQuietly(fos);
            }
        }
    }
}
