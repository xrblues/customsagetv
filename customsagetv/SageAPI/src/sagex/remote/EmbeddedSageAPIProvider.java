package sagex.remote;

import java.lang.reflect.InvocationTargetException;

import sagex.ISageAPIProvider;

public class EmbeddedSageAPIProvider implements ISageAPIProvider {
    public EmbeddedSageAPIProvider() {
        try {
            Class.forName("sage.SageTV");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
	public Object callService(String name, Object[] args) throws Exception {
		return sage.SageTV.api(name, args);
	}

	public String toString() {
		return "sage://embedded";
	}

	public Object callService(String context, String name, Object[] args) throws Exception {
	    return sage.SageTV.apiUI(context, name, args);
	}
}
