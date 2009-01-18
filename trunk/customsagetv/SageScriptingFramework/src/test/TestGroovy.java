package test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class TestGroovy {
    public static void main(String args[]) throws Exception {
        testJavascript();
        testPython();
        testGroovy();
    }
    
    public static void testPython() throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByName("python");
        // evaluate JavaScript code from String
        engine.eval("print 'Hello Python, World!'");
    }

    public static void testGroovy() throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByName("groovy");
        // evaluate JavaScript code from String
        engine.eval("println \"Hello Groovy World!\"");
    }
    
    public static void testJavascript() throws Exception {
        ScriptEngineManager factory = new ScriptEngineManager();
        // create a JavaScript engine
        ScriptEngine engine = factory.getEngineByName("js");
        // evaluate JavaScript code from String
        engine.eval("println('Hello Javacript World')");
    }
}
