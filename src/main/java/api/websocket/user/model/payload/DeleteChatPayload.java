package api.websocket.user.model.payload;

import api.websocket.common.model.Payload;

/**
 * Represents the payload of a WebSocket frame to broadcast details of a deleted chat.
 *
 * @author Tomas Perez Molina
 */
public class DeleteChatPayload implements Payload {

    public static final String TYPE = "deleteChat";

    private Integer id;

    public DeleteChatPayload() {}

    public DeleteChatPayload(Integer id) {
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
        return "DeleteChatPayload{" +
                "id=" + id +
                '}';
    }
}
