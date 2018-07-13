package api.websocket.user.model;

import api.websocket.common.model.Payload;
import api.websocket.user.model.payload.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Tomas Perez Molina
 */
public class UserSocketMessage {

    private String type;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = FriendConnectedPayload.class, name = FriendConnectedPayload.TYPE),
            @JsonSubTypes.Type(value = FriendDisconnectedPayload.class, name = FriendDisconnectedPayload.TYPE),
            @JsonSubTypes.Type(value = ReceivedFriendRequestPayload.class, name = ReceivedFriendRequestPayload.TYPE),
            @JsonSubTypes.Type(value = SentFriendRequestPayload.class, name = SentFriendRequestPayload.TYPE),
            @JsonSubTypes.Type(value = NewChatPayload.class, name = NewChatPayload.TYPE),
            @JsonSubTypes.Type(value = DeleteChatPayload.class, name = DeleteChatPayload.TYPE),
            @JsonSubTypes.Type(value = NewFriendPayload.class, name = NewFriendPayload.TYPE),
            @JsonSubTypes.Type(value = DeleteFriendPayload.class, name = DeleteFriendPayload.TYPE),
            @JsonSubTypes.Type(value = NewPostPayload.class, name = NewPostPayload.TYPE),
            @JsonSubTypes.Type(value = DeletePostPayload.class, name = DeletePostPayload.TYPE),
            @JsonSubTypes.Type(value = NewGroupPayload.class, name = NewGroupPayload.TYPE),
            @JsonSubTypes.Type(value = DeleteGroupPayload.class, name = DeleteGroupPayload.TYPE)
    })
    private Payload payload;

    public UserSocketMessage() {}

    public UserSocketMessage(Payload payload) {
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
        return "UserSocketMessage{" +
                "type='" + type + '\'' +
                ", payload=" + payload +
                '}';
    }
}
