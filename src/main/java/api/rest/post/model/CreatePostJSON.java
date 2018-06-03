package api.rest.post.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import api.rest.common.exception.BadRequestException;

/**
 * @author Tomas Perez Molina
 */
public class CreatePostJSON {
    private String description;
    private Integer activityID;
    private Integer ownerID;
    private Integer groupID;

    @JsonCreator
    public CreatePostJSON(@JsonProperty(value = "description", required = true) String description,
                          @JsonProperty(value = "activityID") Integer activityID,
                          @JsonProperty(value = "ownerID") Integer ownerID,
                          @JsonProperty(value = "groupID") Integer groupID)
    {
        if(groupID == null && (activityID == null || ownerID == null)) {
            throw new BadRequestException("Invalid post creation syntax");
        }
        this.description = description;
        this.activityID = activityID;
        this.ownerID = ownerID;
        this.groupID = groupID;
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
}
