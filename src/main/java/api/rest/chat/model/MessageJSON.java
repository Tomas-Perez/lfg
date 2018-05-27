package api.rest.chat.model;

import persistence.model.Message;

import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class MessageJSON {
    private int id;
    private int sender;
    private String message;
    private String date;

    public MessageJSON(int id, int sender, String message, String date) {
        this.id = id;
        this.sender = sender;
        this.message = message;
        this.date = date;
    }

    public MessageJSON(Message message) {
        this.id = message.getId();
        this.sender = message.getSender().getId();
        this.message = message.getMessage();
        this.date = message.getDate().toString();
    }

    public MessageJSON() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MessageJSON)) return false;
        MessageJSON that = (MessageJSON) o;
        return id == that.id &&
                sender == that.sender &&
                Objects.equals(message, that.message) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, message, date);
    }

    @Override
    public String toString() {
        return "MessageJSON{" +
                "id=" + id +
                ", sender=" + sender +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
