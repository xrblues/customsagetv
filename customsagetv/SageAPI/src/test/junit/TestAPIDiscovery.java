package test.junit;

import sagex.ISageAPIProvider;
import sagex.SageAPI;
import sagex.stub.NullSageAPIProvider;
import junit.framework.TestCase;

public class TestAPIDiscovery extends TestCase {
    public void testDiscovery() {
        // for a bad provider that we cannot locate
        System.setProperty("sagex.SageAPI.remoteUrl", "rmi://192.168.1.121:9088");
        ISageAPIProvider prov = SageAPI.getProvider();
        assertNotNull(prov);
        assertTrue(prov instanceof NullSageAPIProvider);
    }
}
