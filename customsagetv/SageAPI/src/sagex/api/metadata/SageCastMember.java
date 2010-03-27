package sagex.api.metadata;

import java.io.Serializable;

public class SageCastMember implements ISageCastMember, Serializable {
    private static final long serialVersionUID = 1L;
    private String name, role;
    
    public SageCastMember(String name, String role) {
        super();
        this.name = name;
        this.role = role;
    }

    public SageCastMember() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

}
