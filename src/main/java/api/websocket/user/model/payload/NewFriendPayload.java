package api.websocket.user.model.payload;

import api.rest.user.model.BasicUserData;
import api.websocket.common.model.Payload;

/**
 * Represents the payload of a WebSocket frame to broadcast details of a new friend.
 *
 * @author Tomas Perez Molina
 */
public class NewFriendPayload implements Payload {

    public static final String TYPE = "newFriend";

    private Integer id;
    private String username;

    public NewFriendPayload() {}

    public NewFriendPayload(Integer id, String username) {
        this.id = id;
        this.username = username;
    }

    public NewFriendPayload(BasicUserData data) {
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
        return "NewFriendPayload{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
