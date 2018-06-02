package api.websocket.user.model.payload;

import api.websocket.common.model.Payload;

/**
 * Represents the payload of a WebSocket frame to broadcast details of a deleted friend.
 *
 * @author Tomas Perez Molina
 */
public class DeleteFriendPayload implements Payload {

    public static final String TYPE = "deleteFriend";

    private Integer id;

    public DeleteFriendPayload() {}

    public DeleteFriendPayload(Integer id) {
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
        return "DeleteFriendPayload{" +
                "id=" + id +
                '}';
    }
}
