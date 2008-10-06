package sagex.remote;

import java.lang.reflect.InvocationTargetException;

import sagex.ISageAPIProvider;

public class EmbeddedSageAPIProvider implements ISageAPIProvider {
	public Object callService(String name, Object[] args) {
		try {
			return sage.SageTV.api(name, args);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	public String toString() {
		return "sage://embedded";
	}
}
