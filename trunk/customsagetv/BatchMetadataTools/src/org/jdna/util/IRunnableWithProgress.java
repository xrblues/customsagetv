package org.jdna.util;

public interface IRunnableWithProgress<T extends IProgressMonitor> {
    public void run(T monitor);
}
