package api.websocket.user.model.payload;

import api.websocket.common.model.Payload;

/**
 * Represents the payload of a WebSocket frame to broadcast details of a user who has connected to the chat.
 *
 * @author Tomas Perez Molina
 */
public class BroadcastDisconnectedUserPayload implements Payload {

    public static final String TYPE = "broadcastDisconnectedUser";

    private Integer id;

    public BroadcastDisconnectedUserPayload() {

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
}
