package api.rest.group.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Tomas Perez Molina
 */
public class CreateGroupJSON {
    private int slots;
    private int activityID;
    private int ownerID;

    @JsonCreator
    public CreateGroupJSON(@JsonProperty(value = "slots", required = true) int slots,
                           @JsonProperty(value = "activityID", required = true) int activityID,
                           @JsonProperty(value = "ownerID", required = true) int ownerID) {
        this.slots = slots;
        this.activityID = activityID;
        this.ownerID = ownerID;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public int getActivityID() {
        return activityID;
    }

    public void setActivityID(int activityID) {
        this.activityID = activityID;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }
}
