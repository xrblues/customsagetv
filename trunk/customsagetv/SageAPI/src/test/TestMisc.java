package test;

import sagex.api.Global;
import sagex.api.UserRecordAPI;

public class TestMisc {
	public static void main(String args[]) {
		System.out.println(Global.GetServerAddress());

		System.out.println("Adding...");
		Object rec = UserRecordAPI.AddUserRecord("test://table1", "1");
		UserRecordAPI.SetUserRecordData(rec, "name", "sean2");
		UserRecordAPI.SetUserRecordData(rec, "age", "39");

		System.out.println("Fetching...");
		Object rec2 =  UserRecordAPI.GetUserRecord("test://table1", "1");
		System.out.println("Name: " + UserRecordAPI.GetUserRecordData(rec2, "name"));
		System.out.println("Age: " + UserRecordAPI.GetUserRecordData(rec2, "age"));
		
		System.out.println("Dumping...");
		String stores[] = UserRecordAPI.GetAllUserStores();
		for (String s: stores) {
			System.out.println("Store: " + s);
			Object recs[] = UserRecordAPI.GetAllUserRecords(s);
			for (Object r: recs) {
				System.out.println("Name: " + UserRecordAPI.GetUserRecordData(r, "name"));
			}
		}
		
		System.out.println("done");
	}
}
