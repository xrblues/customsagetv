package org.jdna.media.metadata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// TODO: Make this contain a map of ids
public class MetadataID implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static String[] getNVP(String id) {
        if (id==null) return null;
        String parts[] = id.split(":");
        if (parts==null || parts.length!=2) {
            return new String[] {id};
        }
        return parts;
    }

    private static String[] getNVPParts(String id) {
        if (id==null) return null;
        String parts[] = id.split(";");
        if (parts==null || parts.length==1) {
            return new String[] {id};
        }
        return parts;
    }

    public MetadataID() {
    }
    
    private String key, id;
    private Map<String, String> args = new HashMap<String, String>();
    
    public MetadataID(String key, String id) {
        this.key=key;
        this.id=id;
    }
    
    public Map<String, String> getArgs() {
        return args;
    }
    
    public MetadataID(String id) {
        String nvps[] = getNVPParts(id);
        
        if (nvps==null) {
            // error
            return;
        }
        
        String keyParts[] = getNVP(nvps[0]);
        this.key = keyParts[0];
        this.id=keyParts[1];
        
        if (nvps.length>1) {
            for (int i=1;i<nvps.length;i++) {
                String nvp[] = getNVP(nvps[i]);
                args.put(nvp[0].trim(), nvp[1].trim());
            }
        }
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void addArg(String key, String value) {
        getArgs().put(key, value);
    }
    
    public String getArg(String key) {
        return getArgs().get(key);
    }
    
    public String toIDString() {
        StringBuilder sb = new StringBuilder(key + ":" + id);
        if (getArgs().size()>0) {
            for (Map.Entry<String, String> me : getArgs().entrySet()) {
                sb.append(";").append(me.getKey()).append(":").append(me.getValue());
            }
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return toIDString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((args == null) ? 0 : args.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MetadataID other = (MetadataID) obj;
        if (args == null) {
            if (other.args != null) return false;
        } else if (!args.equals(other.args)) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (key == null) {
            if (other.key != null) return false;
        } else if (!key.equals(other.key)) return false;
        return true;
    }
}
