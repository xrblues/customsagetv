package org.jdna.cmdline;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Simple Commandline handler for parsine the commandline and providing access.
 * 
 * Use CommandLine.process() to process the initial args, then call getArg(), etc after the process() has completed.
 * 
 * @author seans
 *
 */
public class CommandLine {
	private static final Logger log = Logger.getLogger(CommandLine.class);

	/**
	 * Assign this as the argument name in a CommandLineArg annotation, and it's method will recieve the extra arguments that are passed on the command line.
	 * @value
	 */
	public static final String EXTRA_ARGS_CLA = "EXTRAARGS";

	private String args[];
	private Map<String, Object> map;
	private List<String> extra;

	private String commandTitle;

	private String command;

	private String description;

	/**
	 * Create a CommandLine from the passed args
	 * 
	 * @param commandTitle Simple title for the command (used for help)
	 * @param command String that shows the command usage (used for help)
	 * @param args commandline args (these will be processed)
	 * @param description Describes the command (used for help)
	 */
	public CommandLine(String commandTitle, String command, String args[]) {
		this.commandTitle = commandTitle;
		this.command = command;
		this.args = args;
	}

	/**
	 * return all args that were passed on the commandline.
	 * @return
	 */
	public String[] getArgs() {
		return args;
	}

	/**
	 * Gets a named arg.  Args are passed as --NAME=VALUE, so calling getArg("NAME") would return value
	 * 
	 * @param name Named commandline arg
	 * 
	 * @return value or null if the arg does not exist.
	 */
	public Object getArg(String name) {
		return map.get(name);
	}

	/**
	 * Returns a set of all of the named args passed on the commandline.
	 * @return
	 */
	public Set<String> getArgSet() {
		return map.keySet();
	}
	
	/**
	 * Convenience method for getting a string arg
	 * @param name
	 * @return
	 */
	public Object getStringArg(String name) {
		return map.get(name);
	}
	
	/**
	 * Convenience method for getting a boolean arg.  A boolean arg is passed as --NAME or --NAME=true|false
	 * 
	 * @param arg
	 * @return
	 */
	public boolean getBooleanArg(String arg) {
		return (Boolean) map.get(arg);
	}

	/**
	 * Test if an arg exists
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasArg(String name) {
		return map.get(name)!=null;
	}

	/**
	 * This returns all of the unnamed args.  ie, if you passed "--OPT=VALUE test1 test2" then this would return an array with test1 and test2 only.
	 * 
	 * @return
	 */
	public String[] getExtraArgs() {
		return extra.toArray(new String[extra.size()]);
	}

	/**
	 * Process has to be called before you can access the args.
	 * 
	 * @throws Exception
	 */
	public void process() throws Exception {
		extra = new ArrayList<String>();
		map = new HashMap<String, Object>();
		for (String a : args) {
			if (a.startsWith("--")) {
				a = a.substring(2);
				int pos = a.indexOf("=");
				if (pos == -1) {
					map.put(a, Boolean.TRUE);
				} else {
					String n = a.substring(0, pos);
					Object v = a.substring(pos + 1);
					if ("true".equals(v)) {
						v = Boolean.TRUE;
					} else if ("false".equals(v)) {
						v = Boolean.FALSE;
					}
					log.debug("Adding Mapped Arg: ["+n+"]=[]"+v);
					map.put(n, v);
				}
			} else if (a.startsWith("-")) {
				log
						.error("CommandLine does not support single - args! Skipping: "
								+ a);
				throw new Exception(
						"CommandLine does not support single args! " + a);
			} else {
				log.debug("Adding Unamed Arg: " + a);
				extra.add(a);
				
			}
		}
	}
	
