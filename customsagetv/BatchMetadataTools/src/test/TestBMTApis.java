package test;

import org.jdna.media.metadata.IProviderInfo;

public class TestBMTApis {
    public static void main(String args[]) {
        System.out.println("Current Ids: " + bmt.api.GetCurrentMetadataProviderIds());

        //bmt.api.AddDefaultMetadataProvider("imdb.xml");
        
        IProviderInfo pi[] = bmt.api.GetUninstalledMetadataProviders();
        for (IProviderInfo mi : pi) {
            System.out.println("Not Installed: " + mi.getId());
        }
        
        System.out.println("Current Ids: " + bmt.api.GetCurrentMetadataProviderIds());
    }
}
