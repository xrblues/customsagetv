package sagex.api.metadata;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import sagex.api.enums.MediaFileAPI;

/**
 * SageTV Metadata Class.  This class should always contain the complete list of metadata fields that work with
 * {@link MediaFileAPI}.GetMediaFileMetadata()
 * 
 * Other classes can call addProperties() to register thier own custom metadata fields
 * 
 * @author seans
 *
 */
public class SageMetadata {
    private static Set<ISageProperty> properties = new TreeSet<ISageProperty>(new Comparator<ISageProperty>() {
        public int compare(ISageProperty p1, ISageProperty p2) {
            return p1.key().compareTo(p2.key());
        }
    });
    
    static {
        addProperties(SagePropertyRO.values());
        addProperties(SagePropertyRW.values());
        addProperties(SageRolePropertyRW.values());
        addProperties(SageFormatPropertyRO.values());
    }
    
    public static Set<ISageProperty> properties() {
        return properties;
    }

    public static void addProperties(ISageProperty[] props) {
        for (ISageProperty p: props) {
            addProperty(p);
        }
    }
    
    public static boolean addProperty(ISageProperty p) {
        return properties.add(p);
    }

    public static ISageProperty valueOf(String propName) {
        if (propName==null) return null;
        for (ISageProperty p : properties) {
            if (propName.equals(p.key())) {
                return p;
            }
        }
        return null;
    }
}
