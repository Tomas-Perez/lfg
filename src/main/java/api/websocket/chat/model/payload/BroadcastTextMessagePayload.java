package api.websocket.chat.model.payload;


import api.websocket.common.model.Payload;

/**
 * Represents the payload of a WebSocket frame to broadcast a text message.
 *
 * @author cassiomolin
 */
public class BroadcastTextMessagePayload implements Payload {

    public static final String TYPE = "broadcastTextMessage";

    private Integer id;
    private String content;

    public BroadcastTextMessagePayload() {}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
