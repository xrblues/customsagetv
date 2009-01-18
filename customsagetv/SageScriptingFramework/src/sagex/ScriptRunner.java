package sagex;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptRunner {
    public static void main(String args[]) throws Exception {
        if (args == null || args.length==0) {
            System.out.println("Missing script!");
            throw new Exception("Missing script!");
        }
        
        String script = args[0];
        String cArgs[] = new String[0];
        if (args.length>1) {
            cArgs = new String[args.length-1];
            for (int i=1;i<args.length;i++) {
                cArgs[i-1] = args[i];
            }
        }
        
        // python overrides
        File py = new File("libs/pycache");
        if (!py.exists()) py.mkdirs();
        System.setProperty("python.cachedir", py.getAbsolutePath());
        
        // find the script engine based on extension
        String scriptType = script.substring(script.lastIndexOf(".")+1);
        if ("py".equals(scriptType)) scriptType = "python";
        ScriptEngineManager factory = new ScriptEngineManager(ScriptRunner.class.getClassLoader());
        ScriptEngine engine = factory.getEngineByName(scriptType);
        if (engine==null) {
            System.out.println("Unknown Script Engine: " + scriptType);
            throw new Exception("Unknown Script Engine: " + scriptType);
        }
        
        engine.put("SCRIPT_NAME", script);
        engine.put("SCRIPT_ARGS", cArgs);
        engine.eval(new InputStreamReader(new FileInputStream(new File(script))));
    }
}
