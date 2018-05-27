package api.websocket.chat.model.payload;


import api.rest.chat.model.MessageJSON;
import api.websocket.common.model.Payload;

/**
 * Represents the payload of a WebSocket frame to broadcast a text message.
 *
 * @author cassiomolin
 */
public class BroadcastTextMessagePayload implements Payload {

    public static final String TYPE = "broadcastTextMessage";

    private MessageJSON message;

    public BroadcastTextMessagePayload() {}

    public BroadcastTextMessagePayload(MessageJSON message) {
        this.message = message;
    }

    public MessageJSON getMessage() {
        return message;
    }

    public void setMessage(MessageJSON message) {
        this.message = message;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
