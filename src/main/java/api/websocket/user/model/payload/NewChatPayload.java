package api.websocket.user.model.payload;

import api.rest.user.model.BasicUserData;
import api.websocket.common.model.Payload;

/**
 * Represents the payload of a WebSocket frame to broadcast details of a new chat.
 *
 * @author Tomas Perez Molina
 */
public class NewChatPayload implements Payload {

    public static final String TYPE = "newChat";

    private Integer id;

    public NewChatPayload() {}

    public NewChatPayload(Integer id) {
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
        return "NewChatPayload{" +
                "id=" + id +
                '}';
    }
}
