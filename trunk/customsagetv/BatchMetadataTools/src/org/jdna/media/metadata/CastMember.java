package org.jdna.media.metadata;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class CastMember implements ICastMember, Serializable {
    private static final long serialVersionUID = 1L;
    private String            id;
    private String            name;
    private String            part;
    private String            providerDataUrl;
    private int               type;
    private List<String> fanart = new LinkedList<String>();

    public CastMember() {
    }

    public CastMember(int type) {
        setType(type);
    }

    public CastMember(ICastMember cm) {
        this.setId(cm.getId());
        this.setName(cm.getName());
        this.setPart(cm.getPart());
        this.setProviderDataUrl(cm.getProviderDataUrl());
        this.setType(cm.getType());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getProviderDataUrl() {
        return providerDataUrl;
    }

    public void setProviderDataUrl(String providerDataUrl) {
        this.providerDataUrl = providerDataUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    public void addFanart(String url) {
        if (url!=null) {
            fanart.add(url.trim());
        }
    }
}
