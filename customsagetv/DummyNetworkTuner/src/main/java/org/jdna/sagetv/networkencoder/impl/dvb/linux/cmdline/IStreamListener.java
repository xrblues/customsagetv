package org.jdna.sagetv.networkencoder.impl.dvb.linux.cmdline;

public interface IStreamListener {
    public void onMatch(String data);
    public String getMatcher();
}
