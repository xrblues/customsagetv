package org.jdna.bmt.web.client.ui.prefs;

import java.util.Comparator;

public class ChannelEnabledComparator implements Comparator<Channel> {
	public ChannelEnabledComparator() {
	}

	@Override
	public int compare(Channel c1, Channel c2) {
		try {
			int i1 = (c1.enabled().get())?1:0;
			int i2 = (c2.enabled().get())?1:0;
			return i2 - i1;
		} catch (Exception e) {
			return 0;
		}
	}

}
