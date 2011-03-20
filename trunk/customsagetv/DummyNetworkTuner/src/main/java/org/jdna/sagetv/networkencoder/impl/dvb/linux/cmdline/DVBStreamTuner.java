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
public class DVBStreamTuner implements IDVBTuner {
    private Logger log = Logger.getLogger(this.getClass());
	private StreamConsumerThread stderrThread  = null;
	private Process process = null;
	
	private SZAPConfiguration config = null;
	private File tunedFile = null;
	
	private enum State {Idle, Tuned, Failed}
	
	private State state = State.Idle; 
	
	public DVBStreamTuner() {
	    config = GroupProxy.get(SZAPConfiguration.class);
	}
	
	/**
	 * Tune the channel and setup the drv0 file for reading.
	 * 
	 * @param chan
	 * @param listener
	 * @throws Exception
	 */
	public void tune(final IDVBChannel chan, File output) throws Exception {
	    tunedFile = output;
	    
	    // if we are already tuned, then stop
		if (process!=null) {
			stop();
		}
		
		state = State.Idle;
		
		String lnb[] = config.getLnb().split(",");
		String lof1 = lnb[0];
		String lof2 = lnb[1];
		String switchvalue = lnb[2];
		
		// tune using dvbstream
		// ./dvbstream -o:test.ts  -f 12267 -p H -s 20000 -c 1 -I 2 -L1 11250 -L2 11250 -SL 12700 5154 5155
		String cmd[] = new String[] {
			config.getDvbStreamPath(),
			"-o:" + tunedFile.getAbsolutePath(),
			"-c",
			String.valueOf(config.getDvbAdapter()),
			"-f",
			chan.get(Field.TRANSPODER),
			"-p",
			chan.get(Field.POLARITY),
			"-s",
			chan.get(Field.SYMBOL_RATE),
			"-I",
			"2",
			"-L1",
			lof1,
			"-L2",
			lof2,
			"-SL",
			switchvalue,
			chan.get(Field.VPID),
			chan.get(Field.APID)
		};
		
		log.info("Running Process: " + toString(cmd, " "));
		process = Runtime.getRuntime().exec(cmd);
		
		stderrThread = new StreamConsumerThread("dvbstream(stderr)", process.getErrorStream());
		stderrThread.addListener(new IStreamListener() {
            @Override
            public String getMatcher() {
                return null;
            }

            @Override
            public void onMatch(String data) {
                log.debug("dvbstream(stderr): " + data);
                if (state == State.Idle) {
                    if (data.contains("Not able to lock to the signal")) {
                        state = State.Failed;
                        log.warn("Failed to tune channel: " + chan);
                        return;
                    }
                    if (data.contains("Streaming")) {
                        state = State.Tuned;
                        log.info("dvbstream(stderr) has lock on channel :) " + chan);
                    }
                }
            }
		});
		stderrThread.start();
		
		// wait until we tune or timeout
		WaitFor waitFor = new WaitFor() {
            @Override
            public boolean isDoneWaiting() {
                return state!=State.Idle;
            }
		};
		waitFor.waitFor(10*1000, 100);
		
		if (state != State.Tuned) {
		    stop();
		    throw new Exception("Failed to tune channel: " + chan);
		}
	}

	public void stop() {
		if (process!=null) {
			process.destroy();
			process = null;
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
	    return "dvbstream tuner["+((config!=null)?config.getDvbStreamPath():"")+"]";
	}
}
