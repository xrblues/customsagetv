package org.jdna.sagetv.networkencoder.impl.dvb.linux;

import java.util.List;


/**
 * A Provider for IDVBChannel objects.  A provider is a class that knows how to get a list
 * of channels.
 * @author seans
 *
 */
public interface IDVBChannelProvider {
	/**
	 * Find a channel based on it's program number
	 * @param pNum Program Number
	 * @return IDVBChannel
	 * @throws Exception on error
	 */
	public IDVBChannel getChannelBySID(String pNum) throws Exception;
	
	/**
	 * Find a Channel By name.
	 * @param name Channel Name, ie, "TSN"
	 * @return IDVBChannel
	 * @throws Exception on error
	 */
	public IDVBChannel getChannelByName(String name) throws Exception;
	
	/**
	 * returns the channels for this provider
	 * @return
	 */
	public List<IDVBChannel> getChannels();
}
