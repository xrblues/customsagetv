package org.jdna.bmt.web.client.ui.util.binder;

import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.user.client.ui.Widget;

public class FieldManager {
	public Map<String, FieldBinder<?>> fields = new TreeMap<String, FieldBinder<?>>();
	
	public FieldBinder<?> addField(String id, FieldBinder<?> field) {
		fields.put(id, field);
		return field;
	}
	
	public void updateFields() {
		for (FieldBinder<?> f: fields.values()) {
			f.updateField();
		}
	}

	public void updateProperties() {
		for (Map.Entry<String, FieldBinder<?>> me: fields.entrySet()) {
			FieldBinder<?> f = me.getValue();
			if (f.isEnabled()) {
				f.updateProperty();
			}
		}
	}
	
	public FieldBinder<?> getField(String id) {
		return fields.get(id);
	}
	
	public Widget getWidget(String id) {
		return fields.get(id).getWidget();
	}
}
