package test;

import sagex.api.Global;
import sagex.api.MediaFileAPI;

public class TestMisc {
	public static void main(String args[]) {
		System.out.println(Global.GetServerAddress());

		Object ob [] = MediaFileAPI.GetMediaFiles();
		for (Object o : ob) {
			System.out.println("MFNAME: " + MediaFileAPI.GetMediaTitle(o));
		}
		MediaFileAPI.GetMediaFiles();
		
		ob = MediaFileAPI.GetMediaFiles();
		for (int i=1;i<10;i++) {
			System.out.println("GetName: " + MediaFileAPI.GetMediaTitle(ob[i]));
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Global.IsClient();
		Global.IsServerUI();
		
		System.out.println("done");
	}
}


