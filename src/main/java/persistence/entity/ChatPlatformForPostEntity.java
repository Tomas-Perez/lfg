package persistence.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author Tomas Perez Molina
 */
@Entity
@Table(name = "CHAT_PLATFORM_FOR_POST", schema = "PUBLIC", catalog = "PUBLIC")
@IdClass(ChatPlatformForPostEntityPK.class)
public class ChatPlatformForPostEntity {
    private int postId;
    private int chatPlatformId;

    @Id
    @Column(name = "POST_ID", nullable = false)
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    @Id
    @Column(name = "CHAT_PLATFORM_ID", nullable = false)
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
        ChatPlatformForPostEntity that = (ChatPlatformForPostEntity) o;
        return postId == that.postId &&
                chatPlatformId == that.chatPlatformId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(postId, chatPlatformId);
    }
}
