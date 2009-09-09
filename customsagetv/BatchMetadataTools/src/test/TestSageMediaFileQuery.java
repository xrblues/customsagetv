package test;

import org.jdna.sage.media.SageMediaFile;

import sagex.api.MediaFileAPI;

public class TestSageMediaFileQuery {
    public static void main(String args[]) {
        Object mf  = MediaFileAPI.GetMediaFileForID(3751436);
        SageMediaFile smf = new SageMediaFile(mf);
        System.out.println("Media Title: " + smf.getTitle());
    }
}
