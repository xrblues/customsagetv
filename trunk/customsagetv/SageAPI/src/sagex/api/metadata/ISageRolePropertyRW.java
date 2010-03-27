package sagex.api.metadata;

import java.util.List;

public interface ISageRolePropertyRW extends ISageMetadata {
    @SageProperty("Actor")
    public List<ISageCastMember> getActors();
    
    @SageProperty("Lead Actor")
    public List<ISageCastMember> getLeadActors();
    
    @SageProperty("Supporting Actor")
    public List<ISageCastMember> getSupportingActors();

    @SageProperty("Actress")
    public List<ISageCastMember> getActresses();

    @SageProperty("Lead Actress")
    public List<ISageCastMember> getLeadActresses();
    
    @SageProperty("Supporting Actress")
    public List<ISageCastMember> getSupportingActresses();
    
    @SageProperty("Guest")
    public List<ISageCastMember> getGuests();
    
    @SageProperty("Guest Star")
    public List<ISageCastMember> getGuestStars();
    
    @SageProperty("Director")
    public List<ISageCastMember> getDirectors();
    
    @SageProperty("Producer")
    public List<ISageCastMember> getProducers();

    @SageProperty("Writer")
    public List<ISageCastMember> getWriters();

    @SageProperty("Choreographer")
    public List<ISageCastMember> getChoreographers();

    @SageProperty("Sports Figure")
    public List<ISageCastMember> getSportsFigures();

    @SageProperty("Coach")
    public List<ISageCastMember> getCoaches();
    
    @SageProperty("Host")
    public List<ISageCastMember> getHosts();
    
    @SageProperty("Executive Producer")
    public List<ISageCastMember> getExecutiveProducers();

    @SageProperty("Artist")
    public List<ISageCastMember> getArtists();

    @SageProperty("Album Artist")
    public List<ISageCastMember> getAlbumArtists();

    @SageProperty("Composer")
    public List<ISageCastMember> getComposers();

    @SageProperty("Judge")
    public List<ISageCastMember> getJudges();

    @SageProperty("Narrator")
    public List<ISageCastMember> getNarrators();

    @SageProperty("Contestant")
    public List<ISageCastMember> getContestants();

    @SageProperty("Correspondent")
    public List<ISageCastMember> getCorrespondents();
}
