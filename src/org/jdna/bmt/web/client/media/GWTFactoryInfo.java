package org.jdna.bmt.web.client.media;

import java.io.Serializable;

public class GWTFactoryInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum SourceType {Source, View, Sort, Filter, Group, ALL, SCANS}; 
    private String id, label, description;
    private SourceType sourceType = SourceType.Source;
    
    public GWTFactoryInfo(SourceType type, String id, String label, String description) {
        super();
        this.sourceType=type;
        this.id = id;
        this.label = label;
        this.description = description;
    }

    public GWTFactoryInfo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }
    
    public String toString() {
        return "Factory[type: " + sourceType + ", id: "+ getId() +"]";
    }
}
