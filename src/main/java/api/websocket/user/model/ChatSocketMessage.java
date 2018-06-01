package api.websocket.user.model;

import api.websocket.common.model.Payload;
import api.websocket.user.model.payload.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Tomas Perez Molina
 */
public class ChatSocketMessage{

    private String type;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = BroadcastAvailableUsersPayload.class, name = BroadcastAvailableUsersPayload.TYPE),
            @JsonSubTypes.Type(value = BroadcastConnectedUserPayload.class, name = BroadcastConnectedUserPayload.TYPE),
            @JsonSubTypes.Type(value = BroadcastDisconnectedUserPayload.class, name = BroadcastDisconnectedUserPayload.TYPE),
            @JsonSubTypes.Type(value = BroadcastTextMessagePayload.class, name = BroadcastTextMessagePayload.TYPE),
            @JsonSubTypes.Type(value = SendTextMessagePayload.class, name = SendTextMessagePayload.TYPE)
    })
    private Payload payload;

    public ChatSocketMessage() {}

    public ChatSocketMessage(Payload payload) {
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
}
