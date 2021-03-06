package persistence.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "CHAT_MEMBER", schema = "PUBLIC", catalog = "PUBLIC")
@IdClass(ChatMemberEntityPK.class)
public class ChatMemberEntity {
    private int chatId;
    private int memberId;
    private boolean openChat;

    public ChatMemberEntity(int chatId, int memberId) {
        this.chatId = chatId;
        this.memberId = memberId;
        this.openChat = true;
    }

    public ChatMemberEntity() {
    }

    @Id
    @Column(name = "CHAT_ID")
    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    @Id
    @Column(name = "MEMBER_ID")
    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    @Basic
    @Column(name = "OPEN_CHAT", nullable = false)
    public boolean isOpenChat() {
        return openChat;
    }

    public void setOpenChat(boolean openChat) {
        this.openChat = openChat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMemberEntity that = (ChatMemberEntity) o;
        return chatId == that.chatId &&
                memberId == that.memberId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(chatId, memberId);
    }
}
