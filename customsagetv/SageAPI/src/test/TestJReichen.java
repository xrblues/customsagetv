package test;

import sagex.api.Database;

public class TestJReichen {
	public static void main(String args[]) {
		System.setProperty("sagex.SageAPI.remoteUrl", "http://mediaserver:8081/sagex/rpcJava");
		System.out.println("get titles");
		String[] titles = null;
		titles = (String[]) sagex.SageAPI.call("GetAllTitles", null);
		System.out.println("titles: " + titles.length);

		System.out.println("sorting titles");
		titles =(String[]) Database.Sort(titles, true, null);
		System.out.println("done sort titles");
		
		//Database.Sort(titles, true, "Natural");
		//titles = (String[]) sagex.SageAPI.call("Sort", params);

		//for (String title : titles){
		//  System.out.println(title);
		//}
	}
}
