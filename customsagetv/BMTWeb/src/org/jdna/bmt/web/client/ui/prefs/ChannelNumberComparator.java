package org.jdna.bmt.web.client.ui.prefs;

import java.util.Comparator;

public class ChannelNumberComparator implements Comparator<Channel> {
	public ChannelNumberComparator() {
	}

	@Override
	public int compare(Channel c1, Channel c2) {
		try {
			int i1 = Integer.parseInt(c1.getNumber());
			int i2 = Integer.parseInt(c2.getNumber());
			return i1-i2; 
		} catch (Exception e) {
			return 0;
		}
	}

}
