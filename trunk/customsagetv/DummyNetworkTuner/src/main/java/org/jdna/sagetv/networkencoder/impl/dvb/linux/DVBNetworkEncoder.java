package org.jdna.sagetv.networkencoder.impl.dvb.linux;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;
import org.jdna.sagetv.networkencoder.CommandException;
import org.jdna.sagetv.networkencoder.GetFileSizeCommand;
import org.jdna.sagetv.networkencoder.INetworkEncoder;
import org.jdna.sagetv.networkencoder.StartCommand;
import org.jdna.sagetv.networkencoder.StopCommand;
import org.jdna.sagetv.networkencoder.impl.dvb.linux.cmdline.DVBStreamTuner;
import org.jdna.sagetv.networkencoder.impl.dvb.linux.cmdline.SZAPConfiguration;
import org.jdna.sagetv.networkencoder.util.WaitFor;

import sagex.phoenix.configuration.proxy.GroupProxy;

/**
 * INetworkEncoder implementation that uses szap to tune a channel and uses
 * mencoder to transcode the TS into a mpeg2 stream suitable for Sage.
 * 
 * This implementation is considered sluggish and it usually takes about 10-15
 * seconds before sage will start to show video that has been tuned by this
 * implementation.
 */
public class DVBNetworkEncoder implements INetworkEncoder {
    private static final Logger logger     = Logger.getLogger(DVBNetworkEncoder.class);

    private File                tuningFile = null;
    private FileChannel         tuningChannel = null;

    private ChannelsConfChannelProvider channels   = null;
    private IDVBTuner           tuner      = null;

    private boolean             tuned      = false;
    private long lastFileSize=0;

    public DVBNetworkEncoder() throws Exception {
        SZAPConfiguration cfg = GroupProxy.get(SZAPConfiguration.class);
        channels = new ChannelsConfChannelProvider();
        channels.load(new File(cfg.getChannelsConf()));
        tuner = new DVBStreamTuner();
        logger.info("Created Tuner instance: " + tuner);
    }

    @Override
    public void start(StartCommand command) throws CommandException {
        // TODO: check if we are tuning the last channel, if so, then just do
        // the encoder
        logger.info("Starting the Tuner.");
        if (tuned) {
            stop(null);
            tuned=false;
        }

        tuningFile = new File(command.getFilename());

        logger.debug("Attempting to tune to file: " + tuningFile.getAbsolutePath());
        IDVBChannel chan = null;
        try {
            chan = channels.getChannelBySID(command.getChannel());
        } catch (Exception e1) {
            throw new CommandException(e1);
        }
        logger.debug("Mapped Channel # [" + command.getChannel() + "] to Channel: [" + chan + "]");

        // tune and notify us when it's done, so that we can use menucoder to
        // transcode the dvb stream to what sage wants....
        try {
            tuner.tune(chan, tuningFile);
        } catch (Exception e1) {
            logger.warn("Tuner Failed", e1);
            stop(null);
            throw new CommandException("Failed to tune: " + chan);
        }

        tuned=true;
        logger.debug("Channel has been tuned and encoder is running...");
    }

    @Override
    public long getFileSize(GetFileSizeCommand command) throws CommandException {
        logger.debug("GetFileSize Command: " + command);
        if (!tuned) {
            throw new CommandException("ABORT: Not Tuned!");
        }
        
        if (tuningChannel==null) {
            try {
                tuningChannel = new FileInputStream(tuningFile).getChannel();
            } catch (Exception e) {
                logger.warn("Failed to create FileChannel for recording file: " + tuningFile);
                return 0;
            }
        }

        // if the file size is the same as last time, then hold the request for up to 10 seconds...
        // hopefully this prevents sagetv from timeout out when the buffer keeps responding with the same values
        // over and over
        // Use Channels because this is more efficient, supoosedly.
        try {
            if (lastFileSize==tuningChannel.size()) {
                WaitFor wait = new WaitFor() {
                    @Override
                    public boolean isDoneWaiting() {
                        try {
                            return (tuningChannel.size()>lastFileSize);
                        } catch (IOException e) {
                            return false;
                        }
                    }
                };
                wait.waitFor(10*1000, 100);
            }
    
            return (lastFileSize=tuningChannel.size());
        } catch (Exception e) {
            logger.error("Unable to get the filesize!", e);
            return lastFileSize;
        }
    }

    @Override
    public void stop(StopCommand command) throws CommandException {
        tuned = false;
        logger.debug("Stopping the tuner.");
        tuner.stop();
        tuningFile = null;
        if (tuningChannel!=null) {
            try {
                tuningChannel.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        tuningChannel=null;
    }

    @Override
    public boolean isActive() {
        return tuned;
    }
}
