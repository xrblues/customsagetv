package org.jdna.sagetv.networkencoder.impl.dvb.linux.cmdline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class StreamConsumerThread extends Thread {
    private Logger                log       = Logger.getLogger(this.getClass());
    private BufferedReader        stream    = null;
    private List<IStreamListener> listeners = new ArrayList<IStreamListener>();

    public StreamConsumerThread(String name, InputStream is) {
        setName(name);
        this.stream = new BufferedReader(new InputStreamReader(is));
    }

    public void addListener(IStreamListener l) {
        listeners.add(l);
    }

    public void run() {
        String l = null;
        while (stream != null) {
            try {
                if (stream.ready()) {
                    l = stream.readLine();
                    if (l != null) {
                        for (IStreamListener lnr : listeners) {
                            if (lnr.getMatcher() != null) {
                                if (l.contains(lnr.getMatcher())) {
                                    lnr.onMatch(l);
                                }
                            } else {
                                lnr.onMatch(l);
                            }
                        }
                    }
                }
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    log.warn(getName() + ": Stream Consumer has been interrupted.");
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (IOException e) {
                log.warn(getName() + ": Error in Stream Consumer", e);
            }
        }
        log.info(getName() + ": Stream consumer is shutting down...");
    }

    public void cancel() {
        interrupt();
        listeners.clear();
        stream = null;
    }
}
