package api.websocket.user.model.payload;


import api.rest.chat.model.MessageJSON;
import api.websocket.common.model.Payload;

/**
 * Represents the payload of a WebSocket frame to broadcast a text message.
 *
 * @author Tomas Perez Molina
 */
public class BroadcastTextMessagePayload implements Payload {

    public static final String TYPE = "broadcastTextMessage";

    private int id;
    private String message;
    private int sender;
    private String date;


    public BroadcastTextMessagePayload() {}

    public BroadcastTextMessagePayload(MessageJSON message) {
        this.message = message.getMessage();
        this.id = message.getId();
        this.sender = message.getSender();
        this.date = message.getDate();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
