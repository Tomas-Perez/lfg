package persistence.model;

import persistence.entity.ChatMessageEntity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class Message {
    private int id;
    private String message;
    private User sender;
    private LocalDateTime date;

    public Message(ChatMessageEntity entity, User sender) {
        this.id = entity.getId();
        this.message = entity.getMessage();
        this.date = entity.getDate();
        this.sender = sender;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public User getSender() {
        return sender;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message1 = (Message) o;
        return id == message1.id &&
                Objects.equals(message, message1.message) &&
                Objects.equals(sender, message1.sender) &&
                Objects.equals(date, message1.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, sender, date);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", sender=" + sender +
                ", date=" + date +
                '}';
    }
}
