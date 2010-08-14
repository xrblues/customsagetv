package test;

import sagex.api.Global;
import sagex.api.MediaFileAPI;

public class TestMisc {
	public static void main(String args[]) {
		System.out.println(Global.GetServerAddress());
		
		Object mf = MediaFileAPI.GetMediaFileForID(187125);
		System.out.println("Season: " + MediaFileAPI.GetMediaFileMetadata(mf, "SeasonNumber"));
		System.out.println("XXX: " + MediaFileAPI.GetMediaFileMetadata(mf, "XXX"));
		System.out.println("YYY: " + MediaFileAPI.GetMediaFileMetadata(null, "YYY"));
		System.out.println("null1: " + MediaFileAPI.GetMediaFileMetadata(null, null));
		System.out.println("null2: " + MediaFileAPI.GetMediaFileMetadata(mf, null));
		
		System.out.println("done");
	}
}
