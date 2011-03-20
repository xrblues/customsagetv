package org.jdna.sagetv.networkencoder.dummy;

import org.jdna.sagetv.networkencoder.AbstractNetworkEncoderService;
import org.jdna.sagetv.networkencoder.INetworkEncoder;
import org.jdna.sagetv.networkencoder.ServerConfiguration;

import sagex.phoenix.configuration.proxy.GroupProxy;

public class DummyNetworkEncoder extends AbstractNetworkEncoderService {
    public DummyNetworkEncoder() {
        super("dummy");
    }

    @Override
    protected INetworkEncoder createNetworkEncoder() {
    	ServerConfiguration config = GroupProxy.get(ServerConfiguration.class);
        return new DummyFileCopyEncoder(config.getRecordingFile());
    }
}
