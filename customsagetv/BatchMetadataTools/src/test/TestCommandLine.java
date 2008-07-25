package test;

import org.apache.log4j.Logger;
import org.jdna.cmdline.CommandLine;
import org.jdna.cmdline.CommandLineArg;
import org.jdna.cmdline.CommandLineProcess;

@CommandLineProcess(acceptExtraArgs=true, description="Testing the Commandline Output")
public class TestCommandLine {
	private static final Logger log = Logger.getLogger(TestCommandLine.class);
	
	@CommandLineArg(name="arg1", description="Sets the Command line Arg1", required=true)
	public void setArg1(String arg) {
		System.out.printf("Setting arg1 %s\n", arg);
	}
	
	@CommandLineArg(name="arg2", description="Sets the Command line Arg2", required=false)
	public void setAnotherArg(String arg) {
		System.out.printf("Setting arg2 %s\n", arg);
	}
	
	public void setNoArg() throws Exception {
		throw new Exception("Set NO Arg should not be called!");
	}
	
	public static void main(String[] args) throws Exception {
		log.info("Testing Command Line");
		CommandLine cl = new CommandLine(args);
		cl.process();
		
		System.out.println("Named Args------------");
		for (String k : cl.getArgSet()) {
			System.out.printf("Arg: %20s = %s\n", k, cl.getArg(k));
		}
		
		System.out.println("Extra Args------------");
		String extra[] = cl.getExtraArgs();
		for (String s : extra) {
			System.out.printf("Extra Arg: %s\n", s);
		}
		
		TestCommandLine tcl = new TestCommandLine();
		System.out.println("Will Now Attempt to call applyToAnnotatedObject()");
		cl.applyToAnnotated(tcl);
		
		System.out.println("Will Now Attempt to call help");
		cl.help("TestCommandLine", tcl);
		
		System.out.println("Done");
	}
}
