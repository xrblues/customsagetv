package test;

import java.io.IOException;

import org.jdna.sage.media.SageBackupPersistenceUsingSageXmlInfo;
import org.jdna.sage.media.SageMediaFile;

import sagex.api.MediaFileAPI;

public class TestBackupPersistenceUsingSageXml {
    public static void main(String args[]) throws IOException {
        Object mf = MediaFileAPI.GetMediaFileForID(3822951);
        SageBackupPersistenceUsingSageXmlInfo save = new SageBackupPersistenceUsingSageXmlInfo();
        save.storeMetaData(null, new SageMediaFile(mf), null);
    }
}
