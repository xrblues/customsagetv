package org.jdna.media.metadata;

public enum SearchResultType {
    EXACT("Exact Match", "*", 0),
    PARTIAL("Partial Match", "/", 1),
    POPULAR("Popular Match", "+", 2),
    UNKNOWN("Unknown", "?", 3);
    
    private final String desc, symbol;
    private final int id;
    
    SearchResultType(String desc, String symbol, int val) {
        this.desc=desc;
        this.symbol=symbol;
        this.id=val;
    }
    
    public String label() {return desc;};
    public String symbol() {return symbol;};
    public int id() {return id;};
    
    public static SearchResultType byId(int id) {
        for (SearchResultType t : values()) {
            if (id==t.id()) return t;
        }
        return UNKNOWN;
    }
}
