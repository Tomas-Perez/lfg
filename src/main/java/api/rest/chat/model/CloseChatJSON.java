package api.rest.chat.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Tomas Perez Molina
 */
public class CloseChatJSON {
    private boolean close;
    private int memberID;

    @JsonCreator
    public CloseChatJSON(@JsonProperty(value = "close", required = true) boolean close,
                         @JsonProperty(value = "memberID", required = true) int memberID) {
        this.close = close;
        this.memberID = memberID;
    }

    public boolean isClose() {
        return close;
    }

    public int getMemberID() {
        return memberID;
    }
}
