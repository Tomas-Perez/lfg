package api.websocket.user.model.payload;

import api.websocket.common.model.Payload;

import java.util.Set;

/**
 * Represents the payload of a WebSocket frame to broadcast the available users.
 *
 * @author Tomas Perez Molina
 */
public class BroadcastAvailableUsersPayload implements Payload {

    public static final String TYPE = "broadcastAvailableUsers";

    private Set<Integer> ids;

    public BroadcastAvailableUsersPayload() {}

    public Set<Integer> getIds() {
        return ids;
    }

    public void setIds(Set<Integer> ids) {
        this.ids = ids;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
