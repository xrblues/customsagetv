package test;

import sagex.remote.SageRPCServerRunner;

public class TestHTTPD {
	public static void main(String args[]) throws InterruptedException {
		SageRPCServerRunner run = new SageRPCServerRunner();
		run.run();

		System.out.println("Ctrl+C to kill server...");
		while (true)
			Thread.currentThread().sleep(5000);
	}
}
