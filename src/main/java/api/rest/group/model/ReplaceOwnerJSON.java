package api.rest.group.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Tomas Perez Molina
 */
public class ReplaceOwnerJSON {
    private int newOwnerID;

    @JsonCreator
    public ReplaceOwnerJSON(@JsonProperty(value = "newOwnerID", required = true) int id) {
        this.newOwnerID = id;
    }

    public int getNewOwnerID() {
        return newOwnerID;
    }

    public void setNewOwnerID(int newOwnerID) {
        this.newOwnerID = newOwnerID;
    }
}
