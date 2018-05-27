package api.websocket.chat.model.payload;

import api.websocket.common.model.Payload;

/**
 * Represents the payload of a WebSocket frame to broadcast details of a user who has connected to the chat.
 *
 * @author Tomas Perez Molina
 */
public class BroadcastConnectedUserPayload implements Payload {

    public static final String TYPE = "broadcastConnectedUser";

    private Integer id;

    public BroadcastConnectedUserPayload() {}

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
