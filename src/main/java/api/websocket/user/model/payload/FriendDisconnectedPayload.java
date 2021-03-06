package api.websocket.user.model.payload;

import api.websocket.common.model.Payload;

/**
 * Represents the payload of a WebSocket frame to broadcast details of a user who has connected to the chat.
 *
 * @author Tomas Perez Molina
 */
public class FriendDisconnectedPayload implements Payload {

    public static final String TYPE = "friendDisconnected";

    private Integer id;

    public FriendDisconnectedPayload() {}

    public FriendDisconnectedPayload(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "FriendDisconnectedPayload{" +
                "id=" + id +
                '}';
    }
}
