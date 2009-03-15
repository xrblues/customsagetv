package org.jdna.media.metadata;

public class MetadataUtil {
    /**
     * Given a metadata id, id:###, return 2 parts, the id, and the ####
     * 
     * if the id is not a valid id, then only a 1 element array will be returned.
     * 
     * @param id
     * @return
     */
    public static String[] getMetadataIdParts(String id) {
        if (id==null) return null;
        String parts[] = id.split(":");
        if (parts==null || parts.length!=2) {
            return new String[] {id};
        }
        return parts;
    }
}
