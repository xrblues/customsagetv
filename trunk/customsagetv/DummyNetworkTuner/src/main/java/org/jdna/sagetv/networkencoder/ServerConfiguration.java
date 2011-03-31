package org.jdna.sagetv.networkencoder;

import org.jdna.sagetv.networkencoder.dummy.DummyNetworkEncoder;

import sagex.phoenix.configuration.proxy.AField;
import sagex.phoenix.configuration.proxy.AGroup;
import sagex.phoenix.configuration.proxy.FieldProxy;
import sagex.phoenix.configuration.proxy.GroupProxy;

@AGroup(label="Dummy Network Tuner ", path = "jdna/networktuner/dummy", description = "Dummy Network Tuner")
public class ServerConfiguration extends GroupProxy {
    @AField(label="Port", description = "Network Encoder Port")
    private FieldProxy<Integer>  port = new FieldProxy<Integer>(5000);

    @AField(label="Default Encoder Class", description = "Default Encoder Class", visible=false)
    private FieldProxy<String>  defaultEncoderClass   = new FieldProxy<String>(DummyNetworkEncoder.class.getName());

    @AField(label="Enable Discovery?", description = "If true, then the sage discovery server will be started.")
    private FieldProxy<Boolean>  discoveryEnabled   = new FieldProxy<Boolean>(true);

    @AField(label="Dummy Recording File", description = "Full path to a video file that will be used by the encoder when a recording is requested.  Should be as large as the longest show you will dummy record.", editor="fileChooser")
    private FieldProxy<String>  recordingFile   = new FieldProxy<String>("dummy-recording.mpg");

    @AField(label="Device Name", description = "Just a human readable device name")
    private FieldProxy<String>  deviceName   = new FieldProxy<String>("DummyTuner");
    
    public ServerConfiguration() {
        super();
        init();
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port.get();
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port.set(port);
    }

    /**
     * @return the defaultEncoderClass
     */
    public String getDefaultEncoderClass() {
        return defaultEncoderClass.get();
    }

    /**
     * @param defaultEncoderClass the defaultEncoderClass to set
     */
    public void setDefaultEncoderClass(String defaultEncoderClass) {
        this.defaultEncoderClass.set(defaultEncoderClass);
    }

    /**
     * @return the discoveryEnabled
     */
    public boolean getDiscoveryEnabled() {
        return discoveryEnabled.get();
    }

    /**
     * @param discoveryEnabled the discoveryEnabled to set
     */
    public void setDiscoveryEnabled(Boolean discoveryEnabled) {
        this.discoveryEnabled.set(discoveryEnabled);
    }

	public void setRecordingFile(String recordingFile) {
		this.recordingFile.set(recordingFile);
	}

	public String getRecordingFile() {
		return recordingFile.get();
	}

	public void setDeviceName(String deviceName) {
		this.deviceName.set(deviceName);
	}

	public String getDeviceName() {
		return deviceName.get();
	}
}
