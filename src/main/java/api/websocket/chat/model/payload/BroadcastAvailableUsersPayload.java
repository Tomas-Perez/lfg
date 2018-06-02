package api.websocket.chat.model.payload;

import api.rest.user.model.BasicUserData;
import api.websocket.common.model.Payload;

import java.util.Set;

/**
 * Represents the payload of a WebSocket frame to broadcast the available users.
 *
 * @author Tomas Perez Molina
 */
public class BroadcastAvailableUsersPayload implements Payload {

    public static final String TYPE = "broadcastAvailableUsers";

    private Set<BasicUserData> users;

    public BroadcastAvailableUsersPayload() {}

    public BroadcastAvailableUsersPayload(Set<BasicUserData> users) {
        this.users = users;
    }

    public Set<BasicUserData> getUsers() {
        return users;
    }

    public void setUsers(Set<BasicUserData> users) {
        this.users = users;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "BroadcastAvailableUsersPayload{" +
                "users=" + users +
                '}';
    }
}
