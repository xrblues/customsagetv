package org.jdna.sagetv.networkencoder.util;

public interface IStreamListener {
    public void onMatch(String data);
    public String getMatcher();
}
