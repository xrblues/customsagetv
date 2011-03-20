package org.jdna.sagetv.networkencoder.util;

import java.io.File;

public interface IFileEncoder {
    public void encode(final File src, final File dest) throws Exception;
    public boolean isRunning();
    public void stop();
}
