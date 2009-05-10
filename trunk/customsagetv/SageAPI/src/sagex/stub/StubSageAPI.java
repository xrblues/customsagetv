package sagex.stub;

import java.util.HashMap;
import java.util.Map;

import sagex.ISageAPIProvider;

public class StubSageAPI implements ISageAPIProvider {
    private boolean debugCalls = false;
    private Map<String,Object> calls = new HashMap<String, Object>();

    public StubSageAPI() {
    }
    
    public void addCall(String call, Object result) {
        calls.put(call, result);
    }
    
	public Object callService(String name, Object[] args) throws Exception {
	    if (debugCalls) {
    		System.out.printf("Calling: %s\n", name);
    		if (args != null) {
    			for (int i = 0; i < args.length; i++) {
    				System.out.printf("Arg[%d]: %s\n", i, args[i]);
    			}
    		}
	    }
	    Object o = calls.get(name);
	    if (o==null) {
	        System.out.printf("provider.addCall(\"%s\",\"\"); // call %s not set\n", name, name);
	    }
		return o;
	}

	public String toString() {
		return "sage://stub";
	}

	public Object callService(String context, String name, Object[] args) throws Exception {
		return callService(name, args);
	}

    public void setDebugCalls(boolean debugCalls) {
        this.debugCalls = debugCalls;
    }
}
