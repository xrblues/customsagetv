package org.jdna.sagetv.networkencoder.impl.dvb.linux.cmdline;

public class WaitFor {
    public WaitFor() {
    }
    
    public boolean isDoneWaiting() {
        return false;
    }
    
    public void waitFor(final long howLong, final int checkInterval) {
        long timeout = System.currentTimeMillis()+howLong;
        while (!isDoneWaiting() && System.currentTimeMillis()<timeout) {
            try {
                Thread.sleep(checkInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
