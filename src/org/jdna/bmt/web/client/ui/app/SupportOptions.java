package org.jdna.bmt.web.client.ui.app;

import java.io.Serializable;

import org.jdna.bmt.web.client.util.Property;

public class SupportOptions implements Serializable {
    private static final long serialVersionUID = 1L;
    private Property<String> comment = new Property<String>();
    private Property<Boolean> includeLogs = new Property<Boolean>(true);
    private Property<Boolean> includeProperties = new Property<Boolean>(false);
    private Property<Boolean> includeSageImports = new Property<Boolean>(false);
    
    public SupportOptions() {
        super();
    }
    
    /**
     * @return the comment
     */
    public Property<String> getComment() {
        return comment;
    }
    /**
     * @return the includeLogs
     */
    public Property<Boolean> getIncludeLogs() {
        return includeLogs;
    }
    /**
     * @return the includeProperties
     */
    public Property<Boolean> getIncludeProperties() {
        return includeProperties;
    }
    /**
     * @return the includeSageImports
     */
    public Property<Boolean> getIncludeSageImports() {
        return includeSageImports;
    }
}
