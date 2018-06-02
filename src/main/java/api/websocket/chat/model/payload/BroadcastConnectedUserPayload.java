package api.websocket.chat.model.payload;

import api.rest.user.model.BasicUserData;
import api.websocket.common.model.Payload;

/**
 * Represents the payload of a WebSocket frame to broadcast details of a user who has connected to the chat.
 *
 * @author Tomas Perez Molina
 */
public class BroadcastConnectedUserPayload implements Payload {

    public static final String TYPE = "broadcastConnectedUser";

    private Integer id;
    private String username;

    public BroadcastConnectedUserPayload() {}

    public BroadcastConnectedUserPayload(BasicUserData data) {
        this.id = data.getId();
        this.username = data.getUsername();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
        return "BroadcastConnectedUserPayload{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
