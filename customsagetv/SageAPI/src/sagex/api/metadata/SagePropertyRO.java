package sagex.api.metadata;

public enum SagePropertyRO implements ISageProperty {
    Width(Type.Int),
    Height(Type.Int),
    Track(Type.Int),
    TotalTracks(Type.Int),
    Comment(Type.String),
    AiringTime(Type.DateTime),
    ThumbnailOffset(Type.Int),
    ThumbnailSize(Type.Int),
    ThumbnailDesc(Type.Int),
    // The metadata duration may be different then the actual duration since there's different ways to detect duration
    Duration(Type.Long),
    ;


    private String value;
    private Type type;
    SagePropertyRO() {
        this.value = name();
        this.type=Type.StringList;
    }

    SagePropertyRO(Type type) {
        this.value = name();
        this.type=type;
    }

    SagePropertyRO(String name) {
        this.value = name;
        this.type=Type.StringList;
    }

    SagePropertyRO(String value, Type type) {
        this.value = value;
        this.type = type;
    }
    
    public String key() {
        return value;
    }
    
    public Type type() {
        return type;
    }
}
