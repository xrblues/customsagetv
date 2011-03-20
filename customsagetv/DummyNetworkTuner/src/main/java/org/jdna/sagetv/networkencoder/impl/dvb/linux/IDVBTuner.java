package org.jdna.sagetv.networkencoder.impl.dvb.linux;

import java.io.File;


public interface IDVBTuner {
	public void tune(final IDVBChannel chan, final File out) throws Exception;
	public void stop();
}
