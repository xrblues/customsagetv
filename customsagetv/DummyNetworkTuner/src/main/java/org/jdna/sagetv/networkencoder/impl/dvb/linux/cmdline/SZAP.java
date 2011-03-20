package org.jdna.sagetv.networkencoder.impl.dvb.linux.cmdline;

import java.io.File;

import org.apache.log4j.Logger;
import org.jdna.sagetv.networkencoder.impl.dvb.linux.IDVBChannel;
import org.jdna.sagetv.networkencoder.impl.dvb.linux.IDVBTuner;
import org.jdna.sagetv.networkencoder.impl.dvb.linux.IDVBChannel.Field;

import sagex.phoenix.configuration.proxy.GroupProxy;

/**
 * Wrapper for the dbutils szap command line utility for tuning dvb channels.
 * 
 * @author seans
 *
 */
public class SZAP implements IDVBTuner {
    private Logger log = Logger.getLogger(this.getClass());
	private StreamConsumerThread stdoutThread = null;
	private StreamConsumerThread stderrThread  = null;
	private Process process = null;
	
	private SZAPConfiguration config = null;
	private boolean tuned = false;
	
	public SZAP() {
	    config = GroupProxy.get(SZAPConfiguration.class);
	}

	/**
	 * Tune the channel and setup the drv0 file for reading.
	 * 
	 * @param chan
	 * @param listener
	 * @throws Exception
	 */
	public void tune(final IDVBChannel chan, final File out) throws Exception {
	    // TODO: make mencoder a part of this...
	    
	    // if we are already tuned, then stop
		if (process!=null) {
			stop();
		}
		
		tuned = false;
		
		// find the channel # for this... that is compatible with szap...
		// /usr/bin/szap -a 1 -r -l DBS -n TSN
		String cmd[] = new String[] {
			config.getSzapPath(),
			"-a",
			String.valueOf(config.getDvbAdapter()),
			"-c",
			config.getChannelsConf(),
			"-r",
			"-l",
			config.getLnb(),
			chan.get(Field.NAME)
		};
		
		log.info("Running Process: " + toString(cmd, " "));
		process = Runtime.getRuntime().exec(cmd);
		
		stdoutThread = new StreamConsumerThread("szap(stdout)", process.getInputStream());
		stdoutThread.addListener(new IStreamListener() {
            @Override
            public String getMatcher() {
                return "FE_HAS_LOCK";
            }

            @Override
            public void onMatch(String data) {
                if (!tuned) {
                    tuned=true;
                    log.info("SZAP has lock on channel: " + chan);
		        } else {
		            log.debug("SZAP (stdout): " + data);
		        }
		    }
		});
		stdoutThread.start();
		
		stderrThread = new StreamConsumerThread("szap(stderr)", process.getErrorStream());
		stderrThread.addListener(new IStreamListener() {
            @Override
            public String getMatcher() {
                return null;
            }

            @Override
            public void onMatch(String data) {
                log.warn("SZAP (stderr): " + data);
            }
		});
		stderrThread.start();
		
		// wait until we tune or timeout
		WaitFor waitFor = new WaitFor() {
            @Override
            public boolean isDoneWaiting() {
                return tuned;
            }
		};
		waitFor.waitFor(10*1000, 100);
		
		if (!tuned) {
		    stop();
		    throw new Exception("Failed to tune channel: " + chan);
		}
	}

	public void stop() {
		if (process!=null) {
			process.destroy();
			process = null;
			stdoutThread.interrupt();
			stderrThread.interrupt();
		}
		
		log.warn("Tuner Stopped");
		
		// give it time to shutdown
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		    Thread.currentThread().interrupt();
		}
	}
	
	public String toString(Object[] arr, String sep) {
		StringBuffer sb = new StringBuffer();
	   for (int i=0;i<arr.length;i++) {
		   sb.append(arr[i]).append(sep);
	   }
	   return sb.toString();
	}

	public String toString() {
	    return "szap tuner["+((config!=null)?config.getSzapPath():"")+"]";
	}
}
