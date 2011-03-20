package org.jdna.sagetv.networkencoder.impl.dvb.linux;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.jdna.sagetv.networkencoder.impl.dvb.linux.IDVBChannel.Field;

/**
 * IDVBChannelProvide implementation that gets it's channels from a
 * channels.conf file. channels.conf is what is used by dvbutils such as, szap.
 * 
 * @author seans
 * 
 */
public class ChannelsConfChannelProvider implements IDVBChannelProvider {
	private static final Logger logger = Logger.getLogger(ChannelsConfChannelProvider.class);
	
	private List<IDVBChannel> channelInfo = new ArrayList<IDVBChannel>();
	private Comparator<IDVBChannel> sorter = new Comparator<IDVBChannel>() {
        @Override
        public int compare(IDVBChannel arg0, IDVBChannel arg1) {
            if (arg0==null || arg1==null) return -1;
            String sid0 = arg0.get(Field.SID);
            String sid1 = arg1.get(Field.SID);
            if (sid0==null) return -1;
            if (sid1==null) return 1;
            if (sid0.equals(sid1)) return 0;
            
            Integer s0 = NumberUtils.toInt(sid0, -1);
            Integer s1 = NumberUtils.toInt(sid1, -1);
            return s0.compareTo(s1);
        }
    };

	public ChannelsConfChannelProvider() {
	}

	public void load(File chans) throws Exception {
		logger.info("Loading Channel File: " +chans.getAbsolutePath());
		channelInfo.clear();
		if (!chans.exists()) {
			throw new FileNotFoundException(chans.getAbsolutePath());
		}

		BufferedReader r = new BufferedReader(new FileReader(chans));
		String line = null;
		while ((line = r.readLine()) != null) {
			if (line.startsWith("#"))
				continue;
			if (line.trim().length() == 0)
				continue;
			try {
				ChannelsConfDVBChannel ch = new ChannelsConfDVBChannel(line);
				addChannel(ch);
			} catch (Exception e) {
				logger.debug("Ignoring Channel: " + line);
			}
		}
		logger.debug("Loaded " + channelInfo.size() + " channels.");
	}

	public void save(File file) throws IOException {
	    Collections.sort(channelInfo, sorter);
	    PrintStream ps = null;
	    try {
	        ps = new PrintStream(file);
	        for (IDVBChannel c : channelInfo) {
	            String transponder = c.get(Field.TRANSPODER);
	            String sr = c.get(Field.SYMBOL_RATE);
	            String pol = getPolarity(c.get(Field.POLARITY));
	            String apid = c.get(Field.APID);
	            String vpid = c.get(Field.VPID);
	            String sid = c.get(Field.SID);
	            String diseqc = "1";
	            
	            if (NumberUtils.toInt(sid, 0)<198) {
	                // skip low channels on bell
	                continue;
	            }
	            
	            if (StringUtils.isEmpty(sid) || StringUtils.isEmpty(transponder) || StringUtils.isEmpty(sr) || StringUtils.isEmpty(sr) || StringUtils.isEmpty(apid) || StringUtils.isEmpty(vpid)) {
	                logger.warn("Skipping Channel: " + sid + "; " + transponder + "; " + apid + "; " + vpid);
	                continue;
	            }
	         
	            ps.printf("CH%s:%s:%s:%s:%s:%s:%s:%s\n", sid, transponder, pol, diseqc, sr, vpid, apid, sid);
	            
	        }
	    } finally {
	        if (ps!=null) {
	            ps.flush();
	            ps.close();
	        }
	    }
	    
	    
	}

	private String getPolarity(String pol) {
	    if ("R".equalsIgnoreCase(pol) || "V".equalsIgnoreCase(pol)) {
	        return "v";
	    } else {
	        return "h";
	    }
    }

    public IDVBChannel getChannelBySID(String sid) throws Exception {
		IDVBChannel retchan = null;

		for (Iterator<IDVBChannel> i = channelInfo.iterator(); i.hasNext();) {
			IDVBChannel chan = i.next();
			if (sid.equals(chan.get(Field.SID))) {
				retchan = chan;
			}
		}
		if (retchan == null) {
			throw new Exception("Invalid Channel SID: " + sid);
		}

		return retchan;
	}

	public IDVBChannel getChannelByName(String name) throws Exception {
		IDVBChannel retchan = null;

		for (Iterator<IDVBChannel> i = channelInfo.iterator(); i.hasNext();) {
			IDVBChannel chan = i.next();
			if (name.equals(chan.get(Field.NAME))) {
				retchan = chan;
			}
		}
		if (retchan == null) {
			throw new Exception("Invalid Channel Name: " + name);
		}

		return retchan;
	}

	public int indexOf(IDVBChannel chan) throws Exception {
		return channelInfo.indexOf(chan);
	}

	public void addChannel(IDVBChannel chan) {
		if (!channelInfo.contains(chan)) {
			logger.debug("Adding Channel: " + chan);
			channelInfo.add(chan);
		} else {
			logger.debug("Ignoring Channel: " + chan);
		}
	}

	@Override
	public List<IDVBChannel> getChannels() {
		return channelInfo;
	}

    public void addChannels(List<IDVBChannel> channels) {
        channelInfo.addAll(channels);
    }
}
