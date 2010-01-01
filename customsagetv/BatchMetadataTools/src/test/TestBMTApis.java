package test;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.jdna.media.metadata.IMediaMetadata;
import org.jdna.media.metadata.IMediaMetadataProvider;
import org.jdna.media.metadata.IMediaSearchResult;
import org.jdna.media.metadata.IProviderInfo;
import org.jdna.media.metadata.MediaMetadataFactory;
import org.jdna.media.metadata.MetadataConfiguration;
import org.jdna.media.metadata.PersistenceOptions;
import org.jdna.sage.media.SageShowPeristence;

import sagex.SageAPI;
import sagex.api.AiringAPI;
import sagex.api.MediaFileAPI;
import sagex.api.ShowAPI;
import sagex.phoenix.configuration.proxy.GroupProxy;
import sagex.phoenix.fanart.IMetadataSearchResult;

public class TestBMTApis {
    public static void main(String args[]) throws Exception {
        BasicConfigurator.configure();
        SageAPI.setProvider(SageAPI.getRemoteProvider());
        // testFindSearchResults();
        //testRemoveShowMetadata();
        //testUpdateShow();
        testShowTitles();
    }

    private static void testShowTitles() {
        Object smf = MediaFileAPI.GetMediaFileForFilePath(new File("/var/media/tv/Futurama-ISecondThatEmotion-2559577-0.ts"));
        System.out.println("Title: " + AiringAPI.GetAiringTitle(smf));
        System.out.println("Show Title: " + ShowAPI.GetShowTitle(smf));
        System.out.println("Show Title: " + ShowAPI.GetShowEpisode(smf));
    }

    private static void testUpdateShow() throws Exception {
        Object smf = MediaFileAPI.GetMediaFileForFilePath(new File("/var/media/tv/Futurama-ISecondThatEmotion-2559577-0.ts"));

        IMetadataSearchResult[] results = phoenix.api.GetMetadataSearchResults(smf);
        IMediaMetadataProvider prov = MediaMetadataFactory.getInstance().getProvider(results[0].getProviderId(), results[0].getMediaType());
        IMediaMetadata md = prov.getMetaData((IMediaSearchResult) results[0]);

        GroupProxy.get(MetadataConfiguration.class).setImportTVAsRecordedShows(true);
        
        SageShowPeristence sp = new SageShowPeristence();
        PersistenceOptions options = new PersistenceOptions();
        options.setOverwriteMetadata(true);
        sp.storeMetaData(md, phoenix.api.GetMediaFile(smf), options);
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
