package org.jdna.bmt.web.client.ui.status;

import java.io.Serializable;

public class StatusValue implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int NORMAL = 0;
    public static final int ERROR = 1;
    public static final int WARN = 2;
    
    public String label;
    public String value;
    private int level;
    private String reason;
    private boolean isSepartor;
    
    public StatusValue() {
    }
    
    public StatusValue(String label, String value) {
        this(label, value, NORMAL);
    }

    public StatusValue(String label, String value, int level) {
        this.label=label;
        this.value=value;
        this.level=level;
    }

    public StatusValue(String label, String value, int level, String reason) {
        this.label=label;
        this.value=value;
        this.level=level;
        this.reason=reason;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public int getLevel() {
        return level;
    }

    public String getReason() {
        return reason;
    }

    /**
     * @return the isSepartor
     */
    public boolean isSepartor() {
        return isSepartor;
    }

    /**
     * @param isSepartor the isSepartor to set
     */
    public void setSepartor(boolean isSepartor) {
        this.isSepartor = isSepartor;
    }
}
