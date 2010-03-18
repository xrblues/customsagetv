package sagex.api.metadata;

public enum SageRolePropertyRW implements ISageProperty {
    Actor(),
    LeadActor("Lead Actor"),
    SupportingActor("Supporting Actor"),
    Actress(Type.StringList),
    LeadActress("Lead Actress"),
    SupportingActress("Supporting Actress"),
    Guest(Type.StringList), 
    GuestStar("Guest Star"),
    Director(),
    Producer(),
    Writer(),
    Choreographer(),
    SportsFigure("Sports Figure"),
    Coach(), 
    Host(), 
    ExecutiveProducer("Executive Producer"),
    Artist(),
    AlbumArtist("Album Artist"),
    Composer(),
    Judge(),
    Narrator(),
    Contestant(),
    Correspondent()
    ;
    
    
    private String value;
    private ISageProperty.Type type;
    
    SageRolePropertyRW() {
        this.value = name();
        this.type=Type.StringList;
    }

    SageRolePropertyRW(Type type) {
        this.value = name();
        this.type=type;
    }

    SageRolePropertyRW(String name) {
        this.value = name;
        this.type=Type.StringList;
    }

    SageRolePropertyRW(String value, Type type) {
        this.value = value;
        this.type = type;
    }

    public Type type() {
        return type;
    }

    public String key() {
        return value;
    }
}
