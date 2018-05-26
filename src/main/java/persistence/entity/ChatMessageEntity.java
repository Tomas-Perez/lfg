package persistence.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "CHAT_MESSAGE", schema = "PUBLIC", catalog = "PUBLIC")
public class ChatMessageEntity {
    private int id;
    private int chatId;
    private int senderId;
    private String message;
    private LocalDateTime date;

    public ChatMessageEntity(int chatId, int senderId, String message, LocalDateTime date) {
        this.chatId = chatId;
        this.senderId = senderId;
        this.message = message;
        this.date = date;
    }

    public ChatMessageEntity() {
    }

    @Id
    @GeneratedValue
    @Column(name = "ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "CHAT_ID")
    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    @Basic
    @Column(name = "SENDER_ID")
    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    @Basic
    @Column(name = "MESSAGE")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Basic
    @Column(name = "DATE")
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessageEntity that = (ChatMessageEntity) o;
        return id == that.id &&
                chatId == that.chatId &&
                senderId == that.senderId &&
                Objects.equals(message, that.message) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, chatId, senderId, message, date);
    }
}
