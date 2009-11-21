package org.jdna.media.metadata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SearchQuery implements Serializable {
    public enum Field { TITLE, SEASON, EPISODE, DISC, EPISODE_TITLE, EPISODE_DATE, YEAR};
    public enum Type { TV, MOVIE, MUSIC };
    
    private Map<Field, String> fields = new HashMap<Field, String>();
    private Type type = Type.MOVIE;
    
    public SearchQuery() {
    }
    
    public SearchQuery(SearchQuery query) {
        this.type=query.getType();
        for (Field f : query.fields.keySet()) {
            fields.put(f, query.get(f));
        }
    }

    public SearchQuery(String title) {
        this (Type.MOVIE, title);
    }

    public SearchQuery(Type type, String title) {
        this(type, Field.TITLE, title);
    }
    
    public SearchQuery(Type type, Field field, String value) {
        this.type=type;
        set(field, value);
    }
    
    public Type getType() {
        return type;
    }
    
    public SearchQuery setType(Type type) {
        this.type=type;
        return this;
    }
    
    public SearchQuery set(Field field, String value) {
        fields.put(field, value);
        return this;
    }
    
    public String get(Field field) {
        return fields.get(field);
    }
    
    @Override
    public String toString() {
       StringBuffer sb =  new StringBuffer("SearchQuery; Type: ").append(type.name()).append("; ");;
       for (Field k : fields.keySet()) {
           sb.append(k.name()).append(":").append(fields.get(k)).append(";");
       }
       return sb.toString();
    }
    
    public static SearchQuery copy(SearchQuery q) {
        return new SearchQuery(q);
    }
}
