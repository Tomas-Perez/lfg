package api.websocket.user.model.payload;

import api.rest.user.model.BasicUserData;
import api.websocket.common.model.Payload;

/**
 * Represents the payload of a WebSocket frame to broadcast details of a sent friend request.
 *
 * @author Tomas Perez Molina
 */
public class SentFriendRequestPayload implements Payload {

    public static final String TYPE = "sentFriendRequest";

    private Integer id;
    private String username;

    public SentFriendRequestPayload() {}

    public SentFriendRequestPayload(Integer id, String username) {
        this.id = id;
        this.username = username;
    }

    public SentFriendRequestPayload(BasicUserData data) {
        this.id = data.getId();
        this.username = data.getUsername();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "SentFriendRequestPayload{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
