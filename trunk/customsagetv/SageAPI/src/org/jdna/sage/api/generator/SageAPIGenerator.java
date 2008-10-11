package org.jdna.sage.api.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdna.sage.api.generator.MethodParser.MethodParam;
import org.jdna.sage.api.generator.MethodParser.SageMethod;

public class SageAPIGenerator {
	private static final Logger log = Logger.getLogger(SageAPIGenerator.class);

	private File srcDir;
	private String packageName;
	private String className;
	private File outFile;
	private File outDir;
	private PrintWriter pw = null;
	private List<SageMethod> methods;

	public SageAPIGenerator(File srcDir, String packageName, String className, List<SageMethod> methods) {
		this.srcDir = srcDir;
		this.packageName = packageName;
		this.className = className;
		this.methods = methods;

		outDir = new File(srcDir, packageName.replaceAll("\\.", "/") + "/");
	}

	public void generate() throws Exception {
		outDir.mkdirs();
		outFile = new File(outDir, className + ".java");

		log.info("Creating Java: " + outFile.getAbsolutePath());

		pw = new PrintWriter(new FileWriter(outFile));
		pw.printf("package %s;\n\n", packageName);

		pw.println("/**");
		pw.println(" * Unofficial SageTV Generated File - Never Edit");
		pw.println(" * Generated Date/Time: " + new SimpleDateFormat().format(new Date()));
		pw.printf(" * See Official Sage Documentation at <a href='http://download.sage.tv/api/sage/api/%s.html'>%s</a>\n", className, className);
		pw.println(" * This Generated API is not Affiliated with SageTV.  It is user contributed.");
		pw.println(" */");

		// pw.println("import org.apache.log4j.Logger;");

		pw.printf("public class %s {\n", className);

		// pw.printf("protected static final Logger log =
		// Logger.getLogger(%s.class);\n", className);
		for (SageMethod m : methods) {
			if (m.comment != null && m.comment.trim().length() > 0) {
				pw.println("/**");
				pw.println(m.comment.trim());
				pw.println(" */");
			}
			pw.printf("public static %s %s (%s) {\n", fixSageApi(m.returnType), m.name, createArgList(m.args));

			// build the body to call a sage service
			callSageSerice(pw, m);

			pw.println("}\n");
		}
		pw.println("}");

		pw.close();
		pw.flush();
	}

	private String fixSageApi(String name) {
		return name.replaceAll("sage\\.[^$\\[\\]]+", "Object");
	}

	private void callSageSerice(PrintWriter pw, SageMethod m) {
		String objArr = buildObjectArray(m);
		String retStr = buildReturnString(m);
		// pw.println("try {");
		pw.printf("   %s sagex.SageAPI.call(\"%s\", %s);\n", retStr, m.name, objArr);
		// pw.println("} catch (Throwable t) {");
		// pw.printf(" log.error(\"Failed to call Sage Service: %s\", t);",
		// m.name);
		// pw.println("}");
	}

	private String buildObjectArray(SageMethod m) {
		if (m.args.size() == 0)
			return "(Object[])null";

		StringBuffer sb = new StringBuffer("new Object[] {");
		for (int i = 0; i < m.args.size(); i++) {
			MethodParam mp = m.args.get(i);
			sb.append(mp.varName);
			if (i < m.args.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("}");
		return sb.toString();
	}

	private String buildReturnString(SageMethod m) {
		if (m.returnType.contains("void")) {
			return "";
		} else {
			return "return (" + makeObject(fixSageApi(m.returnType)) + ")";
		}
	}

	private String makeObject(String o) {
		if (o.contains("[]")) {
			return o;
		} else {
			o = o.replace("boolean", "Boolean");
			o = o.replace("long", "Long");
			o = o.replace("int", "Integer");
			o = o.replace("float", "Float");
			o = o.replace("double", "Double");
		}
		return o;
	}

	private String createArgList(List<MethodParam> args) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < args.size(); i++) {
			MethodParam p = args.get(i);
			sb.append(fixSageApi(p.dataType));
			sb.append(" ");
			sb.append(p.varName);
			if (i < args.size() - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}
}
