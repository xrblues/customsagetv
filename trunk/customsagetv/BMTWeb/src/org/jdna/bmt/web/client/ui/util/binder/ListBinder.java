package org.jdna.bmt.web.client.ui.util.binder;

import java.util.ArrayList;
import java.util.List;

import org.jdna.bmt.web.client.ui.input.NVP;
import org.jdna.bmt.web.client.util.Property;

import com.google.gwt.user.client.ui.ListBox;

public class ListBinder extends FieldBinder<String> {
	public ListBinder(Property<String> prop) {
		this(new ListBox(), prop, (String)null);
	}

	public ListBinder(Property<String> prop, List<NVP<String>> values) {
		this(new ListBox(), prop, values);
	}

	public ListBinder(ListBox w, Property<String> prop, List<NVP<String>> values) {
		super(w, prop);
		setFieldValues(values);
	}

	public ListBinder(Property<String> prop, String values) {
		this(new ListBox(), prop, values);
	}
	
	public ListBinder(ListBox w, Property<String> prop, String values) {
		super(w, prop);

		if (values==null) return;
		
		List<NVP<String>> vals = new ArrayList<NVP<String>>();
		String[] svals = values.split("\\s*,\\s*");
		for (String s : svals) {
			vals.add(new NVP<String>(s, s));
		}
		setFieldValues(vals);
	}
	
	public void setFieldValues(List<NVP<String>> values) {
		if (values!=null) {
			ListBox lb = (ListBox) getWidget();
			lb.clear();
			for (NVP<String> n : values) {
				lb.addItem(n.getName(), n.getValue());
			}
			lb.setSelectedIndex(-1);
		}
	}
	
	public void updateField() {
		ListBox lb = (ListBox) getWidget();
		for (int i=0;i<lb.getItemCount();i++) {
			if (lb.getValue(i).equals(getProp().get())) {
				lb.setSelectedIndex(i);
				break;
			}
		}
	}
	
	public void updateProperty() {
		ListBox lb = (ListBox) getWidget();
		int sel = lb.getSelectedIndex();
		if (sel>=0) {
			getProp().set(lb.getValue(sel));
		} else {
			getProp().set(null);
		}
	}

	@Override
	public String getText() {
		ListBox lb = (ListBox) getWidget();
		if (lb.getSelectedIndex()>=0) {
			return lb.getValue(lb.getSelectedIndex());
		} else {
			return null;
		}
	}

	@Override
	public void setText(String text) {
		ListBox lb = (ListBox) getWidget();
		for (int i=0;i<lb.getItemCount();i++) {
			if (lb.getValue(i).equals(text)) {
				lb.setSelectedIndex(i);
				break;
			}
		}
	}
}
