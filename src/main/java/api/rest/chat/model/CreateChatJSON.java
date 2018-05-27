package api.rest.chat.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Tomas Perez Molina
 */
public class CreateChatJSON {
    private List<Integer> members;

    @JsonCreator
    public CreateChatJSON(@JsonProperty(value = "members", required = true) List<Integer> members) {
        this.members = members;
    }

    public List<Integer> getMembers() {
        return members;
    }

    public void setMembers(List<Integer> members) {
        this.members = members;
    }
}
