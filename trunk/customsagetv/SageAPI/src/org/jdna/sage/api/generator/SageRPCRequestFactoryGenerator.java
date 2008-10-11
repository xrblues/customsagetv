package org.jdna.sage.api.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.sage.api.generator.Main.ClassMetadata;

public class SageRPCRequestFactoryGenerator {
	private static final Logger log = Logger.getLogger(SageRPCRequestFactoryGenerator.class);

	private File srcDir;
	private String packageName;
	private String className;
	private File outFile;
	private File outDir;
	private PrintWriter pw = null;
	private List<ClassMetadata> allClasses;

	public SageRPCRequestFactoryGenerator(File srcDir, String packageName, String className, List<ClassMetadata> allClasses) {
		this.srcDir = srcDir;
		this.packageName = packageName;
		this.className = className;
		this.allClasses = allClasses;

		outDir = new File(srcDir, packageName.replaceAll("\\.", "/") + "/");
	}

	public void generate() throws Exception {
		outDir.mkdirs();
		outFile = new File(outDir, className + ".java");

		System.out.printf("Generating %s.%s\n", packageName, className);
		log.info("Creating Java: " + outFile.getAbsolutePath());

		pw = new PrintWriter(new FileWriter(outFile));
		pw.printf("package %s;\n\n", packageName);

		pw.println("/**");
		pw.println(" * Unofficial SageTV Generated File - Never Edit");
		pw.println(" * Generated Date/Time: " + new SimpleDateFormat().format(new Date()));
		pw.printf(" * See Official Sage Documentation at <a href='http://download.sage.tv/api/sage/api/%s.html'>%s</a>\n", className, className);
		pw.println(" * This Generated API is not Affiliated with SageTV.  It is user contributed.");
		pw.println(" */");

		pw.println("\nimport java.util.Map;");
		pw.println("import sagex.remote.RemoteRequest;");

		pw.printf("public class %s {\n", className);
		pw.println("   public static RemoteRequest createRequest(String context, String api, String command, String[] parameters) {");
		for (ClassMetadata cm : allClasses) {
			pw.printf("      if (\"%s\".equals(api)) {\n", cm.name);
			pw.printf("         return %sFactory.createRequest(context, command, parameters);\n", cm.name);
			pw.println("      }");
		}
		pw.printf("      throw new RuntimeException(\"Invalid %s Command: \"+command);\n", className);
		pw.println("   }\n\n");
		pw.println("}");

		pw.close();
		pw.flush();
	}
}
