package test;

import java.io.File;

import org.jdna.media.metadata.IProviderInfo;
import org.jdna.util.LoggerConfiguration;

import sagex.SageAPI;
import sagex.api.MediaFileAPI;
import sagex.phoenix.fanart.IMetadataSearchResult;

public class TestBMTApis {
    public static void main(String args[]) {
        LoggerConfiguration.configurePlugin();
        SageAPI.setProvider(SageAPI.getRemoteProvider());
        // testFindSearchResults();
        testRemoveShowMetadata();
    }

    public static void testRemoveShowMetadata() {
        Object smf = MediaFileAPI.GetMediaFileForFilePath(new File("/home/FileServer/Media/Videos/VideoCollection/Movies/New/Before Devil Knows.avi"));
        if (smf == null) {
            System.out.println("No Media!");
        } else {
            MediaFileAPI.MoveTVFileOutOfLibrary(smf);
        }
        System.out.println("done");
    }

    public static void testFindSearchResults() {
        Object smf = MediaFileAPI.GetMediaFileForFilePath(new File("/home/FileServer/Media/Videos/VideoCollection/Movies/New/Before Devil Knows.avi"));

        IMetadataSearchResult[] results = phoenix.api.GetMetadataSearchResults(smf);
        for (int i = 0; i < results.length; i++) {
            System.out.println("result: " + phoenix.api.GetMetadataSearchResultTitle(results[i]));
        }

        phoenix.api.UpdateMediaFileMetadata(smf, results[0]);
    }

    public static void testListInstalledProviders() {
        System.out.println("Current Ids: " + bmt.api.GetCurrentMetadataProviderIds());

        // bmt.api.AddDefaultMetadataProvider("imdb.xml");

        IProviderInfo pi[] = bmt.api.GetUninstalledMetadataProviders();
        for (IProviderInfo mi : pi) {
            System.out.println("Not Installed: " + mi.getId());
        }

        System.out.println("Current Ids: " + bmt.api.GetCurrentMetadataProviderIds());
    }
}
