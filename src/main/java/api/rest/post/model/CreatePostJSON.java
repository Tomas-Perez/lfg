package api.rest.post.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import api.rest.common.exception.BadRequestException;

import java.util.Set;

/**
 * @author Tomas Perez Molina
 */
public class CreatePostJSON {
    private String description;
    private Integer activityID;
    private Integer ownerID;
    private Integer groupID;
    private Set<Integer> gamePlatformIDs;
    private Set<Integer> chatPlatformIDs;

    @JsonCreator
    public CreatePostJSON(@JsonProperty(value = "description", required = true) String description,
                          @JsonProperty(value = "activityID") Integer activityID,
                          @JsonProperty(value = "ownerID") Integer ownerID,
                          @JsonProperty(value = "groupID") Integer groupID,
                          @JsonProperty(value = "gamePlatformIDs") Set<Integer> gamePlatformID,
                          @JsonProperty(value = "chatPlatformIDs") Set<Integer> chatPlatformID)
    {
        if(groupID == null && (activityID == null || ownerID == null)) {
            throw new BadRequestException("Invalid post creation syntax");
        }
        this.description = description;
        this.activityID = activityID;
        this.ownerID = ownerID;
        this.groupID = groupID;
        this.gamePlatformIDs = gamePlatformID;
        this.chatPlatformIDs = chatPlatformID;
    }

    public CreatePostJSON() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getActivityID() {
        return activityID;
    }

    public void setActivityID(Integer activityID) {
        this.activityID = activityID;
    }

    public Integer getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(Integer ownerID) {
        this.ownerID = ownerID;
    }

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public Set<Integer> getGamePlatformIDs() {
        return gamePlatformIDs;
    }

    public void setGamePlatformIDs(Set<Integer> gamePlatformIDs) {
        this.gamePlatformIDs = gamePlatformIDs;
    }

    public Set<Integer> getChatPlatformIDs() {
        return chatPlatformIDs;
    }

    public void setChatPlatformIDs(Set<Integer> chatPlatformIDs) {
        this.chatPlatformIDs = chatPlatformIDs;
    }
}
