package api.rest.chat.model;

import api.rest.common.exception.BadRequestException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Tomas Perez Molina
 */
public class CreateChatJSON {
    private ChatType type;
    private Integer recipient;
    private Integer groupID;

    @JsonCreator
    public CreateChatJSON(
            @JsonProperty(value = "type", required = true) ChatType type,
            @JsonProperty(value = "recipient") Integer recipient,
            @JsonProperty(value = "groupID") Integer groupID) {
        this.type = type;
        if (type == ChatType.PRIVATE){
            if(recipient == null)
                throw new BadRequestException("Need a recipient to create a private chat");
        } else if (groupID == null) {
            throw new BadRequestException("Need a group ID to make a group chat.");
        }
        this.groupID = groupID;
        this.recipient = recipient;
    }

    public Integer getRecipient() {
        return recipient;
    }

    public void setRecipient(Integer recipient) {
        this.recipient = recipient;
    }

    public Integer getGroupID() {
        return groupID;
    }

    public void setGroupID(Integer groupID) {
        this.groupID = groupID;
    }

    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public enum ChatType {
        @JsonProperty("group") GROUP,
        @JsonProperty("private") PRIVATE
    }
}
