package org.jdna.metadataupdater;

import sagex.phoenix.progress.BasicProgressMonitor;

public class ConsoleProgressMonitor extends BasicProgressMonitor {
    public ConsoleProgressMonitor() {
    }

    /* (non-Javadoc)
     * @see sagex.phoenix.progress.BasicProgressMonitor#beginTask(java.lang.String, int)
     */
    @Override
    public void beginTask(String name, int worked) {
        super.beginTask(name, worked);
        System.out.println(name);
    }

    /* (non-Javadoc)
     * @see sagex.phoenix.progress.BasicProgressMonitor#setTaskName(java.lang.String)
     */
    @Override
    public void setTaskName(String name) {
        super.setTaskName(name);
        System.out.println(name);
    }
}
