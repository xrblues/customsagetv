package org.jdna.sagetv.networkencoder.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

import org.apache.log4j.Logger;

public class CopyEncoder implements IFileEncoder {
	public static final Logger logger = Logger.getLogger(CopyEncoder.class);
	private boolean cancelled = false;
	private byte[] buf;
	private Thread copyThread = null;
	private long bytesCopied = 0;
	
	public CopyEncoder() {
		buf = new byte[1024*16];
	}

	public void encode(final File src, final File dest) throws Exception {
		if (isRunning()) {
			throw new Exception("Encoder is still running, will not create another one.");
		}
		
		logger.debug(MessageFormat.format("Encoding  Src: {0}; Dest: {1}", src.getAbsolutePath(), dest.getAbsolutePath()));

		copyThread = new Thread() {

			@Override
			public void run() {
				try {
                    encodeFinal(new FileInputStream(src), new FileOutputStream(dest));
                } catch (FileNotFoundException e) {
                    logger.warn("File Error", e);
                }
			}
			
		};
		
		copyThread.start();
		
		logger.debug("Copy Thread Running...");
	}

	public void encode(final InputStream src, final OutputStream dest) throws Exception {
        copyThread = new Thread() {

            @Override
            public void run() {
                encodeFinal(src, dest);
            }
            
        };
        copyThread.start();
        
        logger.debug("Copy Thread Running...");
    }
	
	public void encodeFinal(final InputStream src, final OutputStream dest) {
		cancelled = false;
		BufferedInputStream bis=null;
		BufferedOutputStream bos=null;
		try {
			bis = new BufferedInputStream((src));
			bos = new BufferedOutputStream((dest));

			logger.debug("Starting to read input");
			int i=0;
			while ((i = bis.read(buf))>0) {
			    if (bytesCopied==0) {
			        logger.info("Copy Has Started...");
			    }
				bos.write(buf, 0, i);
				bos.flush();
				bytesCopied+=i;
				if (cancelled) {
					logger.info("Was Canelled.... Exiting...");
					break;
				}
			}
		} catch (Exception e) {
			logger.error("Error while copying!", e);
		} finally {
			if (bis!=null) {
				try {
					bis.close();
				} catch (IOException e) {
					logger.error("Error closing input stream!",e);
				}
			}
			if (bos!=null) {
				try {
				    bos.flush();
					bos.close();
				} catch (IOException e) {
					logger.error("Error closing output stream.");
				}
			}
		}
		logger.debug("Copy Thread Exiting...");
	}
	
	public long getBytesCopied() {
	    return bytesCopied;
	}

	public void stop() {
		cancelled = true;
		copyThread.interrupt();
	}
	
	public boolean isRunning() {
		return copyThread!=null && copyThread.isAlive();
	}
}
