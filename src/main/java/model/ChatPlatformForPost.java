package model;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "CHAT_PLATFORM_FOR_POST", schema = "PUBLIC", catalog = "PUBLIC")
@IdClass(ChatPlatformForPostPK.class)
public class ChatPlatformForPost {
    private int postId;
    private int chatPlatformId;
    private ChatPlatform chatPlatformByChatPlatformId;

    @Id
    @Column(name = "POST_ID")
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    @Id
    @Column(name = "CHAT_PLATFORM_ID")
    public int getChatPlatformId() {
        return chatPlatformId;
    }

    public void setChatPlatformId(int chatPlatformId) {
        this.chatPlatformId = chatPlatformId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatPlatformForPost that = (ChatPlatformForPost) o;
        return postId == that.postId &&
                chatPlatformId == that.chatPlatformId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(postId, chatPlatformId);
    }

    @ManyToOne
    @JoinColumn(name = "CHAT_PLATFORM_ID", referencedColumnName = "CHAT_PLATFORM_ID", nullable = false)
    public ChatPlatform getChatPlatformByChatPlatformId() {
        return chatPlatformByChatPlatformId;
    }

    public void setChatPlatformByChatPlatformId(ChatPlatform chatPlatformByChatPlatformId) {
        this.chatPlatformByChatPlatformId = chatPlatformByChatPlatformId;
    }
}
