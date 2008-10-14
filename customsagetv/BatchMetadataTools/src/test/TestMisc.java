package test;

import java.io.File;

public class TestMisc {
	public static void main(String args[]) {
		File f = new File("/media/FileServer/Media/Videos/Magic Videos/");
		System.out.println(f.toURI().toString());
	}
}
