package org.jdna.sagetv.networkencoder.impl.dvb.linux.cmdline;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jdna.sagetv.networkencoder.util.IFileEncoder;
import org.jdna.sagetv.networkencoder.util.IStreamListener;
import org.jdna.sagetv.networkencoder.util.StreamConsumerThread;
import org.jdna.sagetv.networkencoder.util.WaitFor;

import sagex.phoenix.configuration.proxy.GroupProxy;

/**
 * Java Wrapper for mencoder.  Used to take the DVB TS and and covert to mpeg2 file for Sage.
 * 
 * Current Parameters are: 
			"/usr/bin/mencoder",
			"-o",
			tuningFile.getAbsolutePath(),
			"-ovc",
			"copy",
			"-oac",
			"copy",
			"-of",
			"mpeg",
			"-mpegopts",
			"mpeg2",
			dvr.getAbsolutePath()
 * @author seans
 *
 */
public class Mencoder implements IFileEncoder {
    private Logger log = Logger.getLogger(Mencoder.class);
	private Process process=null;
	private StreamConsumerThread stdout = null;
	private StreamConsumerThread stderr = null;
	private boolean tuned=false;
    private SZAPConfiguration config = null;

	
	public Mencoder() {
	    config = GroupProxy.get(SZAPConfiguration.class);
	}

	/**
	 * Start the encode process and don't return until mencoder has started to process
	 * the input stream.
	 * @param dvr
	 * @param tuningFile
	 * @throws IOException
	 */
	public void encode(File dvr, final File tuningFile) throws IOException {
		tuned=false;
		
		if (process!=null) {
			stop();
		}
		
		// /usr/bin/mencoder -o "$1" -ovc copy -oac copy -of mpeg /var/media/tv/TSN2.mpg
		String cmd[] = new String[] {
			config.getMencoderPath(),
			"-o",
			tuningFile.getAbsolutePath(),
			"-ovc",
			"copy",
			"-oac",
			"copy",
			"-of",
			"mpeg",
			dvr.getAbsolutePath()
		};
		
		log.info("Running Process: " + toString(cmd, " "));
		process = Runtime.getRuntime().exec(cmd);

		stdout = new StreamConsumerThread("mencoder(stdout)", process.getInputStream());
		stdout.addListener(new IStreamListener() {
            @Override
            public String getMatcher() {
                return "VIDEO";
            }

            @Override
            public void onMatch(String data) {
                if (!tuned) {
                    tuned=true;
                    log.info("Mencoder has finally found the stream...");
                } else {
                    log.debug("Mencoder Output: " + data);
                }
            }
		});
		stdout.start();
		
		stderr = new StreamConsumerThread("mencoder(stderr)", process.getErrorStream());
		stderr.addListener(new IStreamListener() {
            @Override
            public String getMatcher() {
                return null;
            }

            @Override
            public void onMatch(String data) {
                log.warn("mencoder(stderr): " + data);
            }
		});
		stderr.start();

		WaitFor wait = new WaitFor() {
            @Override
            public boolean isDoneWaiting() {
                return tuned;
            }
		};
		wait.waitFor(10*1000, 100);
		
		if (!tuned) {
		    if (!tuningFile.exists()) {
		        tuningFile.createNewFile();
		    }
		    // MAYBE create the file with dummy data, so that sage will respond.
		    stop();
		    throw new IOException("Failed to get video stream for: " + tuningFile.getAbsolutePath());
		}
		
		log.info("Mencoder has the video stream for: " + tuningFile.getAbsolutePath());
	}

	public void stop() {
		if (process!=null) {
			process.destroy();
		}
        process=null;

        if (stderr!=null) {
		    stderr.interrupt();
		}
		if (stdout!=null) {
		    stdout.interrupt();
		}
	}

	public String toString(Object[] arr, String sep) {
		StringBuffer sb = new StringBuffer();
	   for (int i=0;i<arr.length;i++) {
		   sb.append(arr[i]).append(sep);
	   }
	   return sb.toString();
	}

    @Override
    public boolean isRunning() {
        return process!=null;
    }

}
