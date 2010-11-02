package org.jdna.bmt.web.client.ui.prefs;

import java.util.Comparator;

public class ChannelNameComparator implements Comparator<Channel> {
	public ChannelNameComparator() {
	}

	@Override
	public int compare(Channel c1, Channel c2) {
		try {
			return c1.getName().compareTo(c2.getName());
		} catch (Exception e) {
			return 0;
		}
	}

}
