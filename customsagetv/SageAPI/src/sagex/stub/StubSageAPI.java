package sagex.stub;

import sagex.ISageAPIProvider;

public class StubSageAPI implements ISageAPIProvider {

	public Object callService(String name, Object[] args) {
		System.out.printf("Calling: %s\n", name);
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				System.out.printf("Arg[%d]: %s\n", i, args[i]);
			}
		}
		if ("GetMediaFiles".equals(name)) {
			return new String[] {"MediaFile1", "MediaFile2", "MediaFile3"};
		}
		return null;
	}
	
	public String toString() {
		return "sage://stub";
	}
}