	/**
	 * Applies the command line parameters to a passed object.  for each commandline arg, in the form, --ARG=VALUE, it will call object.setARG(VALUE);
	 * Also if your object has a setExtraArgs(String[]) method, then it will call that method with the extra args.
	 * 
	 * This methos is handy for populating an object from the command line.
	 * 
	 * @param o
	 * @throws Exception
	 */
	public void applyTo(Object o) throws Exception {
		Method[] methods = o.getClass().getDeclaredMethods();
		Map<String, Method> names = new HashMap<String, Method>();
		for (Method m : methods ) {
			String name = m.getName();
			if (name.startsWith("set")) {
				name = name.substring(3).toLowerCase();
				log.debug("Adding Method: " + name);
				names.put(name, m);
			} 
		}
		
		// now for each method, see if there is an arg to apply
		for (String key : map.keySet()) {
			Method m = names.get(key.toLowerCase());
			if (m==null) {
				log.warn("appyTo(): Object does not contain a method for: " + key);
			} else {
				try {
					m.invoke(0, map.get(key));
				} catch (Exception e) {
					log.error("Failed to apply command line to the passed object: " + o.getClass().getName(), e);
					throw e;
				}
			}
		}
		
		// now see if the object has method for extraargs, if so, then call it
		Method m = names.get("extraargs");
		if (m!=null) {
			try {
				m.invoke(o, (Object)getExtraArgs());
			} catch (Exception e) {
				log.error("Object contained setExtraArgs() method but did not like the String[] package.");
				throw e;
			}
		}
	}
	
	/**
	 * Similar to applyToObject, but this method will find all annotated methods of type CommandLineArg and pass the named arg to the method.
	 * For example,
	 * <pre>
	  	@CommandLineArg(name = "update", description = "Update Missing AND Updated Metadata. (default false)")
		public void setUpdate(boolean b) {
			this.update = b;
		}
	 * </pre>
	 * If the annotated object contained the above method, and --update was passed on the commandline, then the setUpdate method would be called on the annotated object.
	 * 
	 * Annotated Object should also use the CommandLineProcess annotation on the class.
	 * 
	 * @param o
	 * @throws Exception
	 */
	public void applyToAnnotated(Object o) throws Exception {
		for (Method m : o.getClass().getDeclaredMethods()) {
			CommandLineArg cla = m.getAnnotation(CommandLineArg.class);
			if (cla !=null) {
				// check for special cla annotations and extraargs
				if ("EXTRAARGS".equals(cla.name())) {
					try {
						m.invoke(o, (Object)getExtraArgs());
					} catch (Exception e) {
						System.out.printf("Failed to set EXTRAARGS in method %s.  Check that method supports String[] parameter\n", m.getName());
						throw e;
					}
				}
				
				Object val = map.get(cla.name());
				if (val==null && cla.required()) throw new Exception("Missing Required Arg: " + cla.name());
				if (val!=null) {
					try {
						m.invoke(o, val);
					} catch (Exception e) {
						System.out.printf("Failed while applying arg: %s (%s) to method: %s\n", cla.name(), val, m.getName());
						throw e;
					}
				} else {
					log.warn("Missing potential arg: " + cla.name());
				}
			}
		}
	}

	/**
	 * Same as helo(String command, Object o, Throwable t) but assumes the Throwable is null.
	 *  
	 * @param command
	 * @param o
	 */
	public void help(Object o) {
		help(o, null);
	}
	
	/**
	 * If you pass an annotated object, it will create a help page for that object using the annotations.
	 * 
	 * @param command Description of the command
	 * @param o Annotated object
	 * @param e if not null, then it will print out the error.
	 */
	public void help(Object o, Throwable e) {
		if (commandTitle!=null) {
			System.out.printf("%s\n", commandTitle);
		}
		
		CommandLineProcess clp = o.getClass().getAnnotation(CommandLineProcess.class);
		System.out.printf("%s\n", clp.description());
		
		if (clp==null) {
			System.out.println("help() called on non-annotated class:"+o.getClass().getName()+ ".\nBe sure to add @CommandLineProcess and @CommandLineArg annotations to to your class.");
			return;
		}
		if (e!=null) {
			System.out.printf("ERROR: %s\n", e.getMessage());
		}
		System.out.printf("\nUsage:");
		System.out.printf("%s OPTIONS %s\n", command, (clp.acceptExtraArgs() ? "..." : ""));
		System.out.println("");
		System.out.println("OPTIONS: (* are required)");
		for (Method m : o.getClass().getDeclaredMethods()) {
			CommandLineArg cla = m.getAnnotation(CommandLineArg.class);
			if (cla!=null) {
				if (EXTRA_ARGS_CLA.equals(cla.name())) continue;
				System.out.printf("%s --%-20s %s\n", (cla.required()?"*":" "), cla.name(), cla.description());
			}
		}
	}

	public boolean hasArgs() {
		return args != null && args.length>0;
	}
}
