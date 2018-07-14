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
    private Integer chatPlatformID;
    private Integer gamePlatformID;

    @JsonCreator
    public CreateGroupJSON(@JsonProperty(value = "slots", required = true) int slots,
                           @JsonProperty(value = "activityID", required = true) int activityID,
                           @JsonProperty(value = "ownerID") int ownerID,
                           @JsonProperty(value = "chatPlatformID") Integer chatPlatformID,
                           @JsonProperty(value = "gamePlatformID") Integer gamePlatformID)
    {
        this.slots = slots;
        this.activityID = activityID;
        this.ownerID = ownerID;
        this.chatPlatformID = chatPlatformID;
        this.gamePlatformID = gamePlatformID;
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

    public Integer getChatPlatformID() {
        return chatPlatformID;
    }

    public void setChatPlatformID(Integer chatPlatformID) {
        this.chatPlatformID = chatPlatformID;
    }

    public Integer getGamePlatformID() {
        return gamePlatformID;
    }

    public void setGamePlatformID(Integer gamePlatformID) {
        this.gamePlatformID = gamePlatformID;
    }
}
