package org.jdna.sage.media;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataPersistence;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.sage.io.SageXmlWriter;

import sagex.api.MediaFileAPI;
import sagex.phoenix.util.FileUtils;
import sagex.phoenix.vfs.IMediaResource;

public class SageBackupPersistenceUsingSageXmlInfo implements IMediaMetadataPersistence {
    private Logger log = Logger.getLogger(SageBackupPersistenceUsingSageXmlInfo.class);
    
    public SageBackupPersistenceUsingSageXmlInfo() {
    }

    public String getDescription() {
        return "Creates a backup xml file for a given SageTV MediaFile object";
    }

    public String getId() {
        return "sagexml";
    }

    public IMediaMetadata loadMetaData(IMediaResource mediaFile) {
        log.info("loadMetadata() not implemented.");
        return null;
    }

    public void storeMetaData(IMediaMetadata md, IMediaResource mediaFile, PersistenceOptions options) throws IOException {
        try {
            Object sageFile = phoenix.api.GetSageMediaFile(mediaFile);
            if (sageFile != null) {
                if (!MediaFileAPI.IsTVFile(sageFile)) {
                    log.debug("Not a TV File: " + sageFile);
                    return;
                }
                
                SageXmlWriter writer = new SageXmlWriter();
                writer.addMediaFile(sageFile);
                
                File f = getBackupFile(sageFile);
                writer.write(f, "UTF8");
            } else {
                log.info("Can't use " + SageBackupPersistenceUsingSageXmlInfo.class.getName() + " on non sage media items.");
            }
        } catch (Throwable t) {
            
        }
    }

    private File getBackupFile(Object mediaFile) {
        File f = MediaFileAPI.GetFileForSegment(mediaFile, 0);
        String name = f.getName();
        
        File dir = new File("backup/TV/");
        if (!dir.exists()) {
            FileUtils.mkdirsQuietly(dir);
        }

        int index=1;
        File newFile = null;
        while (newFile==null) {
            newFile = getBackupFileForIndex(dir, name, index++);
        }
        
        return newFile;
    }
    
    private File getBackupFileForIndex(File dir, String filename, int index) {
        File newFile = new File(dir, filename + "." + index + ".xml");
        if (newFile.exists()) return null;
        return newFile;
    }
}
