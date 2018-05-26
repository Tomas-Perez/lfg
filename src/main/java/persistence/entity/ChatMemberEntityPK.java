package persistence.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
public class ChatMemberEntityPK implements Serializable {
    private int chatId;
    private int memberId;

    public ChatMemberEntityPK(int chatId, int memberId) {
        this.chatId = chatId;
        this.memberId = memberId;
    }

    public ChatMemberEntityPK() {
    }

    @Column(name = "CHAT_ID")
    @Id
    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    @Column(name = "MEMBER_ID")
    @Id
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMemberEntityPK that = (ChatMemberEntityPK) o;
        return chatId == that.chatId &&
                memberId == that.memberId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(chatId, memberId);
    }
}
