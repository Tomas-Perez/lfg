package api.websocket.group.model;

import api.websocket.common.model.Payload;
import api.websocket.group.model.payload.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Tomas Perez Molina
 */
public class GroupSocketMessage {

    private String type;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = DeleteMemberPayload.class, name = DeleteMemberPayload.TYPE),
            @JsonSubTypes.Type(value = NewOwnerPayload.class, name = NewOwnerPayload.TYPE),
            @JsonSubTypes.Type(value = NewMemberPayload.class, name = NewMemberPayload.TYPE)
    })
    private Payload payload;

    public GroupSocketMessage() {}

    public GroupSocketMessage(Payload payload) {
        this.type = payload.getType();
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "GroupSocketMessage{" +
                "type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }
}
