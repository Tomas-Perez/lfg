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
    private Integer chatPlatform;
    private Integer gamePlatform;

    @JsonCreator
    public CreateGroupJSON(@JsonProperty(value = "slots", required = true) int slots,
                           @JsonProperty(value = "activityID", required = true) int activityID,
                           @JsonProperty(value = "ownerID") int ownerID,
                           @JsonProperty(value = "chatPlatform") Integer chatPlatform,
                           @JsonProperty(value = "gamePlatform") Integer gamePlatform)
    {
        this.slots = slots;
        this.activityID = activityID;
        this.ownerID = ownerID;
        this.chatPlatform = chatPlatform;
        this.gamePlatform = gamePlatform;
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

    public Integer getChatPlatform() {
        return chatPlatform;
    }

    public void setChatPlatform(Integer chatPlatform) {
        this.chatPlatform = chatPlatform;
    }

    public Integer getGamePlatform() {
        return gamePlatform;
    }

    public void setGamePlatform(Integer gamePlatform) {
        this.gamePlatform = gamePlatform;
    }
}
