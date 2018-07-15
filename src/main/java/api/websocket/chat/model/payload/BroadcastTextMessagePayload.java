package api.websocket.chat.model.payload;

import api.rest.user.model.BasicUserData;
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
    private BasicUserData sender;
    private String date;


    public BroadcastTextMessagePayload() {}

    public BroadcastTextMessagePayload(String message, int id, BasicUserData sender, String date) {
        this.message = message;
        this.id = id;
        this.sender = sender;
        this.date = date;
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

    public BasicUserData getSender() {
        return sender;
    }

    public void setSender(BasicUserData sender) {
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

    @Override
    public String toString() {
        return "BroadcastTextMessagePayload{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", sender=" + sender +
                ", date='" + date + '\'' +
                '}';
    }
}
