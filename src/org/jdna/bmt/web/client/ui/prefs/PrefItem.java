package org.jdna.bmt.web.client.ui.prefs;

import java.io.Serializable;

import org.jdna.bmt.web.client.util.Property;

public class PrefItem extends Property<String> implements Serializable {
    private String label = null;
    private String description = null;
    private String key = null;
    private boolean isGroup = false;
    private String defaultValue = null;
    private String resetValue = null;
    private boolean isArray = false;
    private String editor = null;
    
    private String type = null;
    
    private PrefItem[] children = null;

    public PrefItem() {
        super(null);
    }

    public PrefItem[] getChildren() {
        return children;
    }

    public void setChildren(PrefItem[] children) {
        this.children = children;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String text) {
        this.label = text;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return get();
    }

    public void setValue(String value) {
        set(value);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getResetValue() {
        return resetValue;
    }

    public void setResetValue(String resetValue) {
        this.resetValue = resetValue;
    }
    
    public boolean hasChanged() {
        return (get()!=null && !get().equals(resetValue));
    }

    public boolean isArray() {
        return isArray;
    }

    public void setArray(boolean isArray) {
        this.isArray = isArray;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }
}
