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
    private List<Integer> members;

    @JsonCreator
    public CreateChatJSON(
            @JsonProperty(value = "type", required = true) ChatType type,
            @JsonProperty(value = "members", required = true) List<Integer> members) {
        this.type = type;
        if(type == ChatType.PRIVATE && members.size() > 2){
            throw new BadRequestException("Private chats are for only two members.");
        }
        this.members = members;
    }

    public List<Integer> getMembers() {
        return members;
    }

    public void setMembers(List<Integer> members) {
        this.members = members;
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
