package org.jdna.bmt.web.client.ui.xmleditor;

import java.util.Comparator;

public class NamedItemSorter implements Comparator<NamedItem> {
	public NamedItemSorter() {
	}

	@Override
	public int compare(NamedItem o1, NamedItem o2) {
		if (o1==null||o2==null||o1.getName()==null||o2.getName()==null) return 0;
		return o1.getName().compareTo(o2.getName());
	}
}
