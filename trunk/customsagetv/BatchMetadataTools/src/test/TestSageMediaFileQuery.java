package test;

import sagex.api.MediaFileAPI;
import sagex.phoenix.vfs.IMediaFile;

public class TestSageMediaFileQuery {
    public static void main(String args[]) {
        Object mf  = MediaFileAPI.GetMediaFileForID(3751436);
        IMediaFile smf = phoenix.api.GetMediaFile(mf);
        System.out.println("Media Title: " + smf.getTitle());
    }
}
